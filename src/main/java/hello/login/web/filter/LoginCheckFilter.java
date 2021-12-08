package hello.login.web.filter;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class LoginCheckFilter implements Filter {

    private static final String[] whitelist = {"/", "/members/add", "/login", "/logout", "/css/*"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            log.info("인증 체크 필터 시작 {}", requestURI);

            if (isLoginCheckPath(requestURI)) {
                log.info("인증 체크 로직 실행 {}", requestURI);

                HttpSession session = httpRequest.getSession(false);

                if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
                    log.info("미인증 사용자 요청 {}", requestURI);

                    /**
                     * 미인증 사용자는 로그인 화면으로 리다이렉트 한다.
                     * 그런데 로그인 이후에 다시 홈으로 이동해버리면, 원하는 경로를 다시 찾아가야 하는 불편함이 있다.
                     * 예를 들어서 상품 관리 화면을 보려고 들어갔다가 로그인 화면으로 이동하면, 로그인 이후에 다시 상품 관리 화면으로 들어가는 것이 좋다.
                     * 이런 부분이 개발자 입장에서는 좀 귀찮을 수 있어도 사용자 입장으로 보면 편리한 기능이다.
                     * 이러한 기능을 위해 현재 요청한 경로인 requestURI 를 /login 에 쿼리 파라미터로 함께 전달한다.
                     * 물론 /login 컨트롤러에서 로그인 성공시 해당 경로로 이동하는 기능은 추가로 개발해야 한다.
                     */
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI);

                    // 여기가 중요하다. 필터를 더는 진행하지 않는다. 이후 필터는 물론 서블릿, 컨트롤러까지 더 이상 호출되지 않도록 한다.
                    return;
                }
            }

            chain.doFilter(request, response);

        } catch (Exception e) {
            // 예외 로깅도 가능 하지만, 톰캣(WAS) 까지 예외를 보내주어야 한다.
            // 지금 로직에서 예외를 먹어버리면 정상 동작 처럼 보이기 때문이다.
            throw e;

        } finally {
            log.info("인증 체크 필터 종료");
        }
    }

    /**
     * 화이트 리스트를 제외한 모든 경우에 인증 체크 로직을 적용한다.
     */
    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }
}
