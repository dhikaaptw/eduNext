package com.example.edunext;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("api/v1/chat")
    Call<SenopatiResponse> chatWithSenopati(@Body SenopatiRequest request);
}
