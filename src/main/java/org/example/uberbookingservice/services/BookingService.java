package org.example.uberbookingservice.services;

import org.example.uberbookingservice.dto.createBookingDto;
import org.example.uberbookingservice.dto.createBookingResponseDto;
import org.example.uberprojectentityservice.Models.Booking;

public interface BookingService {
    public createBookingResponseDto createBooking(createBookingDto requestDto);
}
