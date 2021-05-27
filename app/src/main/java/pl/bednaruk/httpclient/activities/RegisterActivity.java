package pl.bednaruk.httpclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import pl.bednaruk.httpclient.ApiClient;
import pl.bednaruk.httpclient.ApiInterface;
import pl.bednaruk.httpclient.R;
import pl.bednaruk.httpclient.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("FieldCanBeLocal")
public class RegisterActivity extends AppCompatActivity {
    private EditText etUsernameRegistry, etPasswordRegistry;
    private Button btnRegistry;
    private TextView btnLoginActivity;
    private Register register;
    private ProgressBar progressBarRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialize();
    }

    private void initialize() {
        etUsernameRegistry = findViewById(R.id.etUsernameRegistry);
        etPasswordRegistry = findViewById(R.id.etPasswordRegistry);
        btnRegistry = findViewById(R.id.btnRegistry);
        btnLoginActivity = findViewById(R.id.btnLoginActivity);
        progressBarRegister = findViewById(R.id.progressBarRegister);
        progressBarRegister.setVisibility(View.INVISIBLE);
        register = new Register();
    }

    public void loginActivity(View view) {//Click Sign in button
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void register(View view) {//Click register button
        if (!etUsernameRegistry.getText().toString().isEmpty() && !etPasswordRegistry.getText().toString().isEmpty())
            register.register();
        else if (etUsernameRegistry.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Enter Username", Toast.LENGTH_SHORT).show();
        else Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();

    }

    private class Register implements Handler.Callback {
        private Handler handler;
        private HandlerThread thread;


        private void register() {
            thread = new HandlerThread("Thread");
            thread.start();
            handler = new Handler(thread.getLooper(), this);
            handler.sendEmptyMessage(0);
            progressBarRegister.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            User user = getUser();
            Call<Void> call = apiInterface.registerUser(user);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.code()==200)
                    runOnUiThread(Register.this::onSuccessRegister);
                    else runOnUiThread(() -> onFailedRegister(response.code()));
                    thread.interrupt();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                runOnUiThread(() -> onFailedRegister(408));
                thread.interrupt();
                }
            });
            return false;
        }

        private User getUser() {
            String username;
            String password;
            username = etUsernameRegistry.getText().toString();
            password = etPasswordRegistry.getText().toString();
            return new User(username, password);
        }

        private void onSuccessRegister() {
        progressBarRegister.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(), "Register success",Toast.LENGTH_LONG).show();
        }

        private void onFailedRegister(int codeHttp) {
            progressBarRegister.setVisibility(View.INVISIBLE);
            if(codeHttp == 422) Toast.makeText(getApplicationContext(), "This username is already registered",Toast.LENGTH_LONG).show();
        }
    }
}