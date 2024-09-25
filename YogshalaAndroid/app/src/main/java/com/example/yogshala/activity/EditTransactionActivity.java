package com.example.yogshala.activity;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.yogshala.R;
import com.example.yogshala.activity.DetailedTransactionActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class EditTransactionActivity extends AppCompatActivity {

    private ImageView backBtn;
    private AppCompatButton btnUpdate;

    private RadioButton rbCash, rbOnline;

    private AutoCompleteTextView autoCompleteType, autoCompleteProgram;
    private TextInputEditText etName, etFromDate, etToDate, etMonthFee, etRemarks, etReceivedAmount;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference programDatabaseReference;
    private DatabaseReference transactionDatabaseReference;

    private ArrayAdapter<String> programAdapter;
    private List<String> programList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        // Hide the action bar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initialize Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance();
        programDatabaseReference = firebaseDatabase.getReference("programs");
        transactionDatabaseReference = firebaseDatabase.getReference("Transactions");

        // Initializing Views
        etName = findViewById(R.id.etName);
        etFromDate = findViewById(R.id.etFromDate);
        autoCompleteType = findViewById(R.id.autoCompleteType);
        autoCompleteProgram = findViewById(R.id.autoCompleteProgram);
        etToDate = findViewById(R.id.etToDate);
        etMonthFee = findViewById(R.id.etMonthFee);
        etReceivedAmount = findViewById(R.id.etReceivedAmount);
        etRemarks = findViewById(R.id.etRemarks);
        rbCash = findViewById(R.id.rbCash);
        rbOnline = findViewById(R.id.rbOnline);

        // Retrive client data from previous activity
        String id = getIntent().getStringExtra("transactionId");
        String clientName = getIntent().getStringExtra("clientName");
        String fromDate = getIntent().getStringExtra("fromDate");
        String toDate = getIntent().getStringExtra("toDate");
        String mode = getIntent().getStringExtra("paymentMode");
        String type = getIntent().getStringExtra("type");
        String monthFee = getIntent().getStringExtra("monthFee");
        String receivedAmount = getIntent().getStringExtra("receivedAmount");
        String program = getIntent().getStringExtra("program");
        String remark = getIntent().getStringExtra("remarks");

        // Set all dropdowns
        setupDropdowns();

        // Set views
        etName.setText(clientName != null ? clientName : "");
        etMonthFee.setText(monthFee != null ? monthFee : "");
        autoCompleteProgram.setText(program != null ? program : "");
        etFromDate.setText(fromDate != null ? fromDate : "");
        etToDate.setText(toDate != null ? toDate : "");
        etRemarks.setText(remark != null ? remark : "");
        etReceivedAmount.setText(receivedAmount != null ? receivedAmount : "");
        if (Objects.equals(mode, "Cash")) {
            rbCash.setChecked(true);
        } else if (Objects.equals(mode, "Online")) {
            rbOnline.setChecked(true);
        }
        autoCompleteType.setText(type != null ? type : "");
        etFromDate.setOnClickListener(v -> showFromDatePickerDialog());
        etToDate.setOnClickListener(v -> showToDatePickerDialogue());

        // handle back button
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Closes the current activity and returns to the previous one
            }
        });

        // Handling Update button click
        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(v -> updateTransaction(id));
    }

    private void setupDropdowns() {
        // Program Dropdown
        programList = new ArrayList<>();
        programAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, programList);
        autoCompleteProgram.setAdapter(programAdapter);
        autoCompleteProgram.setOnClickListener(view -> autoCompleteProgram.showDropDown());

        // Fetch programs from Firebase and populate the dropdown
        fetchProgramsFromDatabase();

        // Transaction Dropdown
        String[] typeOptions = {"Income"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, typeOptions);
        autoCompleteType.setAdapter(typeAdapter);
        autoCompleteType.setOnClickListener(view -> autoCompleteType.showDropDown());
    }

    private void fetchProgramsFromDatabase() {
        DatabaseReference programDatabaseReference = FirebaseDatabase.getInstance().getReference("Programs");
        programDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> programNames = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String programName = snapshot.getValue(String.class);
                    if (programName != null) {
                        programNames.add(programName);
                    }
                }
                ArrayAdapter<String> programAdapter = new ArrayAdapter<>(
                        EditTransactionActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        programNames
                );
                autoCompleteProgram.setAdapter(programAdapter);
                autoCompleteProgram.setOnClickListener(view -> autoCompleteProgram.showDropDown());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditTransactionActivity.this, "Failed to load programs", Toast.LENGTH_SHORT).show();
                Log.e("FetchPrograms", "Error fetching programs", databaseError.toException());
            }
        });
    }

    private void showFromDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialogfrom = new DatePickerDialog(EditTransactionActivity.this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format("%d-%02d-%02d", year1, (monthOfYear + 1), dayOfMonth);

                    etFromDate.setText(selectedDate);

                }, year, month, day);
        datePickerDialogfrom.show();
    }

    private void showToDatePickerDialogue() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialogto = new DatePickerDialog(EditTransactionActivity.this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format("%d-%02d-%02d", year1, (monthOfYear + 1), dayOfMonth);
                    etToDate.setText(selectedDate);
                }, year, month, day);
        datePickerDialogto.show();
    }

    private void updateTransaction(String transactionId) {
        String clientName = etName.getText().toString().trim();
        String fromDate = etFromDate.getText().toString().trim();
        String toDate = etToDate.getText().toString().trim();
        String type = autoCompleteType.getText().toString().trim();
        String monthfees = etMonthFee.getText().toString().trim();
        String receivedAmount = etReceivedAmount.getText().toString().trim();
        String program = autoCompleteProgram.getText().toString().trim();
        String remarks = etRemarks.getText().toString().trim();
        String paymentMode = rbCash.isChecked() ? "Cash" : "Online";

        HashMap TransactionDetails = new HashMap();
        TransactionDetails.put("clientName", clientName);
        TransactionDetails.put("fromDate", fromDate);
        TransactionDetails.put("toDate", toDate);
        TransactionDetails.put("type", type);
        TransactionDetails.put("monthFee", monthfees);
        TransactionDetails.put("receivedAmount", receivedAmount);
        TransactionDetails.put("program", program);
        TransactionDetails.put("remarks", remarks);
        TransactionDetails.put("paymentMode", paymentMode);

        transactionDatabaseReference.child(transactionId).updateChildren(TransactionDetails).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditTransactionActivity.this, "Transaction updated successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditTransactionActivity.this, DetailedTransactionActivity.class);
                intent.putExtra("transactionId", transactionId);
                intent.putExtra("clientName", clientName);
                intent.putExtra("fromDate", fromDate);
                intent.putExtra("toDate", toDate);
                intent.putExtra("paymentMode", paymentMode);
                intent.putExtra("type", type);
                intent.putExtra("monthFee", monthfees);
                intent.putExtra("receivedAmount", receivedAmount);
                intent.putExtra("program", program);
                intent.putExtra("remarks", remarks);
                startActivity(intent);
            } else {
                Toast.makeText(EditTransactionActivity.this, "Failed to update Transaction", Toast.LENGTH_SHORT).show();
            }
        });
    }
}