package ru.practicum.ewm.controller.priv;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.SubscriptionDto;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.model.enums.UserStatus;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/subscriptions/{userId}")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateUserController {

    private final EventService eventService;
    private final UserService userService;

    @PostMapping("/subscribe/{userTargetId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void subscribe(@PathVariable @Positive Long userId, @PathVariable @Positive Long userTargetId) {
        log.info("Adding subscription by user userId={} for target user targetUserId={}", userId, userTargetId);

        userService.subscribe(userId, userTargetId);
    }

    @GetMapping
    public SubscriptionDto getSubscriptions(@PathVariable @Positive Long userId) {
        log.info("Retrieving subscriptions for user userId={}", userId);
        return userService.getSubscriptions(userId);
    }

    @GetMapping("/events")
    public List<EventShortDto> getSubscriptionEvents(@PathVariable @Positive Long userId) {
        log.info("Retrieving events of users subscribed to by userId={}", userId);

        return eventService.getSubscriptionEvents(userId);
    }

    @DeleteMapping("/subscribe/{userTargetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@PathVariable @Positive Long userId, @PathVariable @Positive Long userTargetId) {
        log.info("Removing subscription of user userId={} for target user targetUserId={}", userId, userTargetId);

        userService.unsubscribe(userId, userTargetId);
    }

    @PatchMapping("/status")
    public UserDto setStatus(@PathVariable @Positive Long userId, @RequestParam(name = "status") UserStatus status) {
        return userService.setStatus(userId, status);
    }
}
