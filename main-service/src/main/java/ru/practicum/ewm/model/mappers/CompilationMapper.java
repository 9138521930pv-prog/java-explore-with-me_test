package ru.practicum.ewm.model.mappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.model.Compilation;


@Mapper(componentModel = "spring",
        uses = {EventMapper.class})
public interface CompilationMapper {

    // Сигнатура сохранена. MapStruct сам применит EventMapper для конвертации списка событий
    CompilationDto toDto(Compilation compilation);

    // Сигнатура сохранена.
    // Поля id и events игнорируются, так как их логика заполнения происходит в сервисе
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Compilation toCompilation(NewCompilationDto compilationDto);
}