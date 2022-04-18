package com.plumb5.plugin;

import android.text.TextUtils;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public class ServiceGenerator {
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit retrofit ;


    public static <S> S createService(

            Class<S> serviceClass, final String authToken, final String authId,final String basURL) {
        if (!TextUtils.isEmpty(authToken) && !TextUtils.isEmpty(authId)) {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(basURL)
                    .addConverterFactory(JacksonConverterFactory.create());

            AuthenticationInterceptor interceptor =
                    new AuthenticationInterceptor(authToken, authId);

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);


                builder.client(httpClient.build());
                retrofit = builder.build();
            }

            return retrofit.create(serviceClass);
        } else return null;


    }

    static class AuthenticationInterceptor implements Interceptor {

        private String id;
        private String authToken;

        public AuthenticationInterceptor(String token, String id) {
            this.authToken = token;
            this.id = id;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();

            Request.Builder builder = original.newBuilder()

                    .header("x-apikey", authToken)
                    .header("x-accountid", id);


            Request request = builder.build();
            return chain.proceed(request);
        }
    }

    public interface API {
        @GET("Mobile/PackageInfo")
        Call<ResponseBody> PackageInfo();

        @POST("Mobile/DeviceRegistration")
        Call<String> DeviceRegistration(@Body Map<String, Object> body);

        @POST("Mobile/ContactDetails")
        Call<ResponseBody> ContactDetails(@Body Map<String, Object> body);

        @POST("Mobile/EventResponses")
        Call<String> EventResponses(@Body Map<String, Object> body);

        @POST("/Mobile/Tracking")
        Call<String> Tracking(@Body Map<String, Object> body);

        @POST("/Mobile/PushResponses")
        Call<ResponseBody> PushResponse(@Body Map<String, Object> body);

        @GET("/Mobile/InApppDisplaySettingsDetails")
        Call<ResponseBody> InAppDetails(@QueryMap Map<String, Object> params);
    }

}
