package hello.login.web.argumentresolver;

import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * ArgumentResolver 의 supportsParameter() 를 호출해서 해당 파라미터를 지원하는지 체크하고,
 * 지원하면 resolveArgument() 를 호출해서 실제 객체를 생성한다.
 * 그리고 이렇게 생성된 객체가 컨트롤러 호출시 넘어가는 것이다.
 */
@Slf4j
@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * @Login 애노테이션이 있으면서 Member 타입이면 해당 ArgumentResolver 가 사용된다.
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("supportsParameter 실행");

        boolean hasParameterAnnotation = parameter.hasParameterAnnotation(Login.class);
        boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType());

        // true 면 resolveArgument가 호출된다.
        return hasParameterAnnotation && hasMemberType;
    }

    /**
     * 컨트롤러 호출 직전에 호출 되어서 필요한 파라미터 정보를 생성해준다.
     * 여기서는 세션에 있는 로그인 회원 정보인 member 객체를 찾아서 반환해준다.
     * 이후 스프링MVC는 컨트롤러의 메서드를 호출하면서 여기에서 반환된 member 객체를 파라미터에 전달해준다.
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        log.info("resolveArgument 실행");

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request.getSession();

        if (session == null) {
            return null;
        }

        return session.getAttribute(SessionConst.LOGIN_MEMBER);
    }
}
