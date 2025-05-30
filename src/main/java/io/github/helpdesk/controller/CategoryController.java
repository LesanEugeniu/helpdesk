package io.github.helpdesk.controller;

import io.github.helpdesk.dto.CategoryDto;
import io.github.helpdesk.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //    ?page=1&size=5&sort=name,desc
    @GetMapping
    public ResponseEntity<Page<CategoryDto>> getCategory(Authentication authentication, Pageable pageable) {
        log.debug("User \"{}\" with Role \"{}\" is fetching categories", authentication.getName(), authentication.getAuthorities());
        return ResponseEntity.ok(categoryService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategory(Authentication authentication, @PathVariable Long id) {
        log.debug("User \"{}\" with Role \"{}\" is fetching category with id \"{}\"",
                authentication.getName(), authentication.getAuthorities(), id);
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CategoryDto> create(Authentication authentication, @RequestBody CategoryDto categoryDto) {
        log.debug("Admin \"{}\" is creating category \"{}\"", authentication.getName(), categoryDto.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(categoryDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(Authentication authentication, @PathVariable Long id,
                                    @RequestParam("name") String name) {
        log.debug("Admin \"{}\" is updating category with id \"{}\"", authentication.getName(), id);
        return ResponseEntity.ok(categoryService.update(id, name));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
        log.debug("Admin \"{}\" is deleting category with id \"{}\"", authentication.getName(), id);
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
