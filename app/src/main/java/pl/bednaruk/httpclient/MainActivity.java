package pl.bednaruk.httpclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements Handler.Callback, TrackAdapter.ItemClicked {
    private static final String TAG = "MainActivity";
    private ApiInterface apiInterface;
    private Handler handler;
    private HandlerThread thread;
    private RecyclerView recyclerView;
    private TrackAdapter myAdapter;
    private List<Track> tracks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tracks = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewMain);
        startThread();
    }

    private void startThread() {
        thread = new HandlerThread("Thread");
        thread.start();
        handler = new Handler(thread.getLooper(), this);
        handler.sendEmptyMessage(0);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean handleMessage(@NonNull Message msg) {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Track>> call = apiInterface.getTracks();
        call.enqueue(new Callback<List<Track>>() {

            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                tracks.addAll(response.body());
                runOnUiThread(() -> setRecyclerOfTrack(tracks));
            }
            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                Log.e(TAG,"onFeilure " + t.getLocalizedMessage());
            }
        });
        return true;
    }
    private void setRecyclerOfTrack(List<Track> trackArrayList){
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(layoutManager);
        myAdapter = new TrackAdapter(this, trackArrayList);
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    public void OnItemClicked(int id) {
        Intent intent = new Intent(MainActivity.this
                , TrackActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.quit();
    }
}