package com.example.yogshala.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yogshala.R;

import com.example.yogshala.Adapter.ProgramAdapter;
import com.example.yogshala.model.Program;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
// Import listeners
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddProgramActivity extends AppCompatActivity {

    private EditText etProgram;
    private ImageView addBtn;
    private ListView listViewProgram;

    private List<Program> programList;
    private ProgramAdapter adapter;

    private ImageView backBtn;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_program); // Ensure this is the correct layout

        // Hide the action bar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initialize UI components
        etProgram = findViewById(R.id.etProgram);
        addBtn = findViewById(R.id.addBtn);
        listViewProgram = findViewById(R.id.listview);


        //Button Clicks from here
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddProgramActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Initialize data structures
        programList = new ArrayList<>();
        adapter = new ProgramAdapter(this, programList);
        listViewProgram.setAdapter(adapter);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Programs");

        // Set click listener for the add button
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProgram();
            }
        });

        // Fetch existing programs from Firebase
        fetchPrograms();
    }

    private void addProgram() {
        String programName = etProgram.getText().toString().trim();

        if (!programName.isEmpty()) {
            String id = databaseReference.push().getKey();
            if (id != null) {
                // Create a new Program object
                Program program = new Program(id, programName);
                // Store the program in Firebase
                databaseReference.child(id).setValue(programName)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Program added", Toast.LENGTH_SHORT).show();
                            etProgram.setText(""); // Clear the input field
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to add program", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Failed to generate ID", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter a program name", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchPrograms() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                programList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String id = dataSnapshot.getKey();
                    String name = dataSnapshot.getValue(String.class);
                    Program program = new Program(id, name);
                    programList.add(program);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AddProgramActivity.this, "Failed to load programs", Toast.LENGTH_SHORT).show();
            }
        });
    }
}