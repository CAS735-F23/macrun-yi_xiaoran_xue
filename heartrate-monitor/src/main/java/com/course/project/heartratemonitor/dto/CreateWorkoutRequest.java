package com.course.project.heartratemonitor.dto;

import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkoutRequest {
    private Long userId;
    private Long longitude;
    private Long latitude;
    private Integer heartRate;
}