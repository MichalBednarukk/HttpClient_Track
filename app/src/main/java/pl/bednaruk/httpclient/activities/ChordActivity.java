package pl.bednaruk.httpclient.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;

import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import pl.bednaruk.httpclient.ApiClient;
import pl.bednaruk.httpclient.ApiInterface;
import pl.bednaruk.httpclient.R;
import pl.bednaruk.httpclient.adapters.ChordAdapter;
import pl.bednaruk.httpclient.models.ChordApp;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static androidx.recyclerview.widget.RecyclerView.LayoutManager;

public class ChordActivity extends AppCompatActivity implements Handler.Callback {
    private static final String TAG = "ChordActivity";
    int trackID;
    RecyclerView recyclerView;
    private ApiInterface apiInterface;
    private Handler handler;
    private HandlerThread thread;
    private List<ChordApp> chords;
    private LayoutManager layoutManager;
    private ChordAdapter myAdapter;
    private ProgressBar progressBarChord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chord);
        chords = new ArrayList<>();
        trackID = getIntent().getIntExtra("trackID",0);
        recyclerView = findViewById(R.id.recyclerViewChord);
        startThread();
    }

    private void startThread() {
        thread = new HandlerThread("Thread");
        thread.start();
        handler = new Handler(thread.getLooper(), this);
        handler.sendEmptyMessage(0);
    }

    private void setProgressBarChord(boolean barStatus) {
        progressBarChord = findViewById(R.id.progressBarChord);
        progressBarChord.setIndeterminate(barStatus);
        if (barStatus)progressBarChord.setVisibility(View.VISIBLE);
        else progressBarChord.setVisibility(View.GONE);
    }
    @Override
    public boolean handleMessage(@NonNull Message msg) {
        setProgressBarChord(true);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        {
            Call<List<ChordApp>> call = apiInterface.getChordsByTrackID(trackID);
        //noinspection NullableProblems
        call.enqueue(new Callback<List<ChordApp>>() {
            @Override
            public void onResponse(Call<List<ChordApp>> call, Response<List<ChordApp>> response) {
                chords = response.body();
                runOnUiThread(() -> {
                    setRecyclerOfChord();
                    setProgressBarChord(false);
                });
            }
            @Override
            public void onFailure(Call<List<ChordApp>> call, Throwable t) {
                Log.e(TAG,"onFeilure " + t.getLocalizedMessage());
            }
        });
    }
        return true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.quit();
    }
    private void setRecyclerOfChord(){
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        myAdapter = new ChordAdapter(this, chords);
        recyclerView.setAdapter(myAdapter);
    }
}