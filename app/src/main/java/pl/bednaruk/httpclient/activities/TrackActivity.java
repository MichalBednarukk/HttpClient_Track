package pl.bednaruk.httpclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import pl.bednaruk.httpclient.ApiClient;
import pl.bednaruk.httpclient.ApiInterface;
import pl.bednaruk.httpclient.R;
import pl.bednaruk.httpclient.models.Track;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackActivity extends AppCompatActivity implements Handler.Callback {
    private static final String TAG = "TrackActivity";
    private Handler handler;
    private HandlerThread thread;
    private TextView tvTrackName, tvTrackAuthor, tvLyrics;
    private int trackId;
    private String username;
    private boolean isFavouriteTrack;
    private Track track;
    private ProgressBar progressBarTrack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        trackId = getIntent().getIntExtra("id",0);
        initialize();
        startThread();
    }

    private void initialize() {
        tvTrackName = findViewById(R.id.tvTrackName);
        tvTrackName.setVisibility(View.INVISIBLE);

        tvTrackAuthor = findViewById(R.id.tvTrackAuthor);
        tvTrackAuthor.setVisibility(View.INVISIBLE);

        tvUsername = findViewById(R.id.tvUsername);
        tvUsername.setVisibility(View.INVISIBLE);

        tvLyrics = findViewById(R.id.tvLyrics);
        tvLyrics.setVisibility(View.INVISIBLE);
    }

    private void startThread() {
        thread = new HandlerThread("Thread");
        thread.start();
        handler = new Handler(thread.getLooper(), this);
        handler.sendEmptyMessage(0);
    }


    private void setProgressBarTrack(boolean barStatus) {
        progressBarTrack = findViewById(R.id.progressBarTrack);
        progressBarTrack.setIndeterminate(barStatus);
        if (barStatus)progressBarTrack.setVisibility(View.VISIBLE);
        else progressBarTrack.setVisibility(View.GONE);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        runOnUiThread(() -> setProgressBarTrack(true));
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Track> call = apiInterface.getTrack(trackId,sessionManager.getUserDetail().get("TOKEN"));
        //noinspection NullableProblems
        call.enqueue(new Callback<Track>() {
            @Override
            public void onResponse(Call<Track> call, Response<Track> response) {
                track = response.body();
                favouriteTrack.isFavourite();
                thread.interrupt();
            }
            @Override
            public void onFailure(Call<Track> call, Throwable t) {
                Log.e(TAG,"onFeilure " + t.getLocalizedMessage());
                if(t.getLocalizedMessage().equals("timeout"))handler.sendEmptyMessage(0);//try again
            }
        });
        return true;
    }

    private void setUI(){
        tvTrackName.setText(track.getName());
        tvTrackName.setVisibility(View.VISIBLE);

        tvTrackAuthor.setText(track.getAuthor());
        tvTrackAuthor.setVisibility(View.VISIBLE);

        tvLyrics.setMovementMethod(new ScrollingMovementMethod());
        tvLyrics.setText(track.getBody());
        tvLyrics.setVisibility(View.VISIBLE);

        favouriteTrack.setBtnFavourite();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.track_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(TrackActivity.this, ChordActivity.class);
        intent.putExtra("trackID",trackId);
        startActivity(intent);
        return true;
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void favouriteChange(View view) {//OnClick btnFavourite
        isFavouriteTrack = !isFavouriteTrack;
        if(isFavouriteTrack){
            favouriteTrack.setFavouriteTrack();
        }
        else favouriteTrack.deleteFavouriteTrack();
        favouriteTrack.setBtnFavourite();
    }




    private  class FavouriteTrack implements Handler.Callback {

        private void setFavouriteTrack(){
            thread = new HandlerThread("Thread");
            thread.start();
            handler = new Handler(thread.getLooper(), this);
            handler.sendEmptyMessage(1);//1-for set
        }

        private void deleteFavouriteTrack(){
            thread = new HandlerThread("Thread");
            thread.start();
            handler = new Handler(thread.getLooper(), this);
            handler.sendEmptyMessage(0);//0-for reset
        }

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<Void> call;
            if(msg.what == 1){//set as a favourites Track
                call = apiInterface.setFavouriteTrack(username, String.valueOf(trackId), sessionManager.getUserDetail().get("TOKEN"));
            }
            else {//reset favourites
                call = apiInterface.deleteFavouriteTrack(username, String.valueOf(trackId), sessionManager.getUserDetail().get("TOKEN")); }
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    thread.interrupt();
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    thread.interrupt();
                }
            });
            return true;
        }
        public boolean isFavourite(){
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<Track> call = apiInterface.isFavouriteTrack(username, String.valueOf(trackId),sessionManager.getUserDetail().get("TOKEN"));
            call.enqueue(new Callback<Track>() {
                @Override
                public void onResponse(Call<Track> call, Response<Track> response) {
                    if(response.code() == 404) isFavouriteTrack = false;
                    else if(response.code() == 200){
                        isFavouriteTrack = true;
                    }
                    runOnUiThread(() -> {
                        setUI();//Set track
                        setProgressBarTrack(false);//ProgressBar off
                    });
                }
                @Override
                public void onFailure(Call<Track> call, Throwable t) {
                    //TODO
                }
            });
            return isFavouriteTrack;
        }
        public void setBtnFavourite(){
            if(!isFavouriteTrack)btnFavourite.setImageResource(android.R.drawable.btn_star_big_off);
            else btnFavourite.setImageResource(android.R.drawable.btn_star_big_on);
            btnFavourite.setVisibility(View.VISIBLE);
        }

    }

}




