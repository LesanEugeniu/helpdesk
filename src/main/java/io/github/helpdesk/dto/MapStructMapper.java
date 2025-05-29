package io.github.helpdesk.dto;

import io.github.helpdesk.dto.request.AuthenticationRequestDto;
import io.github.helpdesk.dto.request.EmailVerificationRequestDto;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapStructMapper {


    AuthenticationRequestDto mapTo(@Valid EmailVerificationRequestDto verificationRequestDto);

}
