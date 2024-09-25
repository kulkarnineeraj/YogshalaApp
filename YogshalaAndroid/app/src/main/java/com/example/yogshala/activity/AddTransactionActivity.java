package com.example.yogshala.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.yogshala.Adapter.ClientNamesAdapter;
import com.example.yogshala.model.Client;
import com.example.yogshala.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddTransactionActivity extends AppCompatActivity {

    private ImageView backBtn;

    private ListView listView;
    private ClientNamesAdapter clientNamesAdapter;
    private List<Client> clientList;
    private List<Client> filteredClientList; // List to hold filtered clients

    private EditText etSearch;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // Hide the action bar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        etSearch = findViewById(R.id.etSearch);
        listView = findViewById(R.id.listview);
        clientList = new ArrayList<>();
        filteredClientList = new ArrayList<>();
        clientNamesAdapter = new ClientNamesAdapter(this, filteredClientList);  // Use the filtered list for the adapter
        listView.setAdapter(clientNamesAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("client");

        fetchClients();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Client selectedClient = clientNamesAdapter.getClient(position);
                Intent intent = new Intent(AddTransactionActivity.this, SaveTransactionActivity.class);
                intent.putExtra("clientId", selectedClient.getId());  // Assuming `getId()` method exists in `Client` class
                intent.putExtra("clientName", selectedClient.getFirstName() + " " + selectedClient.getLastName());
                intent.putExtra("clientAmount", selectedClient.getAmount());
                intent.putExtra("clientProgram", selectedClient.getProgram());

                startActivity(intent);
            }
        });

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTransactionActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchClients(s.toString());  // Call search function on text change
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    private void fetchClients() {
        databaseReference.orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clientList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Client client = snapshot.getValue(Client.class);
                    if (client != null && client.getDate() != null) {
                        clientList.add(client);
                        Log.d("ClientFeesActivity", "Client added: " + client.getFirstName());
                    } else {
                        Log.d("ClientFeesActivity", "Enquiry is null or has no date");
                    }
                }
                Collections.sort(clientList, (o1, o2) -> {
                    if (o1.getDate() == null || o2.getDate() == null) {
                        return 0;
                    }
                    return o2.getDate().compareTo(o1.getDate());
                });
                filteredClientList.clear();
                filteredClientList.addAll(clientList);  // Initially display all clients
                clientNamesAdapter.notifyDataSetChanged();  // Notify adapter of data changes
                Log.d("AddTransactionActivity", "Total Enquiries: " + clientList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AddTransactionActivity", "Failed to retrieve data", databaseError.toException());
            }
        });
    }

    private void searchClients(String searchText) {
        // Clear the filtered list before adding new results
        filteredClientList.clear();

        if (searchText.isEmpty()) {
            // If the search text is empty, display all clients
            filteredClientList.addAll(clientList);
        } else {
            // Iterate through the original client list
            for (Client client : clientList) {
                // Check if either the first name or last name contains the search text
                if (client.getFirstName().toLowerCase().contains(searchText.toLowerCase()) ||
                        client.getLastName().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredClientList.add(client);  // Add the matching client to the filtered list
                }
            }
        }

        // Sort the filtered list by date (most recent first)
        Collections.sort(filteredClientList, (o1, o2) -> {
            if (o1.getDate() == null || o2.getDate() == null) {
                return 0;
            }
            return o2.getDate().compareTo(o1.getDate());
        });

        // Notify the adapter that the data has changed so it can update the ListView
        clientNamesAdapter.notifyDataSetChanged();
    }
}
