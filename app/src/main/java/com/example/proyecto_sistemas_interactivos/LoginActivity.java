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
    private Button btnIniciarSesionLogin, btnCrearCuentaLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // pantalla BIENVENIDO DE VUELTA

        edtCorreoLogin = findViewById(R.id.edtCorreoLogin);
        edtContrasenaLogin = findViewById(R.id.edtContrasenaLogin);
        btnIniciarSesionLogin = findViewById(R.id.btnIniciarSesionLogin);
        btnCrearCuentaLogin = findViewById(R.id.btnIrARegistro);

        btnIniciarSesionLogin.setOnClickListener(v -> hacerLogin());

        // Ir a crear cuenta
        btnCrearCuentaLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void hacerLogin() {
        String correo = edtCorreoLogin.getText().toString().trim();
        String pass = edtContrasenaLogin.getText().toString().trim();

        if (correo.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Ingresa correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String savedEmail = prefs.getString("user_email", null);
        String savedPass = prefs.getString("user_password", null);

        if (savedEmail == null || savedPass == null) {
            Toast.makeText(this, "No hay cuenta registrada. Crea una primero.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!correo.equals(savedEmail) || !pass.equals(savedPass)) {
            Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Login correcto -> ir a pantalla Plan para hoy
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); // para que al darle atrás no regrese al login
    }
}
