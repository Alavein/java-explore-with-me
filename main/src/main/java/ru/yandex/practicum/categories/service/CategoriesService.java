package ru.yandex.practicum.categories.service;

import ru.yandex.practicum.categories.dto.CategoryDto;
import ru.yandex.practicum.categories.dto.NewCategoryDto;

import java.util.List;

public interface CategoriesService {

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Integer catId, CategoryDto categoryDto);

    List<CategoryDto> getAllCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Integer catId);

    void deleteCategoryById(Integer catId);
}
