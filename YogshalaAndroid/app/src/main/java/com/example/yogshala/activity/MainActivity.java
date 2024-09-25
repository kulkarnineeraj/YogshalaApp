package com.example.yogshala.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yogshala.R;
import com.example.yogshala.model.Client;
import com.example.yogshala.model.Transaction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView totalTV,  joinedTV, leftTV;

    private RelativeLayout feesDueRL;
    private DatabaseReference clientDatabaseReference;
    private ConstraintLayout CL1, CL2, CL3, CL4, CL5, CL6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide the action bar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Button Clicks from here
        feesDueRL = findViewById(R.id.feesDueRL);
        feesDueRL.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FeesDueActivity.class)));


        CL1 = findViewById(R.id.CL1);
        CL1.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddClientActivity.class)));

        CL2 = findViewById(R.id.CL2);
        CL2.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddProgramActivity.class)));

        CL3 = findViewById(R.id.CL3);
        CL3.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AllTransactionActivity.class)));

        CL4 = findViewById(R.id.CL4);
        CL4.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ClientListActivity.class)));

        CL5 = findViewById(R.id.CL5);
        CL5.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddTransactionActivity.class)));

        CL6 = findViewById(R.id.CL6);
        CL6.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FeesDueActivity.class)));

        totalTV = findViewById(R.id.totalTV);

        joinedTV = findViewById(R.id.joinedTV);
        leftTV = findViewById(R.id.leftTV);

        clientDatabaseReference = FirebaseDatabase.getInstance().getReference("client");

        loadClientStatusCounts();
        loadActiveAndDueCounts();
    }

    private void loadClientStatusCounts() {
        clientDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalCount = 0;

                int joinedCount = 0;
                int leftCount = 0;

                for (DataSnapshot clientSnapshot : snapshot.getChildren()) {
                    Client client = clientSnapshot.getValue(Client.class);
                    if (client != null) {
                        totalCount++;
                        String status = client.getStatus();

                        // Count based on status
                       if ("Joined".equalsIgnoreCase(status)) {
                            joinedCount++;
                        } else if ("Left".equalsIgnoreCase(status)) {
                            leftCount++;
                        }
                    }
                }
                totalTV.setText(String.valueOf(totalCount));

                joinedTV.setText(String.valueOf(joinedCount));
                leftTV.setText(String.valueOf(leftCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadActiveAndDueCounts() {
        DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference("Transactions");

        transactionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Transaction> latestTransactions = new HashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Transaction transaction = snapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        String clientId = transaction.getClientId();
                        String toDate = transaction.getToDate();

                        // If this client has no transactions yet or the current transaction is later, update it
                        if (!latestTransactions.containsKey(clientId) ||
                                isLaterDate(toDate, latestTransactions.get(clientId).getToDate())) {
                            latestTransactions.put(clientId, transaction);
                        }
                    }
                }

                // Now that we have the latest transactions, we can count them as Active or Due
                int activeMedical = 0, dueMedical = 0;
                int activeOffline = 0, dueOffline = 0;
                int activeOnline = 0, dueOnline = 0;

                String todayDate = getTodayDate();

                for (Transaction transaction : latestTransactions.values()) {
                    String toDate = transaction.getToDate();
                    String program = transaction.getProgram();

                    boolean isActive = isLaterDate(toDate, todayDate);

                    if ("Medical".equals(program)) {
                        if (isActive) activeMedical++;
                        else dueMedical++;
                    } else if ("Offline".equals(program)) {
                        if (isActive) activeOffline++;
                        else dueOffline++;
                    } else if ("Online".equals(program)) {
                        if (isActive) activeOnline++;
                        else dueOnline++;
                    }
                }

                // Update your TextViews here
                updateTable(activeMedical, dueMedical, activeOffline, dueOffline, activeOnline, dueOnline);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private boolean isLaterDate(String date1, String date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);

            // Return true if d1 is today or after today (inclusive)
            return d1 != null && d2 != null && (d1.equals(d2) || d1.after(d2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void updateTable(int activeMedical, int dueMedical, int activeNormal, int dueNormal, int activeOnline, int dueOnline) {
        TextView activeMedicalTv = findViewById(R.id.activeMedicalTv);
        TextView dueMedicalTv = findViewById(R.id.dueMedicalTv);
        TextView activeNormalTv = findViewById(R.id.activeNormalTv);
        TextView dueNormalTv = findViewById(R.id.dueNormalTv);
        TextView activeOnlineTv = findViewById(R.id.activeOnlineTv);
        TextView dueOnlineTv = findViewById(R.id.dueOnlineTv);

        activeMedicalTv.setText(String.valueOf(activeMedical));
        dueMedicalTv.setText(String.valueOf(dueMedical));
        activeNormalTv.setText(String.valueOf(activeNormal));
        dueNormalTv.setText(String.valueOf(dueNormal));
        activeOnlineTv.setText(String.valueOf(activeOnline));
        dueOnlineTv.setText(String.valueOf(dueOnline));
    }
}
