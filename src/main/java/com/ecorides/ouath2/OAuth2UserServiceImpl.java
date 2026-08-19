package com.ecorides.ouath2;

import com.ecorides.domain.AuthProvider;
import com.ecorides.domain.UserRole;
import com.ecorides.entity.User;
import com.ecorides.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {

        OAuth2User oauthUser = super.loadUser(request);

        String email = oauthUser.getAttribute("email");
        String firstName = oauthUser.getAttribute("given_name");
        String lastName = oauthUser.getAttribute("family_name");

        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException(new OAuth2Error("email_not_found"), "Email not provided by Google");
        }

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setFirstName(firstName != null && !firstName.isBlank() ? firstName : "Google");
            user.setLastName(lastName != null ? lastName : "");
            user.setPhone(null);
            user.setAddress(null);
            user.setUserRole(UserRole.USER);
            user.setProvider(AuthProvider.GOOGLE);
            user.setIsActive(true);
            user.setIsVerified(true);
            user.setPassword(null);

            userRepository.save(user);
        }

        return oauthUser;
    }
}