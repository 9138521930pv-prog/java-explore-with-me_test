
package ru.practicum.ewm.model.mappers;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.model.Category;

@Mapper(componentModel = "spring") // Это сделает маппер Spring-бином (@Autowired)
public interface CategoryMapper {

    CategoryDto toCategoryDto(Category category);

    Category toCategory(CategoryDto categoryDto);

    Category toNewCategoryDto(NewCategoryDto newCategoryDto);
}