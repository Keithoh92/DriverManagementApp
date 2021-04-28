package com.example.drivermanagement;

import com.example.drivermanagement.notifications.Response;
import com.example.drivermanagement.notifications.RootModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/*

API INTERFACE FOR COMMUNICATING WITH FIREBASE SERVER TO SEND NOTIFICATION TO USER ON MESSAGE SEND
THIS TRIGGERS THE FIREBASE SERVER TO NOTIFY THE USER BY USING THE DEVICE TOKEN OF THE RECEIVER OF THE MESSAGE
 */
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
