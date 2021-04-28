package com.example.drivermanagement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("distancematrix/json")
    Call<ResultDistanceMatrix> getDistance(@Query("key") String key,
                                           @Query("origins") String origins, @Query("destinations") String destinations);


}
