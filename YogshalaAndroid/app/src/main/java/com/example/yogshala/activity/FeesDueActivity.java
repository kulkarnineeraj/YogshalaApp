package com.example.yogshala.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.text.TextWatcher;
import android.widget.Toast;

import com.example.yogshala.Adapter.FeesDueAdapter;
import com.example.yogshala.R;
import com.example.yogshala.model.Transaction;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FeesDueActivity extends AppCompatActivity {
    private ImageView backBtn,menuBtn;

    private ListView listView;
    private FeesDueAdapter adapter;
    private ArrayList<Transaction> originalTransactionList;  // Full list of transactions
    private ArrayList<Transaction> transactionList;          // Filtered list of transactions

    private SimpleDateFormat dateFormat;

    private EditText etSearch;
    private TextInputEditText etFromDate, etToDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fees_due);

        // Hide the action bar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        backBtn = findViewById(R.id.backBtn);
        etSearch = findViewById(R.id.etSearch);
        menuBtn = findViewById(R.id.menuBtn);



        etFromDate = findViewById(R.id.etFromDate); // Initialize etFromDate
        etToDate = findViewById(R.id.etToDate);     // Initialize etToDate

        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        etFromDate.setOnClickListener(v -> showDatePickerDialog(etFromDate));
        etToDate.setOnClickListener(v -> showDatePickerDialog(etToDate));
        Button btnShow = findViewById(R.id.btnShow);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeesDueActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        listView = findViewById(R.id.listview);
        originalTransactionList = new ArrayList<>();  // Initialize the original list
        transactionList = new ArrayList<>();          // Initialize the filtered list

        // Initialize the adapter with the filtered list of transactions
        adapter = new FeesDueAdapter(this, transactionList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected transaction
                Transaction selectedTransaction = transactionList.get(position);

                // Create an intent to start SaveTransactionActivity
                Intent intent = new Intent(FeesDueActivity.this, SaveTransactionActivity.class);
                // Pass the transaction details to the new activity
                intent.putExtra("clientId", selectedTransaction.getClientId());
                intent.putExtra("clientName", selectedTransaction.getClientName());
                intent.putExtra("clientAmount", selectedTransaction.getMonthFee()); // Assuming MonthFee is passed
                intent.putExtra("clientProgram", selectedTransaction.getProgram()); // Assuming Program is passed

                startActivity(intent);
            }
        });

        // Get today's date
        String todayDate = getTodayDate();

        // Load the due fees data
        loadDueFees(todayDate);

        // Set up the button click event to filter the list
        btnShow.setOnClickListener(v -> {
            String fromDate = etFromDate.getText().toString();
            String toDate = etToDate.getText().toString();
            if (!fromDate.isEmpty() && !toDate.isEmpty()) {
                filterDueFees(fromDate, toDate);
            }
        });

        // Initialize the search EditText

        // Add TextWatcher to filter the list based on the search query
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the transaction list as the user types
                filterTransactionsByClientName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text changes
            }
        });


    }

    private void showPopupMenu(View view) {
        // Create a PopupMenu
        PopupMenu popupMenu = new PopupMenu(this, view);
        // Inflate the menu resource
        popupMenu.getMenuInflater().inflate(R.menu.menu_items_list, popupMenu.getMenu());

        // Set up the menu item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.menu_refresh) {
                    Intent intent = new Intent(FeesDueActivity.this, FeesDueActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.menu_byName) {
                    // Order by Client Name alphabetically
                    Collections.sort(transactionList, (t1, t2) -> t1.getClientName().compareToIgnoreCase(t2.getClientName()));
                    adapter.notifyDataSetChanged();
                    Toast.makeText(FeesDueActivity.this, "Ordered by Client Name", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.menu_byProgram) {
                    // Order by Program
                    Collections.sort(transactionList, (t1, t2) -> t1.getProgram().compareToIgnoreCase(t2.getProgram()));
                    adapter.notifyDataSetChanged();
                    Toast.makeText(FeesDueActivity.this, "Ordered by Program", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false;
                }
            }
        });

        // Show the PopupMenu
        popupMenu.show();
    }

    //For Searching Client
    private void filterTransactionsByClientName(String query) {
        ArrayList<Transaction> filteredList = new ArrayList<>();
        for (Transaction transaction : originalTransactionList) {
            if (transaction.getClientName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(transaction);
            }
        }

        transactionList.clear();  // Clear the current displayed list
        transactionList.addAll(filteredList);  // Add the filtered data to it
        adapter.notifyDataSetChanged();  // Notify the adapter to refresh the ListView
    }


    private void filterDueFees(String fromDate, String toDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        ArrayList<Transaction> filteredList = new ArrayList<>();
        for (Transaction transaction : originalTransactionList) { // Use the original list here
            try {
                Date transactionDate = sdf.parse(transaction.getToDate());
                Date start = sdf.parse(fromDate);
                Date end = sdf.parse(toDate);

                if (transactionDate != null && start != null && end != null) {
                    if (!transactionDate.before(start) && !transactionDate.after(end)) {
                        filteredList.add(transaction);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Update the adapter with the filtered list
        transactionList.clear();  // Clear the current displayed list
        transactionList.addAll(filteredList);  // Add filtered data to it
        adapter.notifyDataSetChanged();
    }

    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    editText.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private String getTodayDate() {
        // Define the date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // Get today's date
        return sdf.format(new Date());
    }

    private void loadDueFees(String todayDate) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Transactions");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                originalTransactionList.clear();
                transactionList.clear(); // Clear the current list before adding new data
                Map<String, Transaction> latestTransactions = new HashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Transaction transaction = snapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        String clientId = transaction.getClientId();
                        String toDate = transaction.getToDate();

                        // Update if this is the first transaction for the client or if this transaction is later
                        if (!latestTransactions.containsKey(clientId) ||
                                isLaterDate(toDate, latestTransactions.get(clientId).getToDate())) {
                            latestTransactions.put(clientId, transaction);
                        }
                    }
                }

                // Now filter and add transactions where toDate is less than today's date
                for (Transaction transaction : latestTransactions.values()) {
                    if (isEarlierDate(transaction.getToDate(), todayDate)) {
                        originalTransactionList.add(transaction);  // Add to the original list
                    }
                }

                // Copy original list to transactionList for displaying initially
                transactionList.addAll(originalTransactionList);
                adapter.notifyDataSetChanged();
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

            // Return true if d1 is after d2
            return d1 != null && d2 != null && d1.after(d2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isEarlierDate(String date1, String date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);

            // Return true if d1 is before d2
            return d1 != null && d2 != null && d1.before(d2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
