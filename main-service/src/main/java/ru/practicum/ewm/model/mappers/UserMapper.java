package ru.practicum.ewm.model.mappers;

import org.mapstruct.Mapper;

import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.dto.UserShortDto;
import ru.practicum.ewm.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(NewUserRequest newUserRequest);

    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);
}

