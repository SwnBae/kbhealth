package kb.health.authentication;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kb.health.Exception.LoginException;
import kb.health.Service.MemberService;
import kb.health.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtUtil jwtUtil;
    private final MemberService memberService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(LoginMember.class) != null && parameter.getParameterType().equals(CurrentMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class); // 응답 객체 필요

        if (request == null || response == null) {
            throw LoginException.loginProcessNeeded();
        }

        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }

        if (token == null || !jwtUtil.validateJwtToken(token)) {
            // 쿠키 삭제
            Cookie jwtCookie = new Cookie("jwt", null);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(0); // 즉시 삭제
            response.addCookie(jwtCookie);

            throw LoginException.loginProcessNeeded();
        }

        // 정상 처리...
        CurrentMember currentMember = new CurrentMember(jwtUtil.getIdFromJwt(token), jwtUtil.getAccountFromJwt(token));
        Member member = memberService.findMemberByAccount(currentMember.account);
        if (member == null) {
            throw LoginException.loginProcessNeeded();
        }

        return currentMember;
    }
}
