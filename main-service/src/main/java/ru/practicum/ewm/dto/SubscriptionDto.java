package ru.practicum.ewm.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record SubscriptionDto(Long subscriberId,
                              List<Long> targetUserIds) {
}
