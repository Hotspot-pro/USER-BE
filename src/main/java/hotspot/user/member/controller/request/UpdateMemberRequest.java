package hotspot.user.member.controller.request;

import hotspot.user.member.domain.FamilyRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateMemberRequest(
    String name,
    String email,
    String phone,
    String birth
) {}