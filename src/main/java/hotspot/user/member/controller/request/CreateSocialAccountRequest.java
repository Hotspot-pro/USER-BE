package hotspot.user.member.controller.request;


import hotspot.user.member.domain.Provider;

/**
 * SocialAccount 요청 Dto
 */
public record CreateSocialAccountRequest(
    String email,
    String socialId,
    Provider provider,
    Long memberId
) {
}
