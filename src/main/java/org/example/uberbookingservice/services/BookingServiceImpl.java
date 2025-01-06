package org.example.uberbookingservice.services;

import org.example.uberbookingservice.Repositories.BookingRepository;
import org.example.uberbookingservice.Repositories.PassengerRepository;
import org.example.uberbookingservice.dto.DriverLocationDto;
import org.example.uberbookingservice.dto.createBookingDto;
import org.example.uberbookingservice.dto.createBookingResponseDto;
import org.example.uberbookingservice.dto.getNearbyDriversRequestDto;
import org.example.uberprojectentityservice.Models.Booking;
import org.example.uberprojectentityservice.Models.BookingStatus;
import org.example.uberprojectentityservice.Models.Passenger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class BookingServiceImpl implements BookingService {
    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;
    private final RestTemplate restTemplate;
    private static final String LOCATION_SERVICE = "http://localhost:7777";

    public BookingServiceImpl(PassengerRepository passengerRepository, BookingRepository bookingRepository) {
        this.passengerRepository = passengerRepository;
        this.bookingRepository = bookingRepository;
        this.restTemplate = new RestTemplate();
    }

    public createBookingResponseDto createBooking(createBookingDto bookingDetails) {
        Optional<Passenger> passenger=passengerRepository.findById(bookingDetails.getPassengerId());
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.ASSIGNING_DRIVER)
                .startLocation(bookingDetails.getStartLocation())
                .endLocation(bookingDetails.getEndLocation())
                .passenger(passenger.get())
                .build();
        Booking newBooking=bookingRepository.save(booking);

        getNearbyDriversRequestDto  requestDto= getNearbyDriversRequestDto
                .builder()
                .latitude(bookingDetails.getStartLocation().getLatitude())
                .longitude(bookingDetails.getStartLocation().getLongitude())
                .build();
        // make an api call to location service to fetch nearby drivers
        ResponseEntity<DriverLocationDto[]> res=restTemplate.postForEntity(LOCATION_SERVICE+"/api/location/nearbydrivers",requestDto,DriverLocationDto[] .class);
        if(res.getStatusCode().is2xxSuccessful() && res.getBody()!=null){
            List<DriverLocationDto> driverLocations= Arrays.asList(Objects.requireNonNull(res.getBody()));
            driverLocations.forEach(
                    driverLocation -> {System.out.println(driverLocation.getDriverId()+" "+"lat: "+driverLocation.getLatitude()+" long: "+driverLocation.getLongitude());}
            );
        }
        return createBookingResponseDto
                .builder()
                .bookingId(newBooking.getId())
                .bookingStatus(newBooking.getBookingStatus().toString())
                .build();
    }
}
