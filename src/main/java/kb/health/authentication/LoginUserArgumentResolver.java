package kb.health.authentication;


import io.jsonwebtoken.Header;
import jakarta.servlet.http.HttpServletRequest;
import kb.health.Exception.LoginNeededException;
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
        String authHeader = null;
        if (request != null) {
            authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.replace("Bearer ", "");

                if(jwtUtil.validateJwtToken(token)) {
                    CurrentMember currentMember = new CurrentMember(jwtUtil.getIdFromJwt(token), jwtUtil.getAccountFromJwt(token));
                    try{
                        Member member = memberService.findMemberByAccount(currentMember.account);
                        if (member == null) {
                            throw LoginNeededException.loginProcessNeeded();
                        }
                        return currentMember;
                    } catch (Exception e){
                        throw LoginNeededException.loginProcessNeeded();
                    }
                }
            }
        }
        return null;
    }
}
