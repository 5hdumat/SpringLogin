package hello.login.web.intersepter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
@Component
public class LogInterseptor implements HandlerInterceptor {

    public static final String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        request.setAttribute(LOG_ID, uuid);

        /**
         * 핸들러 정보는 어떤 핸들러 매핑을 사용하는가에 따라 달라진다.
         * 스프링을 사용하면 일반적으로 @Controller, @RequestMapping 을 활용한 핸들러 매핑을 사용하는데,
         * 이 경우 핸들러 정보로 HandlerMethod 가 넘어온다.
         *
         * @Controller가 아니라 /resources/static 와 같은 정적 리소스가 호출 되는 경우 ResourceHttpRequestHandler 가
         * 핸들러 정보로 넘어오기 때문에 타입에 따라서 분기 처리가 필요하다.
         */
        // @RequestMapping을 사용하는 경우 스프링은 HandlerMethod 핸들러를 사용한다.
        // 정적 리소스를 사용하는 경우 스프링은 ResourceHttpRequestHandler 핸들러를 사용한다.
        if (handler instanceof HandlerMethod) {
            // 호출할 컨트롤러 메서드의 모든 정보가 포함되어 있다.
            HandlerMethod hm = (HandlerMethod) handler;
        }

        log.info("REQUEST [{}][{}][{}]", uuid, requestURI, handler);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle [{}]", modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = (String) request.getAttribute(LOG_ID);
        log.info("RESPONSE [{}][{}][{}]", uuid, requestURI, handler);

        if (ex != null) {
            // 오류로그는 {} 생략 가능
            log.error("afterCompletion error!!", ex);
        }
    }
}
