package org.example.uberbookingservice.dto;


import lombok.*;
import org.example.uberprojectentityservice.Models.ExactLocation;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class createBookingDto {

    private Long passengerId;

    private ExactLocation startLocation;

    private ExactLocation endLocation;
}