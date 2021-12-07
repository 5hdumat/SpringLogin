package hello.login.web.session;

import hello.login.domain.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.*;


class SessionManagerTest {
    SessionManager sessionManager = new SessionManager();

    @Test
    void SessionTest() {
        // 여기서는 HttpServletRequest , HttpservletResponse 객체를 직접 사용할 수 없기 때문에 테스트에서 비슷한 역할을 해주는
        // 가짜 MockHttpServletRequest , MockHttpServletResponse 를 사용했다.
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 세션 생성 (서버 -> 클라이언트)
        Member member = new Member("test", "테스터", "test!");
        sessionManager.createSession(member, response);

        // 요청 쿠키 전송 (클라이언트 -> 서버)
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(response.getCookies());

        // 세선 조회 테스트
        Object result = sessionManager.getSession(request);
        assertThat(result).isEqualTo(member);

        // 세선 만료 테스트
        sessionManager.expire(request);
        Object expired = sessionManager.getSession(request);

        assertThat(expired).isNull();
    }
}