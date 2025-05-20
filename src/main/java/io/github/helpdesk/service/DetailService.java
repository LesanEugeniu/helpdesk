package io.github.helpdesk.service;

import io.github.helpdesk.exception.RestErrorResponseException;
import io.github.helpdesk.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static io.github.helpdesk.exception.ErrorType.EMAIL_VERIFICATION_REQUIRED;
import static io.github.helpdesk.exception.ProblemDetailBuilder.forStatusAndDetail;

@Service
public class DetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public DetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserName(username).map(user -> {
            if (!user.isEmailVerified()) {
                throw new RestErrorResponseException(
                        forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Email verification required")
                                .withProperty("email", user.getEmail())
                                .withErrorType(EMAIL_VERIFICATION_REQUIRED)
                                .build()
                );
            }
            return User.builder()
                    .username(username)
                    .password(user.getPassword())
                    .disabled(!user.isEmailVerified())
                    .authorities(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                    .build();
        }).orElseThrow(() -> new UsernameNotFoundException("User with username [%s] not found".formatted(username)));
    }

}
