package ru.yandex.practicum.categories.mapper;

import ru.yandex.practicum.categories.model.Category;
import ru.yandex.practicum.categories.dto.CategoryDto;
import ru.yandex.practicum.categories.dto.NewCategoryDto;

public class CategoryMapper {

    public static Category toCategory(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toCategoryNew(NewCategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .build();
    }
}
