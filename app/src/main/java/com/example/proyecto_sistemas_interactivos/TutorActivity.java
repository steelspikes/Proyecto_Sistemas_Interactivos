package com.example.proyecto_sistemas_interactivos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TutorActivity extends AppCompatActivity {

    private RecyclerView dependentsRecyclerView;
    private DependentAdapter adapter;
    private List<Dependent> dependentList;
    private Button btnAddDependent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor);

        dependentsRecyclerView = findViewById(R.id.dependentsRecyclerView);
        btnAddDependent = findViewById(R.id.btnAddDependent);

        dependentList = new ArrayList<>();
        // Add sample data
        dependentList.add(new Dependent("Usuario 1"));
        dependentList.add(new Dependent("Usuario 2"));
        dependentList.add(new Dependent("Usuario 3"));

        adapter = new DependentAdapter(dependentList, new DependentAdapter.OnDependentClickListener() {
            @Override
            public void onDependentClick(Dependent dependent) {
                Intent intent = new Intent(TutorActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        dependentsRecyclerView.setAdapter(adapter);

        btnAddDependent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutorActivity.this, AddDependentActivity.class);
                startActivity(intent);
            }
        });
    }

    // Dependent data model
    public static class Dependent {
        private String name;

        public Dependent(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    // RecyclerView Adapter
    public static class DependentAdapter extends RecyclerView.Adapter<DependentAdapter.ViewHolder> {

        private List<Dependent> dependents;
        private OnDependentClickListener listener;

        public interface OnDependentClickListener {
            void onDependentClick(Dependent dependent);
        }

        public DependentAdapter(List<Dependent> dependents, OnDependentClickListener listener) {
            this.dependents = dependents;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dependent, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Dependent dependent = dependents.get(position);
            holder.dependentNameTextView.setText(dependent.getName());
            holder.itemView.setOnClickListener(v -> listener.onDependentClick(dependent));
        }

        @Override
        public int getItemCount() {
            return dependents.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView dependentNameTextView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                dependentNameTextView = itemView.findViewById(R.id.dependentNameTextView);
            }
        }
    }
}
