package ru.yandex.practicum.categories.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.categories.mapper.CategoryMapper;
import ru.yandex.practicum.categories.model.Category;
import ru.yandex.practicum.categories.dto.CategoryDto;
import ru.yandex.practicum.categories.dto.NewCategoryDto;
import ru.yandex.practicum.categories.repository.CategoriesRepository;
import ru.yandex.practicum.exceptions.DataNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesRepository categoriesRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        log.info("Сохранение категории {}", newCategoryDto);
        return CategoryMapper.toCategoryDto(categoriesRepository.save(CategoryMapper.toCategoryNew(newCategoryDto)));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Integer catId, CategoryDto categoryDto) {
        categoriesRepository.findById(catId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Категория не найдена."));

        categoryDto.setId(catId);
        log.info("Обновление категории {}", categoryDto);
        return CategoryMapper.toCategoryDto(categoriesRepository.save(CategoryMapper.toCategory(categoryDto)));
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        log.info("Получениекатегорий по параметрам.");

        return categoriesRepository.findAll(page)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Integer catId) {
        Category category = categoriesRepository.findById(catId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Категория не найдена."));
        log.info("Получение категории по id = {}", catId);
        return CategoryMapper.toCategoryDto(category);
    }


    @Override
    @Transactional
    public void deleteCategoryById(Integer catId) {
        categoriesRepository.findById(catId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Категория не найдена."));
        log.info("Удаление категории по id = {}", catId);
        categoriesRepository.deleteById(catId);
    }
}
