package id.co.reich.mockupsouthscape.rest;

import id.co.reich.mockupsouthscape.pojo.EventList;
import id.co.reich.mockupsouthscape.pojo.PaymentList;
import okhttp3.ResponseBody;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> login(@Field("email") String email,
                             @Field("password") String password);

    @GET("user")
    Call<ResponseBody> details(@Header("Accept") String accept,
                               @Header("Authorization") String auth);

    @GET("get_events/{begin}/{end}")
    Call<EventList> getEvents(@Path("begin") int begin,
                              @Path("end") int end);

    @GET("payments/{begin}/{end}")
    Call<PaymentList> getPayments(@Header("Accept") String accept,
                                  @Header("Authorization") String auth,
                                  @Path("begin") int begin,
                                  @Path("end") int end);

    @FormUrlEncoded
    @POST("login")
    Observable<ResponseBody> RxLogin(@Field("email") String email,
                                    @Field("password") String password);
}
