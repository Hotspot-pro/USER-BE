package hotspot.user.member.service.port;

import hotspot.user.member.domain.Member;

import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findByEmail(String email);
}
