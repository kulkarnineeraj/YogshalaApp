package com.example.yogshala.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextWatcher;

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

import com.example.yogshala.Adapter.ClientAdapter;

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


public class ClientListActivity extends AppCompatActivity {

    private ImageView backBtn;

    private EditText etSearch;

    private ListView listView;
    private ClientAdapter clientAdapter;
    private List<Client> clientList;

    private List<Client> filteredClientList;   // List to hold filtered clients
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);

        // Hide the action bar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        listView = findViewById(R.id.listview);
        clientList = new ArrayList<>();
        filteredClientList = new ArrayList<>();
        clientAdapter = new ClientAdapter(this, filteredClientList); // Use filtered list for the adapter
        listView.setAdapter(clientAdapter);


        databaseReference = FirebaseDatabase.getInstance().getReference("client");

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClientListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        fetchClients();

        etSearch = findViewById(R.id.etSearch);

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

        // Set OnItemClickListener for ListView items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Client client = clientList.get(position);

                Intent intent = new Intent(ClientListActivity.this, DetailedDataActivity.class);
                intent.putExtra("clientId", client.getId());
                intent.putExtra("name", client.getFirstName() + " " + client.getLastName());

                intent.putExtra("enquiryDate", client.getDate());
                intent.putExtra("program", client.getProgram());

                intent.putExtra("status", client.getStatus());



                intent.putExtra("birthDate", client.getBirthDate() );
                intent.putExtra("mobile", client.getPhone());
                intent.putExtra("amount", client.getAmount());
                intent.putExtra("email", client.getEmail());
                intent.putExtra("address", client.getAddress());
                startActivity(intent);
            }
        });
    }

    private void fetchClients() {
        // Fetch all clients initially and display them
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clientList.clear();
                filteredClientList.clear();  // Clear both lists
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Client client = snapshot.getValue(Client.class);
                    if (client != null) {
                        clientList.add(client);
                    }
                }
                Collections.sort(clientList, (o1, o2) -> {
                    if (o1.getDate() == null || o2.getDate() == null) {
                        return 0;
                    }
                    return o2.getDate().compareTo(o1.getDate());
                });
                filteredClientList.addAll(clientList);  // Initially display all clients
                clientAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("EnquiryListActivity", "Failed to retrieve data", databaseError.toException());
            }
        });
    }

    private void searchClients(String searchText) {
        filteredClientList.clear(); // Clear the filtered list before adding filtered items
        for (Client client : clientList) {
            if (client.getFirstName().toLowerCase().contains(searchText.toLowerCase()) ||
                    client.getLastName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredClientList.add(client);
            }
        }
        clientAdapter.notifyDataSetChanged();
    }
}

