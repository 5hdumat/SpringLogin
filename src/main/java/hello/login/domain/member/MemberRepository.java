package hello.login.domain.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.util.*;
import java.util.List;

@Slf4j
@Repository
public class MemberRepository {

    // static
    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    public Member save(Member member) {
        member.setId(++sequence);

        log.info("save: member = {}", member);

        store.put(member.getId(), member);
        return member;
    }

    public Member findById(Long id) {
        return store.get(id);
    }

    public Optional<Member> findByLoginId(String loginId) {
        //        List<Member> all = findAll();
        //        for (Member m : all) {
        //            if (m.getLoginId().equals(loginId)) {
        //                return Optional.of(m);
        //            }
        //        }
        //
        //        return Optional.empty();

        /**
         * 위 코드를 람다로 축약할 수 있다.
         *
         * 1. 리스트를 스트림으로 변환
         * 2. 필터로 조건에 만족하는 데이터만 다음 단계로 넘어감
         * 3. 가장 먼저 나온 데이터 리턴
         */
        return findAll().stream()
                .filter(m -> m.getLoginId().equals(loginId))
                .findFirst();
    }

    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public void clearStore() {
        store.clear();
    }
}
