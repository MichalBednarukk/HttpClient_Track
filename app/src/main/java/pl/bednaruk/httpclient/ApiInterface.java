package pl.bednaruk.httpclient;

import java.util.List;

import pl.bednaruk.httpclient.models.ChordApp;
import pl.bednaruk.httpclient.models.Track;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET("/tracks")
    Call<List<Track>> getTracks();
    @GET("/track/{id}")
    Call<Track> getTrack(@Path("id") int id);
    @GET("/chords/track/{id}")
    Call<List<ChordApp>> getChordsByTrackID(@Path("id") int id);
    @POST("/login")
    Call<Void> login(@Body Login login);
   @POST("/user/register")
    Call<Void> registerUser(@Body User user);
    @GET("user/{username}/isFavouriteTrack/{trackId}")
    Call<Track> isFavouriteTrack(@Path("username") String username, @Path("trackId") String trackId, @Header("Authorization") String authorization);
    @PATCH("/user/{username}/{trackId}")
    Call<Void> setFavouriteTrack(@Path("username") String username, @Path("trackId") String trackId, @Header("Authorization") String authorization);
    @DELETE("/user/{username}/{trackId}")
    Call<Void> deleteFavouriteTrack(@Path("username") String username, @Path("trackId") String trackId, @Header("Authorization") String authorization);
    @GET("/user/{username}/favouriteTracks")
    Call<List<Track>> getFavouriteTracks(@Path("username") String username, @Header("Authorization") String authorization);

}
