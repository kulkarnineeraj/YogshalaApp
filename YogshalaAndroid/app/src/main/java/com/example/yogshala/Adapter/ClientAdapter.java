package com.example.yogshala.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.yogshala.R;
import com.example.yogshala.model.Client;

import java.util.List;

public class ClientAdapter extends ArrayAdapter<Client> {

    private Context context;
    private List<Client> clients;

    public ClientAdapter(Context context, List<Client> clients) {
        super(context, R.layout.list_item, clients);
        this.context = context;
        this.clients = clients;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        Client client = clients.get(position);

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvClientDate = convertView.findViewById(R.id.tvEnquiryDate);  // You may want to rename the TextView id in XML to match "tvClientDate"
        TextView tvProgram = convertView.findViewById(R.id.tvProgram);


        tvName.setText(client.getFirstName() + " " + client.getLastName());
        tvClientDate.setText(client.getDate());
        tvProgram.setText(client.getProgram());



        return convertView;
    }
}
