package pl.bednaruk.httpclient;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET("/tracks")
    Call<List<Track>> getTracks();
    @GET("/track/{id}")
    Call<Track> getTrack(@Path("id") int id);
}
