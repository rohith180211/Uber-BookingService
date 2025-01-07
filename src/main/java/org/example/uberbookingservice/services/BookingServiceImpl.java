package org.example.uberbookingservice.services;

import org.example.uberbookingservice.Repositories.BookingRepository;
import org.example.uberbookingservice.Repositories.DriverRepository;
import org.example.uberbookingservice.Repositories.PassengerRepository;
import org.example.uberbookingservice.apis.LocationServiceApi;
import org.example.uberbookingservice.dto.*;
import org.example.uberprojectentityservice.Models.Booking;
import org.example.uberprojectentityservice.Models.BookingStatus;
import org.example.uberprojectentityservice.Models.Driver;
import org.example.uberprojectentityservice.Models.Passenger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
public class BookingServiceImpl implements BookingService {

    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;

    private final LocationServiceApi locationServiceApi;
    private final RestTemplate restTemplate;
    private final DriverRepository driverRepository;


//    private static final String LOCATION_SERVICE = "http://localhost:7777";

    public BookingServiceImpl(PassengerRepository passengerRepository,
                              BookingRepository bookingRepository, LocationServiceApi locationServiceApi, DriverRepository driverRepository) {
        this.passengerRepository = passengerRepository;
        this.bookingRepository = bookingRepository;
        this.locationServiceApi = locationServiceApi;
        this.restTemplate = new RestTemplate();
        this.driverRepository = driverRepository;
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
        processNearbyDriversAsync(request );
//
//
//        ResponseEntity<DriverLocationDto[]> result = restTemplate.postForEntity(LOCATION_SERVICE + "/api/location/nearby/drivers", request, DriverLocationDto[].class);
//
//        if(result.getStatusCode().is2xxSuccessful() && result.getBody() != null) {
//            List<DriverLocationDto> driverLocations = Arrays.asList(result.getBody());
//            driverLocations.forEach(driverLocationDto -> {
//                System.out.println(driverLocationDto.getDriverId() + " " + "lat: " + driverLocationDto.getLatitude() + "long: " + driverLocationDto.getLongitude());
//            });
//        }

        return createBookingResponseDto.builder()
                .bookingId(newBooking.getId())
                .bookingStatus(newBooking.getBookingStatus().toString())
                .build();
    }

    @Override
    public UpdateBookingResponseDto updateBooking(UpdateBookingRequestDto requestDto,Long BookingId) {

        Optional<Driver> driver=driverRepository.findById(requestDto.getDriverId().get());

           bookingRepository.updateBookingStatusandDriverById(BookingId,BookingStatus.SCHEDULED ,driver.get());
           Optional<Booking> booking = bookingRepository.findById(BookingId);
           return UpdateBookingResponseDto.builder()
                   .bookingId(BookingId).status(booking.get().getBookingStatus()).driver(Optional.ofNullable(booking.get().getDriver())).build();

    }

    private void processNearbyDriversAsync(getNearbyDriversRequestDto nearbyDriversRequestDto) {
        Call<DriverLocationDto[]> call=locationServiceApi.getNearbyDrivers(nearbyDriversRequestDto);
        call.enqueue(new Callback<DriverLocationDto[]>() {
            @Override
           public void onResponse(Call<DriverLocationDto[]> call, Response<DriverLocationDto[]> response) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(response.isSuccessful()  && response.body() != null) {
            List<DriverLocationDto> driverLocations = Arrays.asList(response.body());
            driverLocations.forEach(driverLocationDto -> {
                System.out.println(driverLocationDto.getDriverId() + " " + "lat: " + driverLocationDto.getLatitude() + "long: " + driverLocationDto.getLongitude());
            });
        }
                else{
                    System.out.println("Request failed"+response.message());
                }
            }

            @Override
            public void onFailure(Call<DriverLocationDto[]> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}