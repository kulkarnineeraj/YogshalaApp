package com.example.yogshala.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.yogshala.R;
import com.example.yogshala.model.Client;

import java.util.List;

public class ClientNamesAdapter extends ArrayAdapter<Client> {
    private Context context;
    private List<Client> clients;

    public ClientNamesAdapter(Context context, List<Client> clients) {
        super(context, R.layout.list_item_client, clients);
        this.context = context;
        this.clients = clients;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Reuse the view if possible for better performance
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_client, parent, false);
        }

        // Get the current client based on position
        Client client = clients.get(position);

        // Bind the client data to the view elements
        TextView tvName = convertView.findViewById(R.id.tvName);
        tvName.setText(client.getFirstName() + " " + client.getLastName());

        return convertView;
    }

    // Method to retrieve a client object by its position
    public Client getClient(int position) {
        return clients.get(position);
    }
}
