package ai.synact.synact.services;

import ai.synact.synact.entities.SocialAccount;
import ai.synact.synact.entities.User;
import ai.synact.synact.repositories.SocialAccountRepository;
import ai.synact.synact.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepo;
    private final SocialAccountRepository socialRepo;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req)
            throws OAuth2AuthenticationException {
        OAuth2User oauth = super.loadUser(req);
        String provider  = req.getClientRegistration().getRegistrationId();
        String providerId = oauth.getAttribute("id").toString();

        SocialAccount account = socialRepo
                .findByProviderAndProviderUserId(provider, providerId)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(oauth.getAttribute("email"))
                            .passwordHash(null)
                            .enabled(true)
                            .build();
                    newUser = userRepo.save(newUser);

                    SocialAccount sa = SocialAccount.builder()
                            .user(newUser)
                            .provider(provider)
                            .providerUserId(providerId)
                            .build();
                    return socialRepo.save(sa);
                });

        var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        Map<String,Object> attrs = new HashMap<>(oauth.getAttributes());
        attrs.put("appUserId", account.getUser().getId());

        return new DefaultOAuth2User(authorities, attrs, "name");
    }
}

