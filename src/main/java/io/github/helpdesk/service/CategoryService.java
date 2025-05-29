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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public List<CategoryDto> getAll() {
        List<Category> categories = categoryRepository.findAll();
        log.debug("Fetched categories {}", categories.size());
        return mapper.mapTo(categories);
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

    @Override
    public CategoryDto create(CategoryDto categoryDto) {
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

    @Override
    public void delete(Long id) {
        Category category = getOriginalById(id);
        categoryRepository.delete(category);
    }

}
