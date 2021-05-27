package pl.bednaruk.httpclient.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
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
import pl.bednaruk.httpclient.Login;
import pl.bednaruk.httpclient.R;
import pl.bednaruk.httpclient.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements Handler.Callback {
    private static final String TAG = "LoginActivity";
    private EditText etUsername,etPassword;
    private Button btnLogin;
    private Handler handler;
    private HandlerThread thread;
    private ApiInterface apiInterface;
    private ProgressBar progressBarLogin;
    private TextView btnRegister;
    SessionManager sessionManager;
    String username,password,token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
    }
    private void startThread() {
        thread = new HandlerThread("Thread");
        thread.start();
        handler = new Handler(thread.getLooper(), this);
        handler.sendEmptyMessage(0);
    }
    private void initialize() {
        sessionManager = new SessionManager(this);
        btnRegister = findViewById(R.id.btnRegister);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBarLogin = findViewById(R.id.progressBarRegister);
        progressBarLogin.setVisibility(View.INVISIBLE);
    }
    private void setProgressBarLogin(boolean barStatus) {
        progressBarLogin.setIndeterminate(barStatus);
        if (barStatus)progressBarLogin.setVisibility(View.VISIBLE);
        else progressBarLogin.setVisibility(View.GONE);
    }
    @SuppressWarnings("NullableProblems")
    @Override
    public boolean handleMessage(@NonNull Message msg) {
        runOnUiThread(() -> setProgressBarLogin(true));
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Login login = getCredentials();
        Call<java.lang.Void> call = apiInterface.login(login);
        call.enqueue(new Callback<java.lang.Void>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(Call<java.lang.Void> call, Response<java.lang.Void> response) {
                if(response.code()==200){
                    token = response.headers().get("Authorization");
                    Log.e(TAG, "onResponse " + token);
                runOnUiThread(() -> onSuccessLogin());
            }
            else {
               onFailureLogin();
            }
            }
            @Override
            public void onFailure(Call<java.lang.Void> call, Throwable t) {
                Log.e(TAG, "onFeilure " + t.getLocalizedMessage());
                onFailureLogin();
            }
        });
    return true;
    }
    private Login getCredentials(){
        username = etUsername.getText().toString();
        password = etPassword.getText().toString();
        return new Login(username,password);
    }

    public void login(View view) {
        if(view == btnLogin) startThread();
    }

    private void onSuccessLogin(){
        setProgressBarLogin(false);
        Toast.makeText(getApplicationContext(), "LOGIN SUCCESS",Toast.LENGTH_LONG).show();
        sessionManager.createSession("1",username, token);
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }
    private void onFailureLogin(){
        setProgressBarLogin(false);
        Toast.makeText(getApplicationContext(), "INCORRECT USERNAME OR PASSWORD",Toast.LENGTH_LONG).show();
    }

    public void registerActivity(View view) {//Click button register
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

}