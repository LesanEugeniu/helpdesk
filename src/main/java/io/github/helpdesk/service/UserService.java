package io.github.helpdesk.service;

import io.github.helpdesk.exception.RestErrorResponseException;
import io.github.helpdesk.model.User;
import io.github.helpdesk.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static io.github.helpdesk.exception.ErrorType.USER_NOT_FOUND;
import static io.github.helpdesk.exception.ProblemDetailBuilder.forStatusAndDetail;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

}
