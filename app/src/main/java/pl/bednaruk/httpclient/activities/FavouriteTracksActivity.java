package pl.bednaruk.httpclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.bednaruk.httpclient.ApiClient;
import pl.bednaruk.httpclient.ApiInterface;
import pl.bednaruk.httpclient.R;
import pl.bednaruk.httpclient.SessionManager;
import pl.bednaruk.httpclient.adapters.TrackAdapter;
import pl.bednaruk.httpclient.models.Track;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouriteTracksActivity extends AppCompatActivity implements Handler.Callback, TrackAdapter.ItemClicked {
    private ApiInterface apiInterface;
    private Handler handler;
    private HandlerThread thread;
    private RecyclerView recyclerView;
    private TrackAdapter myAdapter;
    private List<Track> tracks;
    private ProgressBar progressBarMain;

    SessionManager sessionManager;

    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    private View headerView;
    private Button btnSignIn;
    private TextView tvUsername;
    private ImageView imageView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_tracks);
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        initialize();
        startThread();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sessionManager.isLoggin()){
                    sessionManager.logout();
                    Toast.makeText(FavouriteTracksActivity.this, "LOGOUT", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(FavouriteTracksActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.home:Intent intent = new Intent(FavouriteTracksActivity.this,MainActivity.class);
                    startActivity(intent);
                case R.id.favourite:
                default:
                    return true;
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.home:Intent intent = new Intent(FavouriteTracksActivity.this,MainActivity.class);
                        startActivity(intent);
                    case R.id.favourite:
                    default:
                        return true;
                }
            }
        });
    }

    private void initialize(){

        tracks = new ArrayList<>();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.favourite);
        drawerLayout = findViewById(R.id.activity_favourite_tracks);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.bringToFront();
        headerView = navigationView.getHeaderView(0);
        tvUsername = headerView.findViewById(R.id.tvUsername);
        imageView = headerView.findViewById(R.id.imageView);
        btnSignIn = headerView.findViewById(R.id.btnSignIn);

        if(sessionManager.isLoggin()){
            tvUsername.setText(sessionManager.getUserDetail().get("USERNAME"));
            btnSignIn.setText("SIGN OUT");
        }

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
        runOnUiThread(() -> setProgressBarMain(true));
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Track>> call = apiInterface.getFavouriteTracks(sessionManager.getUserDetail().get("USERNAME"),sessionManager.getUserDetail().get("TOKEN"));
        call.enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                if (response.body() != null) {
                    tracks.addAll(response.body());
                }
                runOnUiThread(() -> {
                    setRecyclerOfTrack(tracks);
                    setProgressBarMain(false);
                });
            }
            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
            }
        });
        return true;
    }

    private void setRecyclerOfTrack(List<Track> trackArrayList) {
        recyclerView = findViewById(R.id.recyclerViewFavourites);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(FavouriteTracksActivity.this, LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(layoutManager);
        myAdapter = new TrackAdapter(this, trackArrayList);
        recyclerView.setAdapter(myAdapter);
    }

    private void setProgressBarMain(boolean barStatus) {
        progressBarMain = findViewById(R.id.progressBarFavourites);
        progressBarMain.setIndeterminate(barStatus);
        if (barStatus)progressBarMain.setVisibility(View.VISIBLE);
        else progressBarMain.setVisibility(View.GONE);
    }

    @Override
    public void OnItemClicked(int id) {
        Intent intent = new Intent(FavouriteTracksActivity.this
                , TrackActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}