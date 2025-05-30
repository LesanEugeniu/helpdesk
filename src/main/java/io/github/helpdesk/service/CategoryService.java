package io.github.helpdesk.service;

import io.github.helpdesk.dto.CategoryDto;
import io.github.helpdesk.dto.MapStructMapper;
import io.github.helpdesk.exception.ErrorType;
import io.github.helpdesk.exception.RestErrorResponseException;
import io.github.helpdesk.model.Category;
import io.github.helpdesk.repo.CategoryRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static io.github.helpdesk.exception.ProblemDetailBuilder.forStatusAndDetail;

@Service
public class CategoryService implements ServiceBase<CategoryDto, Long> {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    private final MapStructMapper mapper;

    public CategoryService(CategoryRepository categoryRepository,
                           MapStructMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Override
    public Page<CategoryDto> getAll(Pageable pageable) {
        Page<CategoryDto> categories = categoryRepository.findAll(pageable).map(mapper::mapTo);
        log.debug("Fetched categories {}", categories.get());
        return categories;
    }

    @Override
    public CategoryDto getById(Long id) {
        return mapper.mapTo(getOriginalById(id));
    }

    public Category getOriginalById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            throw new RestErrorResponseException(forStatusAndDetail(HttpStatus.NOT_FOUND, "Category not found")
                    .withErrorType(ErrorType.NOT_FOUND)
                    .build()
            );
        }
        return category.get();
    }

    public Category getOriginalByName(String name) {
        Optional<Category> category = categoryRepository.findCategoryByName(name);
        if (category.isEmpty()) {
            throw new RestErrorResponseException(forStatusAndDetail(HttpStatus.NOT_FOUND, "Category not found")
                    .withErrorType(ErrorType.NOT_FOUND)
                    .build()
            );
        }
        return category.get();
    }

    public void existsByName(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new RestErrorResponseException(forStatusAndDetail(HttpStatus.CONFLICT,
                    String.format("Category with %s already exist", name))
                    .withErrorType(ErrorType.RESOURCE_ALREADY_EXISTS)
                    .build()
            );
        }
    }

    @Transactional
    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        existsByName(categoryDto.getName());

        Category savedCategory = categoryRepository.save(mapper.mapTo(categoryDto));
        return mapper.mapTo(savedCategory);
    }

    @Transactional
    @Override
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        Category categoryFromDB = getOriginalById(id);
        mapper.updateCategory(categoryDto, categoryFromDB);
        return mapper.mapTo(categoryFromDB);
    }

    @Transactional
    public CategoryDto update(Long id, String name) {
        existsByName(name);
        Category categoryFromDB = getOriginalById(id);
        categoryFromDB.setName(name);
        return mapper.mapTo(categoryFromDB);
    }

    @Override
    public void delete(Long id) {
        Category category = getOriginalById(id);
        categoryRepository.delete(category);
    }

}
