package com.example.proyecto_sistemas_interactivos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText edtCorreo, edtContrasena, edtConfirmar;
    private Button btnCrearCuenta, btnIniciarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // pantalla CREAR CUENTA

        edtCorreo = findViewById(R.id.edtCorreo);
        edtContrasena = findViewById(R.id.edtContrasena);
        edtConfirmar = findViewById(R.id.edtConfirmarContrasena);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        btnIniciarSesion = findViewById(R.id.btnIrALogin);


        btnCrearCuenta.setOnClickListener(v -> crearCuenta());

        // Ir a pantalla de inicio de sesión
        btnIniciarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void crearCuenta() {
        String correo = edtCorreo.getText().toString().trim();
        String pass = edtContrasena.getText().toString().trim();
        String pass2 = edtConfirmar.getText().toString().trim();

        if (correo.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {
            Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(pass2)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Guardar usuario en SharedPreferences (solo para demo)
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        prefs.edit()
                .putString("user_email", correo)
                .putString("user_password", pass)
                .apply();

        Toast.makeText(this, "Cuenta creada. Ahora inicia sesión.", Toast.LENGTH_LONG).show();

        // Ir a Login y cerrar esta pantalla
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
