package com.example.yogshala.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.data.Set;
import com.anychart.data.Mapping;
import com.example.yogshala.R;
import com.example.yogshala.model.Transaction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncomeReportActivity extends AppCompatActivity {

    private AnyChartView anyChartView;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_report);

        // Hide the action bar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IncomeReportActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Initialize AnyChartView
        anyChartView = findViewById(R.id.anyChartView);
        if (anyChartView == null) {
            Log.e("IncomeReportActivity", "AnyChartView not initialized!");
            return; // Prevent further execution if the view is not found
        }


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Transactions");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isFinishing() || isDestroyed()) {
                    // Activity is no longer valid, skip updates
                    return;
                }

                List<Transaction> transactions = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Transaction transaction = snapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        transactions.add(transaction);
                    }
                }

                // Calculate and update the chart
                runOnUiThread(() -> calculateMonthwiseIncome(transactions));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void calculateMonthwiseIncome(List<Transaction> transactions) {
        // A map to hold the total income for each month
        Map<String, Integer> monthIncomeMap = new HashMap<>();

        // Initialize each month with 0 income
        monthIncomeMap.put("January", 0);
        monthIncomeMap.put("February", 0);
        monthIncomeMap.put("March", 0);
        monthIncomeMap.put("April", 0);
        monthIncomeMap.put("May", 0);
        monthIncomeMap.put("June", 0);
        monthIncomeMap.put("July", 0);
        monthIncomeMap.put("August", 0);
        monthIncomeMap.put("September", 0);
        monthIncomeMap.put("October", 0);
        monthIncomeMap.put("November", 0);
        monthIncomeMap.put("December", 0);

        // Iterate over each transaction and accumulate the income for the respective month
        for (Transaction transaction : transactions) {
            String fromDate = transaction.getFromDate();
            String receivedAmountStr = transaction.getReceivedAmount();

            // Ensure receivedAmount is not null or empty before parsing
            if (receivedAmountStr != null && !receivedAmountStr.isEmpty()) {
                try {
                    int receivedAmount = Integer.parseInt(receivedAmountStr);

                    // Parse the month from the transaction's `fromDate` (Assuming format "dd-MM-yyyy")
                    String[] dateParts = fromDate.split("-");
                    String month = getMonthName(Integer.parseInt(dateParts[1]));  // Convert month number to month name

                    // Add the income for this month
                    monthIncomeMap.put(month, monthIncomeMap.get(month) + receivedAmount);
                } catch (NumberFormatException e) {
                    Log.e("IncomeReportActivity", "Invalid number format for receivedAmount: " + receivedAmountStr, e);
                }
            } else {
                Log.w("IncomeReportActivity", "ReceivedAmount is null or empty for transaction: " + transaction);
            }
        }

        // After calculating the income for each month, update the table
        updateTableWithIncome(monthIncomeMap);
        // After calculating the income for each month, update the pie chart
        updateBarChartWithIncome(monthIncomeMap);
    }


    private String getMonthName(int monthNumber) {
        String[] months = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return months[monthNumber - 1];  // monthNumber is 1-based
    }


    private void updateTableWithIncome(Map<String, Integer> monthIncomeMap) {
        // Example: Finding TextViews by ID for each month and setting the income
        TextView januaryIncome = findViewById(R.id.janIncome);
        TextView februaryIncome = findViewById(R.id.febIncome);
        TextView marchIncome = findViewById(R.id.marIncome);
        TextView aprilIncome = findViewById(R.id.aprIncome);
        TextView mayIncome = findViewById(R.id.mayIncome);
        TextView juneIncome = findViewById(R.id.junIncome);
        TextView julyIncome = findViewById(R.id.julIncome);
        TextView augustIncome = findViewById(R.id.augIncome);
        TextView septemberIncome = findViewById(R.id.septIncome);
        TextView octoberIncome = findViewById(R.id.octIncome);
        TextView novemberIncome = findViewById(R.id.novIncome);
        TextView decemberIncome = findViewById(R.id.decIncome);


        // Set the income for each month
        januaryIncome.setText(String.valueOf(monthIncomeMap.get("January")));
        februaryIncome.setText(String.valueOf(monthIncomeMap.get("February")));
        marchIncome.setText(String.valueOf(monthIncomeMap.get("March")));
        aprilIncome.setText(String.valueOf(monthIncomeMap.get("April")));
        mayIncome.setText(String.valueOf(monthIncomeMap.get("May")));
        juneIncome.setText(String.valueOf(monthIncomeMap.get("June")));
        julyIncome.setText(String.valueOf(monthIncomeMap.get("July")));
        augustIncome.setText(String.valueOf(monthIncomeMap.get("August")));
        septemberIncome.setText(String.valueOf(monthIncomeMap.get("September")));
        octoberIncome.setText(String.valueOf(monthIncomeMap.get("October")));
        novemberIncome.setText(String.valueOf(monthIncomeMap.get("November")));
        decemberIncome.setText(String.valueOf(monthIncomeMap.get("December")));

    }

    //    private void updatePieChartWithIncome(Map<String, Integer> monthIncomeMap) {
//        // Ensure AnyChartView is not null before trying to use it
//        if (anyChartView == null) {
//            Log.e("IncomeReportActivity", "AnyChartView is null!");
//            return;
//        }
//
//        // Create a Pie chart
//        Pie pie = AnyChart.pie();
//
//        // Create a list to hold data entries
//        List<DataEntry> data = new ArrayList<>();
//
//        // Add each month's income as a pie slice
//        for (Map.Entry<String, Integer> entry : monthIncomeMap.entrySet()) {
//            data.add(new ValueDataEntry(entry.getKey(), entry.getValue()));
//        }
//
//        // Set the data in the pie chart
//        pie.data(data);
//
//        // Customize the chart (optional)
//        pie.title("Monthwise Income Report");
//        pie.labels().position("outside");
//
//        // Set the pie chart to AnyChartView
//        anyChartView.setChart(pie);
//    }

    private void updateBarChartWithIncome(Map<String, Integer> monthIncomeMap) {
        // Ensure AnyChartView is not null before trying to use it
        if (anyChartView == null) {
            Log.e("IncomeReportActivity", "AnyChartView is null!");
            return;
        }

        // Create a Column chart (for vertical bars)
        Cartesian columnChart = AnyChart.column();

        // Create a list to hold data entries
        List<DataEntry> data = new ArrayList<>();

        // Define the months in the correct order (January to December)
        String[] monthsInOrder = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};

        // Add each month's income as a column in the column chart
        for (String month : monthsInOrder) {
            int income = monthIncomeMap.getOrDefault(month, 0); // Default to 0 if income is not present
            data.add(new ValueDataEntry(month, income)); // Month on X-axis, Income on Y-axis
        }

        // Set the data in the column chart
        columnChart.data(data);

        // Set the title of the chart
        columnChart.title("Monthwise Income Report");

        // Set X-axis title (Months) and enable labels
        columnChart.xAxis(0).title("Month").labels().enabled(true);

        // Rotate the X-axis labels to display vertically
        columnChart.xAxis(0).labels().rotation(90);  // Rotate labels by 90 degrees

        // Set Y-axis title (Income) and enable labels
        columnChart.yAxis(0).title("Income").labels().enabled(true);

        // Optionally set the Y-axis to begin at 0 (avoiding negative income values)
        columnChart.yScale().minimum(0);

        // Customize the appearance (optional)
        columnChart.labels().position("center");

        // Set the column chart to AnyChartView
        anyChartView.setChart(columnChart);
    }





}