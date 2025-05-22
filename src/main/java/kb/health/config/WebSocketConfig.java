package kb.health.config;

import kb.health.authentication.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트가 구독할 수 있는 경로 설정
        config.enableSimpleBroker("/topic", "/queue");
        // 클라이언트가 메시지를 보낼 수 있는 경로 설정
        config.setApplicationDestinationPrefixes("/app");
        // 사용자별 개인 메시지 경로 설정
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // 개발 환경용, 운영에서는 구체적인 도메인 설정
                .addInterceptors(new HttpSessionHandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request,
                                                   ServerHttpResponse response,
                                                   WebSocketHandler wsHandler,
                                                   Map<String, Object> attributes) throws Exception {
                        // HTTP 요청을 세션에 저장
                        if (request instanceof ServletServerHttpRequest) {
                            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                            HttpServletRequest httpRequest = servletRequest.getServletRequest();

                            // 쿠키에서 JWT 추출하여 세션에 저장
                            String jwtToken = null;
                            Cookie[] cookies = httpRequest.getCookies();
                            if (cookies != null) {
                                for (Cookie cookie : cookies) {
                                    if ("jwt".equals(cookie.getName())) {
                                        jwtToken = cookie.getValue();
                                        break;
                                    }
                                }
                            }

                            // JWT 토큰을 세션 속성에 저장
                            if (jwtToken != null) {
                                attributes.put("JWT_TOKEN", jwtToken);
                                System.out.println("핸드셰이크 시 JWT 토큰 저장 완료");
                            } else {
                                System.out.println("핸드셰이크 시 JWT 토큰 없음");
                            }
                        }
                        return super.beforeHandshake(request, response, wsHandler, attributes);
                    }
                })
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                try {
                    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                        System.out.println("WebSocket 연결 시도 시작");

                        // JWT 토큰 추출
                        String jwtToken = extractJwtFromCookies(accessor);
                        System.out.println("JWT 토큰 추출: " + (jwtToken != null ? "성공" : "실패"));

                        if (jwtToken != null && jwtUtil.validateJwtToken(jwtToken)) {
                            // JWT에서 사용자 정보 추출
                            String account = jwtUtil.getAccountFromJwt(jwtToken);
                            Long userId = jwtUtil.getIdFromJwt(jwtToken);

                            // Principal 설정 (사용자 식별용)
                            accessor.setUser(new WebSocketPrincipal(userId, account));

                            System.out.println("WebSocket 인증 성공: userId=" + userId + ", account=" + account);
                        } else {
                            System.out.println("WebSocket 인증 실패: JWT 토큰이 유효하지 않음");
                            // 인증 실패 시에도 연결은 허용하되, 사용자 정보만 없는 상태로 처리
                            // return null; // 이 부분을 주석 처리하여 연결 자체는 허용
                        }
                    }
                } catch (Exception e) {
                    System.err.println("WebSocket 인증 처리 중 오류 발생: " + e.getMessage());
                    e.printStackTrace();
                    // 예외 발생 시에도 연결은 허용
                }

                return message;
            }
        });
    }

    // 쿠키에서 JWT 토큰 추출 (핸드셰이크 시 저장된 토큰 사용)
    private String extractJwtFromCookies(StompHeaderAccessor accessor) {
        try {
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            if (sessionAttributes != null) {
                // 핸드셰이크 시 저장된 JWT 토큰 사용
                String jwtToken = (String) sessionAttributes.get("JWT_TOKEN");
                if (jwtToken != null) {
                    System.out.println("저장된 JWT 토큰 사용");
                    return jwtToken;
                }
            }
            System.out.println("저장된 JWT 토큰 없음");
        } catch (Exception e) {
            System.err.println("JWT 토큰 추출 중 예외 발생: " + e.getMessage());
        }
        return null;
    }

    // WebSocket Principal 구현
    @Getter
    @AllArgsConstructor
    public static class WebSocketPrincipal implements Principal {
        private final Long userId;
        private final String account;

        @Override
        public String getName() {
            return userId.toString(); // SimpMessagingTemplate에서 사용자 식별용
        }
    }
}