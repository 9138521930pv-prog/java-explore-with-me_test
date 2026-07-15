package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.SubscriptionDto;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.UserSubscription;
import ru.practicum.ewm.model.UserSubscriptionId;
import ru.practicum.ewm.model.enums.UserStatus;
import ru.practicum.ewm.model.mappers.SubscriptionMapper;
import ru.practicum.ewm.model.mappers.UserMapper;
import ru.practicum.ewm.repository.SubscriptionRepository;
import ru.practicum.ewm.repository.UserRepository;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto addNewUser(NewUserRequest newUserRequest) {
        User user = userMapper.toUser(newUserRequest);
        user.setStatus(UserStatus.PUBLIC);
        userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
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

    @Transactional
    @Override
    public void subscribe(Long userId, Long targetUserId) {
        User subscriber = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = " + userId + " нет"));

        if (userId.equals(targetUserId)) {
            throw new ConflictException("Пользователь не может подписаться на себя");
        }

        if (subscriptionRepository.existsBySubscriberIdAndTargetUserId(userId, targetUserId)) {
            throw new ConflictException("Вы уже подписаны на этого пользователя");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = " + targetUserId + " нет"));

        if (targetUser.getStatus() != UserStatus.PUBLIC) {
            throw new ConflictException("Нельзя подписаться на приватного пользователя");
        }

        UserSubscription userSubscription = UserSubscription.builder()
                .id(UserSubscriptionId.builder()
                        .userSubscriberId(userId)
                        .userTargetId(targetUserId)
                        .build())
                .subscriber(subscriber)
                .targetUser(targetUser)
                .build();

        subscriptionRepository.save(userSubscription);
    }

    @Override
    @Transactional
    public void unsubscribe(Long userId, Long targetUserId) {
        User subscriber = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = " + userId + " нет"));

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = " + targetUserId + " нет"));

        if (!subscriptionRepository.existsBySubscriberIdAndTargetUserId(userId, targetUserId)) {
            throw new NotFoundException("Вы не подписаны на этого пользователя");
        }

        UserSubscriptionId userSubscriptionId = UserSubscriptionId.builder()
                .userSubscriberId(subscriber.getId())
                .userTargetId(targetUser.getId())
                .build();

        subscriptionRepository.deleteById(userSubscriptionId);
    }

    @Override
    public SubscriptionDto getSubscriptions(Long userId) {
        List<UserSubscription> userSubscriptions = subscriptionRepository.findAllBySubscriberId(userId);

        List<Long> targetUserId = userSubscriptions.stream()
                .map(userSubscription -> userSubscription.getTargetUser().getId())
                .toList();

        return SubscriptionMapper.mapToSubscriptionDto(targetUserId, userId);
    }


    @Override
    @Transactional
    public UserDto setStatus(Long userId, UserStatus status) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = " + userId + " нет"));

        user.setStatus(status);
        userRepository.save(user);

        if (UserStatus.PRIVATE.equals(status)) {
            subscriptionRepository.deleteAllByTargetUserId(userId);
        }
        return userMapper.toUserDto(user);
    }
}