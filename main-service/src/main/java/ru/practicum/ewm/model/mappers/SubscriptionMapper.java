package ru.practicum.ewm.model.mappers;

import ru.practicum.ewm.dto.SubscriptionDto;

import java.util.List;

public class SubscriptionMapper {

    public static SubscriptionDto mapToSubscriptionDto(List<Long> usersTargetIds, Long subscriberId) {
        return SubscriptionDto.builder()
                .subscriberId(subscriberId)
                .targetUserIds(usersTargetIds)
                .build();
    }
}
