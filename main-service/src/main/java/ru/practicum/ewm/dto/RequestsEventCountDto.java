package ru.practicum.ewm.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder

public class RequestsEventCountDto {

        private final Long eventId;
        private final Long count;

}