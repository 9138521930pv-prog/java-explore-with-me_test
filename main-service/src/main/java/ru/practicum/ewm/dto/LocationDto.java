package ru.practicum.ewm.dto;


import lombok.*;

import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDto {
    @Min(-90)
    @Max(90)
    @NotNull
    private Float lat;
    @Min(-180)
    @Max(180)
    @NotNull
    private Float lon;
}