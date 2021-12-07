package hello.login.web.session;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 세션 관리
 */
@Component
public class SessionManager {

    public static final String SESSION_COOKIE_NAME = "mySessionId";
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>();

    /**
     * 세션 생성
     * 1. sessionId 생성 (임의의 추정 불가능한 랜덤 값)
     * 2. 세션 저장소에 sessionId와 보관할 값 저장
     * 3. sessionId로 응답 쿠키를 생성
     * 4. 클라이언트에 전달
     */
    public void createSession(Object value, HttpServletResponse response) {
        // 1. sessionId 생성 (임의의 추정 불가능한 랜덤 값)
        String sessionId = UUID.randomUUID().toString();

        // 2. 세션 저장소에 sessionId와 보관할 값 저장
        sessionStore.put(sessionId, value);

        // 3. sessionId로 응답 쿠키를 생성
        Cookie mySessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);

        // 4. 클라이언트에 전달
        response.addCookie(mySessionCookie);
    }

    /**
     * 세션 조회
     */
    public Object getSession(HttpServletRequest request) {
        Cookie cookie = findCookie(request, SESSION_COOKIE_NAME);

        if (cookie == null) {
            return null;
        }

        return sessionStore.get(cookie.getValue());
    }

    /**
     * 세션 만료
     */
    public void expire(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if (sessionCookie != null) {
            sessionStore.remove(sessionCookie.getValue());
        }
    }
    
    public Cookie findCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .orElse(null);
    }

}
