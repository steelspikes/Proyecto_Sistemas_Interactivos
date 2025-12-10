package com.example.proyecto_sistemas_interactivos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText edtNombreTutor, edtNombreUsuario, edtTelefono1, edtTelefono2, edtCorreo, edtContrasena, edtConfirmarContrasena;
    private Button btnCrearCuenta, btnIrALogin, btnIniciarSesionTutor;

    // Constantes de prefs de usuario
    private static final String PREFS_USER = "user_prefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtCorreo = findViewById(R.id.edtCorreo);
        edtContrasena = findViewById(R.id.edtContrasena);
        edtConfirmarContrasena = findViewById(R.id.edtConfirmarContrasena);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        btnIrALogin = findViewById(R.id.btnIrALogin);
        btnIniciarSesionTutor = findViewById(R.id.btnIniciarSesionTutor);

        btnCrearCuenta.setOnClickListener(v -> {
            String email = edtCorreo.getText().toString().trim();
            String password = edtContrasena.getText().toString().trim();
            String password2 = edtConfirmarContrasena.getText().toString().trim();

            if (email.isEmpty()) {
                edtCorreo.setError("Ingresa un correo");
                return;
            }
            if (password.isEmpty()) {
                edtContrasena.setError("Ingresa una contraseña");
                return;
            }
            if (!password.equals(password2)) {
                edtConfirmarContrasena.setError("Las contraseñas no coinciden");
                return;
            }

            SharedPreferences prefs = getSharedPreferences(PREFS_USER, MODE_PRIVATE);
            prefs.edit()
                    .putString(KEY_EMAIL, email)
                    .putString(KEY_PASSWORD, password)
                    .putBoolean(KEY_IS_LOGGED_IN, true)
                    .apply();

            Toast.makeText(this, "Cuenta creada", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        });

        btnIrALogin.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        });

        btnIniciarSesionTutor.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, TutorActivity.class);
            startActivity(i);
            finish();
        });
    }
}
