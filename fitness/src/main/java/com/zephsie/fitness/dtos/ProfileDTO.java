package com.zephsie.fitness.dtos;

import com.zephsie.fitness.models.entity.ActivityType;
import com.zephsie.fitness.models.entity.Gender;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {

    @NotNull(message = "Height is required")
    private Double height;

    @NotNull(message = "Weight is required")
    private Double weight;

    @NotNull(message = "Target is required")
    private Double target;

    @NotNull(message = "Birthday is required")
    @Past(message = "Birthday must be in the past")
    private LocalDate birthday;

    @NotNull(message = "Activity type is required")
    private ActivityType activityType;

    @NotNull(message = "Gender is required")
    private Gender gender;
}
