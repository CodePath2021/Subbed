package com.example.subbed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername;
    EditText etPassword;
    Button btnLogin;
    Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ParseUser.getCurrentUser() != null)
            goMainActivity();

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        btnLogin.setOnClickListener(v -> loginUser(etUsername.getText().toString(), etPassword.getText().toString()));

        btnSignup.setOnClickListener(v -> signupUser(etUsername.getText().toString(), etPassword.getText().toString()));
    }

    private void loginUser(String username, String password) {
        Log.i("LoginActivity", "Attempting to login user: " + username);
        ParseUser.logInInBackground(username, password, (user, e) -> {
            if (e != null) {
                // TODO: error handling
                Log.e("LoginActivity", "Issue with login!", e);
                return;
            }
            goMainActivity();
        });
    }

    private void signupUser(String username, String password) {
        Log.i("LoginActivity", "Attempting to signup user " + username);
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        // Invoke signUpInBackground
        user.signUpInBackground(e -> {
            if (e != null) {
                Log.e("LoginActivity", "Issue with signup", e);
                // TODO: error handling
                return;
            }
            goMainActivity();
        });
    }

    private void goMainActivity() {
        // TODO: possibly need splash screen here
        Intent i = new Intent(this, SplashScreen.class);
        startActivity(i);
        finish();
    }
}