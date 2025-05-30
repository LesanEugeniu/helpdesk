package io.github.helpdesk.dto;

import io.github.helpdesk.dto.request.AuthenticationRequestDto;
import io.github.helpdesk.dto.request.EmailVerificationRequestDto;
import io.github.helpdesk.model.Category;
import io.github.helpdesk.model.User;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapStructMapper {


    List<CategoryDto> mapTo(List<Category> categories);

    CategoryDto mapTo(Category category);

    Category mapTo(CategoryDto categoryDto);

    @Mapping(target = "id", ignore = true)
    void updateCategory(CategoryDto categoryDto, @MappingTarget Category categoryFromDB);

    AuthenticationRequestDto mapTo(@Valid EmailVerificationRequestDto verificationRequestDto);

    UserDto mapTo(User user);

    void updateUser(UserDto userDto, @MappingTarget User user);

}
