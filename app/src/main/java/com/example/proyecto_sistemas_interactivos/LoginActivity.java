package com.example.proyecto_sistemas_interactivos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText edtCorreoLogin, edtContrasenaLogin;
    private Button btnIniciarSesionLogin, btnIrARegistro;

    // Constantes de prefs de usuario
    private static final String PREFS_USER = "user_prefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences prefs = getSharedPreferences(PREFS_USER, MODE_PRIVATE);

        // Si ya está logueado, saltamos directo al Home
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        if (isLoggedIn) {
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
            return;
        }

        edtCorreoLogin = findViewById(R.id.edtCorreoLogin);
        edtContrasenaLogin = findViewById(R.id.edtContrasenaLogin);
        btnIniciarSesionLogin = findViewById(R.id.btnIniciarSesionLogin);
        btnIrARegistro = findViewById(R.id.btnIrARegistro);

        btnIniciarSesionLogin.setOnClickListener(v -> {
            String email = edtCorreoLogin.getText().toString().trim();
            String password = edtContrasenaLogin.getText().toString().trim();

            if (email.isEmpty()) {
                edtCorreoLogin.setError("Ingresa tu correo");
                return;
            }
            if (password.isEmpty()) {
                edtContrasenaLogin.setError("Ingresa tu contraseña");
                return;
            }

            String savedEmail = prefs.getString(KEY_EMAIL, null);
            String savedPassword = prefs.getString(KEY_PASSWORD, null);

            if (savedEmail == null || savedPassword == null) {
                Toast.makeText(this, "No existe una cuenta. Por favor, regístrate.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            if (email.equals(savedEmail) && password.equals(savedPassword)) {
                prefs.edit().putBoolean(KEY_IS_LOGGED_IN, true).apply();
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });

        btnIrARegistro.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });
    }
}
