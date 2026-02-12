package hotspot.user.member.infrastructure.entity;

import hotspot.user.common.BaseEntity;
import hotspot.user.member.domain.Provider;
import hotspot.user.member.domain.SocialAccount;
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

    @Column(length = 50, nullable = false)
    private String email;

    @Column(nullable = false)
    private String socialId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    public static SocialAccountEntity domainToEntity(SocialAccount socialAccount, MemberEntity memberEntity) {
        return SocialAccountEntity.builder()
                .id(socialAccount.getId())
                .member(memberEntity)
                .email(socialAccount.getEmail())
                .socialId(socialAccount.getSocialId())
                .provider(socialAccount.getProvider())
                .isDeleted(false)
                .build();
    }

    public SocialAccount entityToDomain() {
        return SocialAccount.builder()
                .id(this.id)
                .memberId(this.member.getId())
                .email(this.email)
                .socialId(this.socialId)
                .provider(this.provider)
                .build();
    }
}
