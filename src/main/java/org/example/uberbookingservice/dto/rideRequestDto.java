package org.example.uberbookingservice.dto;


import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class rideRequestDto {
    private Long passengerId;
   // private  ExactLocation startLocation;
   // private ExactLocation endLocation;
    private List<Long> driverIds;
    private Long bookingId;
}
