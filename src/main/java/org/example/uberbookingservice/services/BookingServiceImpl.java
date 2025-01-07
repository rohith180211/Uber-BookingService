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
import java.util.Optional;


@Service
public class BookingServiceImpl implements BookingService {

    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;

    private final RestTemplate restTemplate;




    private static final String LOCATION_SERVICE = "http://localhost:7777";

    public BookingServiceImpl(PassengerRepository passengerRepository,
                              BookingRepository bookingRepository) {
        this.passengerRepository = passengerRepository;
        this.bookingRepository = bookingRepository;
        this.restTemplate = new RestTemplate();

    }


    @Override
    public createBookingResponseDto createBooking(createBookingDto bookingDetails) {
        Optional<Passenger> passenger = passengerRepository.findById(bookingDetails.getPassengerId());
        Booking booking = Booking.builder()

                .bookingStatus(BookingStatus.ASSIGNING_DRIVER)
                .startLocation(bookingDetails.getStartLocation())
               .endLocation(bookingDetails.getEndLocation())
                .passenger(passenger.get())
                .build();
        Booking newBooking = bookingRepository.save(booking);

        // make an api call to location service to fetch nearby drivers

        getNearbyDriversRequestDto request = getNearbyDriversRequestDto.builder()
                .latitude(bookingDetails.getStartLocation().getLatitude())
                .longitude(bookingDetails.getStartLocation().getLongitude())
                .build();


        ResponseEntity<DriverLocationDto[]> result = restTemplate.postForEntity(LOCATION_SERVICE + "/api/location/nearby/drivers", request, DriverLocationDto[].class);

        if(result.getStatusCode().is2xxSuccessful() && result.getBody() != null) {
            List<DriverLocationDto> driverLocations = Arrays.asList(result.getBody());
            driverLocations.forEach(driverLocationDto -> {
                System.out.println(driverLocationDto.getDriverId() + " " + "lat: " + driverLocationDto.getLatitude() + "long: " + driverLocationDto.getLongitude());
            });
        }

        return createBookingResponseDto.builder()
                .bookingId(newBooking.getId())
                .bookingStatus(newBooking.getBookingStatus().toString())
                .build();
    }
}