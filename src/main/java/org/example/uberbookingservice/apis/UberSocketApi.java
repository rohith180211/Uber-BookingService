package org.example.uberbookingservice.apis;
import org.example.uberbookingservice.dto.rideRequestDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UberSocketApi {

    @POST("api/socket/newride")
    Call<Boolean> raiseRideRequest(@Body rideRequestDto requestDto);
}