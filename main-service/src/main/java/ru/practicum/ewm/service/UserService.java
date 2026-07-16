package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.SubscriptionDto;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.model.enums.UserStatus;

import java.util.List;

public interface UserService {
    UserDto addNewUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);

    List<UserDto> getListUsers(List<Long> ids, Integer from, Integer size);

    void subscribe(Long userId, Long targetUserId);

    void unsubscribe(Long userId, Long targetUserId);

    SubscriptionDto getSubscriptions(Long userId);

    UserDto setStatus(Long userId, UserStatus status);
}