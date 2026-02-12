package hotspot.user.member.controller.request;

import hotspot.user.member.domain.Provider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * SocialAccount 요청 Dto
 */
public record CreateSocialAccountRequest(
    String email,
    String socialId,
    Provider provider
) {
}
