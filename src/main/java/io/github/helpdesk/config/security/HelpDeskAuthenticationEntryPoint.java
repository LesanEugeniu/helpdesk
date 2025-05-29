package io.github.helpdesk.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.helpdesk.exception.ErrorType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static io.github.helpdesk.exception.ProblemDetailBuilder.forStatus;

@Component
public final class HelpDeskAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final static Logger log = LoggerFactory.getLogger(HelpDeskAuthenticationEntryPoint.class);

    private final ObjectMapper objectMapper;

    public HelpDeskAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException)
            throws IOException {
        final var status = HttpStatus.UNAUTHORIZED;

        log.info("{}: {}", status.getReasonPhrase(), authException.getMessage());

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), forStatus(status).withErrorType(ErrorType.UNAUTHORIZED).build());
    }
}
