package com.example.yogshala.activity;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.example.yogshala.Adapter.FeesDueAdapter;
import com.example.yogshala.Adapter.TrsansactionAdapter;
import com.example.yogshala.R;
import com.example.yogshala.model.Transaction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class AllTransactionActivity extends AppCompatActivity {

    private ImageView backBtn,menuBtn;
    private ListView listView;
    private TrsansactionAdapter adapter;
    private ArrayList<Transaction> transactionsList;
    private ArrayList<Transaction> filteredTransactionsList;
    private DatabaseReference databaseReference;

    private EditText etFromDate, etToDate,etSearch;
    private Button btnShow;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_transaction);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        listView = findViewById(R.id.listview);
        transactionsList = new ArrayList<>();
        filteredTransactionsList = new ArrayList<>();
        adapter = new TrsansactionAdapter(this, filteredTransactionsList);
        listView.setAdapter(adapter);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AllTransactionActivity.this, MainActivity.class);
            startActivity(intent);
        });

        menuBtn = findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });


        etFromDate = findViewById(R.id.etFromDate);
        etToDate = findViewById(R.id.etToDate);
        btnShow = findViewById(R.id.btnShow);
        etSearch = findViewById(R.id.etSearch);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        etFromDate.setOnClickListener(v -> showDatePickerDialog(etFromDate));
        etToDate.setOnClickListener(v -> showDatePickerDialog(etToDate));

        databaseReference = FirebaseDatabase.getInstance().getReference("Transactions");

        btnShow.setOnClickListener(v -> filterTransactionsByDate());

        // Load all transactions initially
        loadAllTransactions();

        // Set OnItemClickListener for ListView items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Inside your Adapter's onClickListener
                Transaction transaction = filteredTransactionsList.get(position);
                Intent intent = new Intent(AllTransactionActivity.this,DetailedTransactionActivity.class);

                intent.putExtra("transactionId", transaction.getTransactionId());
                intent.putExtra("clientName", transaction.getClientName());
                intent.putExtra("fromDate", transaction.getFromDate());
                intent.putExtra("toDate", transaction.getToDate());
                intent.putExtra("paymentMode", transaction.getPaymentMode());
                intent.putExtra("type", transaction.getType());
                intent.putExtra("monthFee", transaction.getMonthFee());
                intent.putExtra("receivedAmount", transaction.getReceivedAmount());
                intent.putExtra("remarks", transaction.getRemarks());
                intent.putExtra("program", transaction.getProgram());
                startActivity(intent);

            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchClients(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
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
                    Intent intent = new Intent(AllTransactionActivity.this, AllTransactionActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.menu_byName) {
                    // Order by Client Name alphabetically
                    Collections.sort(filteredTransactionsList, (t1, t2) -> t1.getClientName().compareToIgnoreCase(t2.getClientName()));
                    adapter.notifyDataSetChanged();
                    Toast.makeText(AllTransactionActivity.this, "Ordered by Client Name", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.menu_byProgram) {
                    // Order by Program
                    Collections.sort(filteredTransactionsList, (t1, t2) -> t1.getProgram().compareToIgnoreCase(t2.getProgram()));
                    adapter.notifyDataSetChanged();
                    Toast.makeText(AllTransactionActivity.this, "Ordered by Program", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false;
                }
            }
        });

        // Show the PopupMenu
        popupMenu.show();
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

    private void loadAllTransactions() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactionsList.clear(); // Clear the list before adding new data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Transaction transaction = snapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        transactionsList.add(transaction);
                    }
                }

                // Initially display all transactions
                filteredTransactionsList.clear();
                filteredTransactionsList.addAll(transactionsList);
                Collections.sort(filteredTransactionsList, (t1, t2) -> t2.getToDate().compareTo(t1.getToDate()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AllTransactionActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterTransactionsByDate() {
        String fromDateStr = etFromDate.getText().toString();
        String toDateStr = etToDate.getText().toString();
        Date fromDate = null, toDate = null;

        try {
            fromDate = dateFormat.parse(fromDateStr);
            toDate = dateFormat.parse(toDateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Date finalFromDate = fromDate;
        Date finalToDate = toDate;

        filteredTransactionsList.clear();

        for (Transaction transaction : transactionsList) {
            try {
                Date transactionDate = dateFormat.parse(transaction.getFromDate());
                if (finalFromDate != null && finalToDate != null && transactionDate != null) {
                    if (!transactionDate.before(finalFromDate) && !transactionDate.after(finalToDate)) {
                        filteredTransactionsList.add(transaction);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Collections.sort(filteredTransactionsList, (t1, t2) -> t2.getFromDate().compareTo(t1.getFromDate()));
        adapter.notifyDataSetChanged();
    }

    private void searchClients(String searchText) {
        filteredTransactionsList.clear(); // Clear the filtered list before adding filtered items
        for (Transaction transaction : transactionsList) { // Iterate over the original transactionsList
            if (transaction.getClientName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredTransactionsList.add(transaction); // Add matching items to the filtered list
            }
        }
        adapter.notifyDataSetChanged(); // Notify the adapter of the changes
    }

}
