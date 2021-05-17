package pl.bednaruk.httpclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
        tvTrackAuthor = findViewById(R.id.tvTrackAuthor);
        tvUsername = findViewById(R.id.tvUsername);
        tvLyrics = findViewById(R.id.tvLyrics);
    }

    private void startThread() {
        thread = new HandlerThread("Thread");
        thread.start();
        handler = new Handler(thread.getLooper(), this);
        handler.sendEmptyMessage(0);
    }


    @Override
    public boolean handleMessage(@NonNull Message msg) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Track> call = apiInterface.getTrack(trackId);
        //noinspection NullableProblems
        call.enqueue(new Callback<Track>() {
            @Override
            public void onResponse(Call<Track> call, Response<Track> response) {
                track = response.body();
                runOnUiThread(() -> setUI());
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
        tvTrackAuthor.setText(track.getAuthor());
        tvUsername.setText(track.getUsername());
        tvLyrics.setMovementMethod(new ScrollingMovementMethod());
        tvLyrics.setText(track.getBody());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.track_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.quit();
    }
    }



