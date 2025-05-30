package io.github.helpdesk.service;

import io.github.helpdesk.dto.MapStructMapper;
import io.github.helpdesk.dto.UserDto;
import io.github.helpdesk.exception.RestErrorResponseException;
import io.github.helpdesk.model.User;
import io.github.helpdesk.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static io.github.helpdesk.exception.ErrorType.USER_NOT_FOUND;
import static io.github.helpdesk.exception.ProblemDetailBuilder.forStatusAndDetail;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final MapStructMapper mapper;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, MapStructMapper mapper,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserByEmail(String email) throws RestErrorResponseException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RestErrorResponseException(
                        forStatusAndDetail(HttpStatus.NOT_FOUND, "User not found")
                                .withProperty("email", email)
                                .withErrorType(USER_NOT_FOUND)
                                .build()
                ));
    }

    public User getUserByUserName(String userName) throws RestErrorResponseException {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new RestErrorResponseException(
                        forStatusAndDetail(HttpStatus.NOT_FOUND, "User not found")
                                .withProperty("userName", userName)
                                .withErrorType(USER_NOT_FOUND)
                                .build()
                ));
    }

    public UserDto getUserDtoByUserName(String userName) {
        return mapper.mapTo(getUserByUserName(userName));
    }

    @Transactional
    public void fullUpdate(String userName, UserDto userDto) {
        User user = getUserByUserName(userName);
        mapper.updateUser(userDto, user);
        userRepository.save(user);
    }

    public String resetPassword(String email, String password) {
        User user = getUserByEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        return user.getUserName();
    }

}
