package hotspot.user.member.infrastructure;

import hotspot.user.member.domain.Member;
import hotspot.user.member.domain.SocialAccount;
import hotspot.user.member.infrastructure.entity.MemberEntity;
import hotspot.user.member.infrastructure.entity.SocialAccountEntity;
import hotspot.user.member.service.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;
    private final SocialAccountJpaRepository socialAccountJpaRepository;

    @Override
    @Transactional
    public Member save(Member member) {
        // 1. Member 저장
        MemberEntity memberEntity = MemberEntity.domainToEntity(member);
        MemberEntity savedMemberEntity = memberJpaRepository.save(memberEntity);

        // 2. SocialAccount 저장 (Member ID 연결)
        if (member.getSocialAccountList() != null) {
            List<SocialAccountEntity> socialAccountEntities = member.getSocialAccountList().stream()
                    .map(socialAccount -> SocialAccountEntity.domainToEntity(socialAccount, savedMemberEntity))
                    .collect(Collectors.toList());
            
            socialAccountJpaRepository.saveAll(socialAccountEntities);
        }

        // 3. 다시 도메인으로 변환하여 반환 (완전한 객체 구성을 위해 다시 조회)
        return findById(savedMemberEntity.getId()).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        // 1. SocialAccount에서 이메일로 검색
        Optional<SocialAccountEntity> socialAccountEntity = socialAccountJpaRepository.findByEmail(email);

        if (socialAccountEntity.isEmpty()) {
            return Optional.empty();
        }

        // 2. 연결된 MemberEntity 가져오기
        MemberEntity memberEntity = socialAccountEntity.get().getMember();

        // 3. 전체 도메인 객체 조립 (Member + SocialAccounts)
        return findById(memberEntity.getId());
    }

    // 내부적으로 사용하거나 추후 인터페이스에 추가될 수 있는 메서드
    public Optional<Member> findById(Long id) {
        Optional<MemberEntity> memberEntityOpt = memberJpaRepository.findById(id);

        if (memberEntityOpt.isEmpty()) {
            return Optional.empty();
        }

        MemberEntity memberEntity = memberEntityOpt.get();
        List<SocialAccountEntity> socialAccounts = socialAccountJpaRepository.findAllByMemberId(id);

        List<SocialAccount> socialAccountDomains = socialAccounts.stream()
                .map(SocialAccountEntity::entityToDomain)
                .collect(Collectors.toList());

        return Optional.of(memberEntity.entityToDomain(socialAccountDomains));
    }
}
