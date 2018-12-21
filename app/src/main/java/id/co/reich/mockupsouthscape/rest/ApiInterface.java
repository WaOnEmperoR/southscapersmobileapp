package id.co.reich.mockupsouthscape.rest;

import java.util.List;

import id.co.reich.mockupsouthscape.pojo.EventList;
import id.co.reich.mockupsouthscape.pojo.Payment;
import id.co.reich.mockupsouthscape.pojo.PaymentList;
import id.co.reich.mockupsouthscape.pojo.UserDetail;
import io.reactivex.Single;
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

    @GET("payments_history/{begin}/{end}")
    Single<List<Payment>> RxGetPayments(@Header("Accept") String accept,
                                      @Header("Authorization") String auth,
                                      @Path("begin") int begin,
                                      @Path("end") int end);

    @FormUrlEncoded
    @POST("login")
    Observable<ResponseBody> RxLogin(@Field("email") String email,
                                     @Field("password") String password);

    @GET("user")
    Observable<UserDetail> RxUserDetails(@Header("Accept") String accept,
                                         @Header("Authorization") String auth);

    @GET("get_events/{begin}/{end}/{choice}")
    Observable<EventList> RxGetEvents(@Path("begin") int begin,
                                      @Path("end") int end,
                                      @Path("choice") int choice);
}
