package org.example.uberbookingservice.services;

import org.example.uberbookingservice.dto.UpdateBookingRequestDto;
import org.example.uberbookingservice.dto.UpdateBookingResponseDto;
import org.example.uberbookingservice.dto.createBookingDto;
import org.example.uberbookingservice.dto.createBookingResponseDto;
import org.example.uberprojectentityservice.Models.Booking;

public interface BookingService {
    createBookingResponseDto createBooking(createBookingDto requestDto);
    UpdateBookingResponseDto updateBooking(UpdateBookingRequestDto requestDto,Long id);
}