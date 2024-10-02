package com.example.yogshala.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yogshala.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailedDataActivity extends AppCompatActivity {

    private TextView tvName, tvArea, tvEnquiryDate, tvProgramDetail, tvAge2, tvStatus2, tvInterest2,tvBirthDate, tvRefferedBy2 , tvAmountDetail;
    private TextView tvParent2, tvMobile2, tvEmail2, tvAddress2, tvRefferalName;

    private ImageView backBtn,menuBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_data);

        // Hide the action bar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        menuBtn = findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailedDataActivity.this, ClientListActivity.class);
                startActivity(intent);
            }
        });


        // Initialize views
        tvName = findViewById(R.id.tvName);

        tvEnquiryDate = findViewById(R.id.tvEnquiryDate);
        tvProgramDetail = findViewById(R.id.tvProgramDetail);

        tvStatus2 = findViewById(R.id.tvStatus2);


        tvBirthDate = findViewById(R.id.tvBirthDate2);
        tvMobile2 = findViewById(R.id.tvMobile2);
        tvAmountDetail = findViewById(R.id.tvAmountDetail);
        tvEmail2 = findViewById(R.id.tvEmail2);
        tvAddress2 = findViewById(R.id.tvAddress2);


        // Get data from intent
        Intent intent = getIntent();

        String name = intent.getStringExtra("name");

        String enquiryDate = intent.getStringExtra("enquiryDate");
        String program = intent.getStringExtra("program");

        String status = intent.getStringExtra("status");


        String birthDate = intent.getStringExtra("birthDate");
        String mobile = intent.getStringExtra("mobile");
        String amount = intent.getStringExtra("amount");
        String email = intent.getStringExtra("email");
        String address = intent.getStringExtra("address");

        // Set data to views
        tvName.setText(name);

        tvEnquiryDate.setText(enquiryDate);
        tvProgramDetail.setText(program);

        tvStatus2.setText(status);



        tvBirthDate.setText(birthDate);
        tvMobile2.setText(mobile);
        tvAmountDetail.setText(amount);
        tvEmail2.setText(email);
        tvAddress2.setText(address);

        tvMobile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text from tvMobile2
                String textToCopy = tvMobile2.getText().toString();

                // Get the clipboard system service
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                // Create a clip with the text
                ClipData clip = ClipData.newPlainText("Mobile Number", textToCopy);

                // Set the clip to the clipboard
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);

                    // Show a toast message to confirm the copy action
                    Toast.makeText(getApplicationContext(), "Mobile number copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showPopupMenu(View view) {
        Intent intent = getIntent();
        String clientId = intent.getStringExtra("clientId");

        // Create a PopupMenu
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_items, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.menu_edit) {
                    // Handle Edit action
                    Intent intent = new Intent(DetailedDataActivity.this, EditClientDetails.class);

                    // Pass all data needed to edit the client
                    intent.putExtra("id", getIntent().getStringExtra("clientId")); // Pass the client ID
                    String[] nameParts = getIntent().getStringExtra("name").split(" ");
                    if (nameParts.length > 1) {
                        intent.putExtra("fname", nameParts[0]); // First name
                        intent.putExtra("lname", nameParts[1]); // Last name
                    } else {
                        intent.putExtra("fname", nameParts[0]); // First name
                        intent.putExtra("lname", "");           // Handle case where there's no last name
                    }
                    intent.putExtra("enquiryDate", getIntent().getStringExtra("enquiryDate"));
                    intent.putExtra("program", getIntent().getStringExtra("program"));
                    intent.putExtra("status", getIntent().getStringExtra("status"));
                    intent.putExtra("birthDate", getIntent().getStringExtra("birthDate"));
                    intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
                    intent.putExtra("amount", getIntent().getStringExtra("amount"));
                    intent.putExtra("email", getIntent().getStringExtra("email"));
                    intent.putExtra("address", getIntent().getStringExtra("address"));

                    // Start EditClientDetails activity
                    startActivity(intent);
                } else if (item.getItemId() == R.id.menu_delete) {
                    showDeleteConfirmationDialog();
                    return true;
                }
                return false;
            }

            // Show confirmation dialog before deletion
            private void showDeleteConfirmationDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailedDataActivity.this);
                builder.setTitle("Delete Item");
                builder.setMessage("Are you sure you want to delete this item?");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem(); // Call delete function
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }

            // Delete client from Firebase
            private void deleteItem() {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("client");

                if (clientId != null) {
                    databaseReference.child(clientId).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(DetailedDataActivity.this, "Client deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(DetailedDataActivity.this, "Failed to delete client", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Optional: Show Snackbar with undo option
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Client deleted", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            undoDelete();  // Undo the deletion (this part needs actual logic)
                        }
                    });
                    snackbar.show();
                }
            }

            // Placeholder for undo delete logic
            private void undoDelete() {
                Toast.makeText(DetailedDataActivity.this, "Undo deletion is not implemented", Toast.LENGTH_SHORT).show();
            }
        });

        // Show the PopupMenu
        popupMenu.show();
    }
}