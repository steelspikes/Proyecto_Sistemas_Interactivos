package com.example.proyecto_sistemas_interactivos;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddDependentActivity extends AppCompatActivity {

    private EditText edtDependentEmail;
    private Button btnSendRequest;
    private TextView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dependent);

        edtDependentEmail = findViewById(R.id.edtDependentEmail);
        btnSendRequest = findViewById(R.id.btnSendRequest);
        btnBack = findViewById(R.id.btnBack);

        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtDependentEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    edtDependentEmail.setError("Por favor, ingresa un correo");
                    return;
                }
                // Lógica para enviar la solicitud
                Toast.makeText(AddDependentActivity.this, "Solicitud enviada a " + email, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
