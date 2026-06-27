package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.mappers.UserMapper; // Импортируем интерфейс-маппер
import ru.practicum.ewm.repository.UserRepository;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto addNewUser(NewUserRequest newUserRequest) {
        User user = userRepository.save(userMapper.toUser(newUserRequest));
        return userMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id= " + userId + " не найден");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getListUsers(List<Long> ids, Integer from, Integer size) {
        int pageNumber = (from == null || size == null) ? 0 : from / size;
        PageRequest page = PageRequest.of(pageNumber, size != null ? size : 10);

        if (ids != null && !ids.isEmpty()) {
            return userRepository.findByIdIn(ids, page)
                    .stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAll(page)
                    .stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }
}