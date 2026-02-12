package hotspot.user.member.infrastructure;

import hotspot.user.member.infrastructure.entity.SocialAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialAccountJpaRepository extends JpaRepository<SocialAccountEntity, Long> {
}
