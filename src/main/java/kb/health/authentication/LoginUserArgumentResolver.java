package kb.health.authentication;


import jakarta.servlet.http.HttpServletRequest;
import kb.health.Exception.LoginException;
import kb.health.Exception.MemberException;
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
        if (request == null) {
            throw LoginException.loginProcessNeeded();
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw LoginException.loginProcessNeeded();
        }

        String token = authHeader.replace("Bearer ", "");
        if (!jwtUtil.validateJwtToken(token)) {
            throw LoginException.loginProcessNeeded();
        }

        CurrentMember currentMember = new CurrentMember(jwtUtil.getIdFromJwt(token), jwtUtil.getAccountFromJwt(token));

        Member member = null;

        try{
            member = memberService.findMemberByAccount(currentMember.account);
        } catch (MemberException memberException){
            throw LoginException.loginProcessNeeded();
        }

        if (member == null) {
            throw LoginException.loginProcessNeeded();
        }

        return currentMember;
    }
}
