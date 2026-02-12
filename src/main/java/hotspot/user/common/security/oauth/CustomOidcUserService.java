package hotspot.user.common.security.oauth;

import java.util.Map;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import hotspot.user.common.security.PrincipalDetails;
import hotspot.user.member.domain.FamilyRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * OIDC ID Token만을 사용하여 로그인 처리 (UserInfo API 호출 최적화)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // ID Token 가져오기 (Spring Security가 이미 서명 검증 및 유효성 체크를 완료한 상태)
        OidcIdToken idToken = userRequest.getIdToken();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // ID Token 내부의 정보(Claims) 추출
        Map<String, Object> claims = idToken.getClaims();

        // 로그: 실제 들어온 클레임 확인용
        log.debug("[OIDC] ID Token Claims: {}", claims);

        String email = null;
        String name = null; // 필요 시 사용

        // Provider별 Claim 파싱 (표준 OIDC 및 카카오 커스텀)
        if ("kakao".equals(registrationId)) {
            email = (String) claims.get("email");
            name = (String) claims.get("nickname");
        } else if ("google".equals(registrationId)) {
            // 구글: 표준 클레임 사용
            email = (String) claims.get("email");
            name = (String) claims.get("name");
        } else {
            // 기타 OIDC Provider (표준 따름)
            email = (String) claims.get("email");
        }

        // 필수 정보 누락 체크
        if (email == null) {
            log.error("[OIDC] ID Token에 이메일이 없습니다. Provider 설정을 확인하세요. Claims: {}", claims);
            throw new OAuth2AuthenticationException("ID Token에 이메일 정보가 누락되었습니다."); // 공통 에러 코드 필요
        }

        // 비즈니스 로직 (회원가입/갱신)
        // [TODO] Member 모듈의 서비스(RegisterSocialMemberUseCase)를 호출하여 가입/갱신 처리
        // Member member = memberService.findOrRegister(email, registrationId);

        log.info("[OIDC] 로그인 성공 (UserInfo 호출 Skip): provider={}, email={}", registrationId, email);

        // 5. PrincipalDetails 반환
        return new PrincipalDetails(
                1L, // member.getId()
                email,
                FamilyRole.CHILD, // member.getRole()
                claims // attributes 자리에 claims 저장
        );
    }
}
