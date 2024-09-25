package com.example.yogshala.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// Import necessary widgets
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.yogshala.R;
import com.example.yogshala.model.Program;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ProgramAdapter extends ArrayAdapter<Program> {

    private Context context;
    private List<Program> programList;
    private DatabaseReference databaseReference;

    public ProgramAdapter(Context context, List<Program> programList) {
        super(context, 0, programList);
        this.context = context;
        this.programList = programList;
        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Programs");
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Inflate the view if not already done
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_program, parent, false);
        }

        // Retrieve the current program
        Program program = programList.get(position);

        // Initialize UI components
        TextView tvItem = convertView.findViewById(R.id.tvItem);
        ImageView ivDelete = convertView.findViewById(R.id.ivDelete);

        // Set the program name
        tvItem.setText(program.getName());

        // Set click listener for the delete button
        ivDelete.setOnClickListener(v -> {
            // Ensure the position is within bounds before removing the item
            if (position >= 0 && position < programList.size()) {
                databaseReference.child(program.getId()).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Deleted " + program.getName(), Toast.LENGTH_SHORT).show();
                            // Remove the item from the list only if it's still valid
                            if (position >= 0 && position < programList.size()) {
                                programList.remove(position);
                                //   notifyDataSetChanged();  Refresh the ListView
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to delete " + program.getName(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(context, "Invalid position: " + position, Toast.LENGTH_SHORT).show();
            }
        });


        return convertView;
    }
}
