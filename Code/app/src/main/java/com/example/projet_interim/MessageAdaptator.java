package com.example.projet_interim;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageAdaptator extends BaseAdapter {

    private Context context;
    private ArrayList<ArrayList<String>> notifications;
    private LayoutInflater inflater;

    public MessageAdaptator(Context context, ArrayList<ArrayList<String>> notifications) {
        this.context = context;
        this.notifications = notifications;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public ArrayList<String> getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.message_adaptator, parent, false);
        }

        TextView fromUserTextView = convertView.findViewById(R.id.notif_fromUser);
        TextView titleTextView = convertView.findViewById(R.id.notif_title);
        TextView contentTextView = convertView.findViewById(R.id.notif_content);

        ArrayList<String> notification = getItem(position);

        // Affichage des informations de la notification
        fromUserTextView.setText("De : " + notification.get(3));
        titleTextView.setText("Objet : " + notification.get(4));
        // contentTextView.setText(notification.getContent()); // Ajoutez cette méthode si vous avez une méthode getContent()

        return convertView;
    }
}
