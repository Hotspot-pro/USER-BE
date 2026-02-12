package hotspot.user.member.infrastructure.entity;

import hotspot.user.common.BaseEntity;
import hotspot.user.member.domain.Status;
import jakarta.persistence.*;
import lombok.*;

/**
 * MemberEntity
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 6)
    private String birth;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.PENDING;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
}
