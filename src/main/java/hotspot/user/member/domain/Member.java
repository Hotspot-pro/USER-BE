package hotspot.user.member.domain;

import hotspot.user.member.controller.request.CreateMemberRequest;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class Member {
    private Long id; // DB 저장 전에는 null, 저장 후 ID 할당됨
    private String name;
    private String birth;
    private Status status;
    private List<SocialAccount> socialAccountList; // email 존재
    // private Subscription subscription; [To-Do] subscription 패키지 생성 (phone, role 존재)

    public Member(String name, String birth, Status status) {
        this.name = name;
        this.birth = birth;
        this.status = status;
    }

    public static Member create(CreateMemberRequest request) {
        return Member.builder()
                .name(request.name())
                .birth(request.birth())
                .status(Status.PENDING)
                .build();
    }
}