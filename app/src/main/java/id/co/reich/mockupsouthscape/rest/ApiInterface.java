package id.co.reich.mockupsouthscape.rest;

import id.co.reich.mockupsouthscape.pojo.EventList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> login(@Field("email") String email,
                             @Field("password") String password);

    @GET("user")
    Call<ResponseBody> details(@Header("Accept") String accept, @Header("Authorization") String auth);

    @GET("get_events")
    Call<EventList> getEvents();
}
