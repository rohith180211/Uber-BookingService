package org.example.uberbookingservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class getNearbyDriversRequestDto {
    Double latitude;
    Double longitude;
}