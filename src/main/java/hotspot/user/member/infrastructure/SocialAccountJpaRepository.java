package hotspot.user.member.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotspot.user.member.infrastructure.entity.SocialAccountEntity;

@Repository
public interface SocialAccountJpaRepository extends JpaRepository<SocialAccountEntity, Long> {
    Optional<SocialAccountEntity> findByEmail(String email);
    List<SocialAccountEntity> findAllByMemberId(Long memberId);
}
