package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

//    @GetMapping("/")
    public String home() {
        return "/home";
    }

//    @GetMapping("/")
    public String homeLogin(@CookieValue(name="memberId", required = false) Long memberId, Model model) {
        // 로그인 쿠키( memberId )가 없는 사용자는 기존 home 으로 보낸다.
        if (memberId == null) {
            return "/home";
        }

        // 추가로 로그인 쿠키가 있어도 회원이 존재하지 않는다면 홈으로 보낸다.
        Member loginMember = memberRepository.findById(memberId);
        if (loginMember == null) {
            return "/hoqme";
        }

        // 로그인 쿠키(memberId)가 있는 사용자는 로그인 사용자 전용 홈 화면인 loginHome 으로 보낸다.
        // 추가로 홈 화면에 화원 관련 정보도 출력해야 해서 member 데이터도 모델에 담아서 전달한다.
        model.addAttribute("member", loginMember);
        return "/loginHome";
    }

    @GetMapping("/")
    public String homeLoginV2(HttpServletRequest request, Model model) {
        // 세션 관리자에 저장된 회원 정보 조회 (캐스팅 해줘야 한다.)
        Member member = (Member) sessionManager.getSession(request);

        // 로그인 쿠키(memberId)가 없는 사용자는 기존 home 으로 보낸다.
        if (member == null) {
            return "/home";
        }

        model.addAttribute("member", member);
        return "/loginHome";
    }
}