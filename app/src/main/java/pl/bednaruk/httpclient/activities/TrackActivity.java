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
    private TextView tvTrackName, tvTrackAuthor,tvUsername, tvLyrics;
    private int trackId;
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
        Call<Track> call = apiInterface.getTrack(trackId);
        //noinspection NullableProblems
        call.enqueue(new Callback<Track>() {
            @Override
            public void onResponse(Call<Track> call, Response<Track> response) {
                track = response.body();
                runOnUiThread(() -> {
                    setUI();//Set track
                    setProgressBarTrack(false);//ProgressBar off
                });
            }
            @Override
            public void onFailure(Call<Track> call, Throwable t) {
                Log.e(TAG,"onFeilure " + t.getLocalizedMessage());
            }
        });
        return true;
    }

    private void setUI(){
        tvTrackName.setText(track.getName());
        tvTrackName.setVisibility(View.VISIBLE);

        tvTrackAuthor.setText(track.getAuthor());
        tvTrackAuthor.setVisibility(View.VISIBLE);

        tvUsername.setText(track.getUsername());
        tvUsername.setVisibility(View.VISIBLE);

        tvLyrics.setMovementMethod(new ScrollingMovementMethod());
        tvLyrics.setText(track.getBody());
        tvLyrics.setVisibility(View.VISIBLE);
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
        thread.quit();
    }
    }



