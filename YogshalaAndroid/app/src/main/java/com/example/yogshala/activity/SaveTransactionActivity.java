package com.example.yogshala.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.yogshala.R;
import com.example.yogshala.model.Transaction;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class SaveTransactionActivity extends AppCompatActivity {

    private RadioButton rbCash,rbOnline;

    private AutoCompleteTextView autoCompleteType,autoCompleteProgram;
    private TextInputEditText etName, etFromDate, etToDate, etMonthFee, etRemarks,etReceivedAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_transaction);

        // Hide the action bar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initializing Views
        autoCompleteType = findViewById(R.id.autoCompleteType);
        autoCompleteProgram = findViewById(R.id.autoCompleteProgram);
        etName = findViewById(R.id.etName);
        etFromDate = findViewById(R.id.etFromDate);

        // Format today's date
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = today.format(formatter);

        // Set today's date as the text of the TextInputEditText
        etFromDate.setText(formattedDate);

        etToDate = findViewById(R.id.etToDate);
        etMonthFee = findViewById(R.id.etMonthFee);

        etReceivedAmount = findViewById(R.id.etReceivedAmount);

        etRemarks = findViewById(R.id.etRemarks);

        rbCash = findViewById(R.id.rbCash);
        rbOnline = findViewById(R.id.rbOnline);

        // Retrieving client data passed from the previous activity
        String clientId = getIntent().getStringExtra("clientId");
        String clientName = getIntent().getStringExtra("clientName");
        String clientAmount = getIntent().getStringExtra("clientAmount");
        String clientProgram= getIntent().getStringExtra("clientProgram");


        // Set client data to the views
        etName.setText(clientName != null ? clientName : "");
        etMonthFee.setText(clientAmount != null ? clientAmount : "");
        autoCompleteProgram.setText(clientProgram != null ? clientProgram : "");



        etFromDate.setOnClickListener(v -> showFromDatePickerDialog());
        etToDate.setOnClickListener(v -> showToDatePickerDialogue());


        // Transaction Dropdown
        String[] typeOptions = {"Income"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, typeOptions);
        autoCompleteType.setAdapter(typeAdapter);
        autoCompleteType.setOnClickListener(view -> autoCompleteType.showDropDown());

        // Handling Save button click
        AppCompatButton btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveTransaction(clientId));
    }

    private void saveTransaction(String clientId) {
        String clientName = etName.getText().toString();

        // Formatting the fromDate
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateFormatted = "";
        String toDateFormatted = "";
        try {
            Date fromDate = inputFormat.parse(etFromDate.getText().toString().trim());
            Date toDate = inputFormat.parse(etToDate.getText().toString().trim());
            fromDateFormatted = outputFormat.format(fromDate);
            toDateFormatted = outputFormat.format(toDate);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        String type = autoCompleteType.getText().toString();
        String monthFee = etMonthFee.getText().toString();
        String receivedAmount = etReceivedAmount.getText().toString();
        String program = autoCompleteProgram.getText().toString();
        String remarks = etRemarks.getText().toString();
        String paymentMode = rbCash.isChecked() ? "Cash" : "Online";
        DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference("Transactions");
        String transactionId = transactionRef.push().getKey();

        Transaction transaction = new Transaction(transactionId, clientId, clientName, fromDateFormatted, toDateFormatted, type, monthFee, receivedAmount, program, remarks,paymentMode);
        transactionRef.child(transactionId).setValue(transaction)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SaveTransactionActivity.this, "Transaction saved successfully", Toast.LENGTH_SHORT).show();
                        finish();  // Close the activity
                    } else {
                        Toast.makeText(SaveTransactionActivity.this, "Failed to save transaction", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void showFromDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialogfrom = new DatePickerDialog(SaveTransactionActivity.this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                    etFromDate.setText(selectedDate);

                }, year, month, day);
        datePickerDialogfrom.show();
    }

    private void showToDatePickerDialogue() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialogto = new DatePickerDialog(SaveTransactionActivity.this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;

                    etToDate.setText(selectedDate);
                }, year, month, day);
        datePickerDialogto.show();
    }
}