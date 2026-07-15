package ru.practicum.ewm.dto;

import lombok.*;
import ru.practicum.ewm.model.enums.UserStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private UserStatus status;
}