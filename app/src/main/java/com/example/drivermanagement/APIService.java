package com.example.drivermanagement;

import com.example.drivermanagement.notifications.Response;
import com.example.drivermanagement.notifications.RootModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Authorization: key=AAAAefRbBCI:APA91bEU3VQCUHA2EEHpT9CqldCu-7kjfUXYfcYS5-L1LmaUqH_h1at8ykF9wM9JWKr00NjmCLQOGw-4LOtaOLkh7MyqPRJbVt0aMRO_WsqBGdUKj1xWmRt9wfqTPnLgQufwFDOB0QMV",
                    "Content-Type:application/json"
            }
    )

    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body RootModel root);
}
