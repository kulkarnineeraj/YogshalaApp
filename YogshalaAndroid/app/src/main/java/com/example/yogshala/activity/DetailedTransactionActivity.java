package com.example.yogshala.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yogshala.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailedTransactionActivity extends AppCompatActivity {

    private ImageView backBtn, menuBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_transaction);

        // Hide the action bar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        menuBtn = findViewById(R.id.menuBtn);
        // Handle menu button click
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
                finish(); // Closes the current activity and returns to the previous one
            }
        });

        // Retrieve the data from the Intent
        String clientName = getIntent().getStringExtra("clientName");
        String fromDate = getIntent().getStringExtra("fromDate");
        String toDate = getIntent().getStringExtra("toDate");
        String mode = getIntent().getStringExtra("paymentMode");
        String type = getIntent().getStringExtra("type");
        String monthFee = getIntent().getStringExtra("monthFee");
        String receivedAmount = getIntent().getStringExtra("receivedAmount");
        String program = getIntent().getStringExtra("program");
        String remark = getIntent().getStringExtra("remarks");


        // Find and populate the views with the retrieved data
        TextView tvClientName = findViewById(R.id.tvName);
        TextView tvFromDate = findViewById(R.id.tvFromDate);
        TextView tvToDate = findViewById(R.id.tvToDate);
        TextView tvMode = findViewById(R.id.tvMode2);
        TextView tvType = findViewById(R.id.tvType);
        TextView tvMonthFee = findViewById(R.id.tvMonthFee2);
        TextView tvReceivedAmount = findViewById(R.id.tvReceivedAmount2);
        TextView tvProgram = findViewById(R.id.tvProgram2);
        TextView tvRemark = findViewById(R.id.tvRemark2);

        tvClientName.setText(clientName);
        tvFromDate.setText(fromDate);
        tvToDate.setText(toDate);
        tvMode.setText(mode);
        tvType.setText(type);
        tvProgram.setText(program);
        tvRemark.setText(remark);
        tvMonthFee.setText(String.valueOf(monthFee));
        tvReceivedAmount.setText(String.valueOf(receivedAmount));

    }

    // Show popup menu
    private void showPopupMenu(View view) {

        Intent intent = getIntent();
        String transactionId = getIntent().getStringExtra("transactionId");
        String clientName = intent.getStringExtra("clientName");
        String fromDate = intent.getStringExtra("fromDate");
        String toDate = intent.getStringExtra("toDate");
        String mode = intent.getStringExtra("paymentMode");
        String type = intent.getStringExtra("type");
        String monthFee = intent.getStringExtra("monthFee");
        String receivedAmount = intent.getStringExtra("receivedAmount");
        String program = intent.getStringExtra("program");
        String remark = intent.getStringExtra("remarks");

        // Create a PopupMenu
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_items, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle Edit action
                if (item.getItemId() == R.id.menu_edit) {
                    Intent intent = new Intent(DetailedTransactionActivity.this, EditTransactionActivity.class);
                    intent.putExtra("transactionId", transactionId);
                    intent.putExtra("clientName", clientName);
                    intent.putExtra("fromDate", fromDate);
                    intent.putExtra("toDate", toDate);
                    intent.putExtra("paymentMode", mode);
                    intent.putExtra("type", type);
                    intent.putExtra("monthFee", monthFee);
                    intent.putExtra("receivedAmount", receivedAmount);
                    intent.putExtra("program", program);
                    intent.putExtra("remarks", remark);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.menu_delete) {
                    showDeleteConfirmationDialog();
                    return true;
                }
                return false;
            }
            // Show confirmation dialog before deletion
            private void showDeleteConfirmationDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailedTransactionActivity.this);
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
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Transactions");

                if (transactionId != null) {
                    databaseReference.child(transactionId).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(DetailedTransactionActivity.this, "Transaction deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(DetailedTransactionActivity.this, "Failed to delete transaction", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Optional: Show Snackbar with undo option
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Transaction deleted", Snackbar.LENGTH_LONG);
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
                Toast.makeText(DetailedTransactionActivity.this, "Undo deletion is not implemented", Toast.LENGTH_SHORT).show();
            }
        });
        // Show the PopupMenu
        popupMenu.show();
    }
}