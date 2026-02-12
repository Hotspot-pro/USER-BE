package hotspot.user.member.infrastructure.entity;

import hotspot.user.common.BaseEntity;
import hotspot.user.member.domain.Provider;
import jakarta.persistence.*;
import lombok.*;

/**
 * SocialAccountEntity
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SocialAccountEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @Column(length = 20, nullable = false)
    private String email;

    @Column(nullable = false)
    private String socialId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
}
