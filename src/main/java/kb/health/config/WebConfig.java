package kb.health.config;

import kb.health.service.MemberService;
import kb.health.authentication.JwtUtil;
import kb.health.authentication.LoginUserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtUtil jwtUtil;
    private final MemberService memberService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/images/**")
                // ① 실제 업로드된 파일을 담는 디렉토리
                .addResourceLocations("file:images/")
                // ② src/main/resources/static/images/... 의 기본 이미지도 함께 매핑
                .addResourceLocations("classpath:/static/images/");
    }



    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginUserArgumentResolver(jwtUtil, memberService));
    }
}
