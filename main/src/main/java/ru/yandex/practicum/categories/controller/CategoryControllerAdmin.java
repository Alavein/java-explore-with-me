package ru.yandex.practicum.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.categories.dto.CategoryDto;
import ru.yandex.practicum.categories.dto.NewCategoryDto;
import ru.yandex.practicum.categories.service.CategoriesService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryControllerAdmin {

    private final CategoriesService categoriesService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Создание категории {}", newCategoryDto);
        return categoriesService.createCategory(newCategoryDto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable Integer catId,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Обновление категории с id {}", catId);
        return categoriesService.updateCategory(catId, categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable Integer catId) {
        log.info("Удаление категории с id {}", catId);
        categoriesService.deleteCategoryById(catId);
    }
}