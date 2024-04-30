package com.example.projet_interim;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NotifAdaptator extends BaseAdapter {

    private Context context;
    private ArrayList<ArrayList<String>> list;
    private LayoutInflater inflater;


    public NotifAdaptator(Context context, ArrayList<ArrayList<String>> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ArrayList<String> getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.notif_listview_adaptator,null);
        TextView fromUser = view.findViewById(R.id.notif_fromUser);
        TextView title = view.findViewById(R.id.notif_title);
        TextView content = view.findViewById(R.id.notif_content);

        ArrayList<String> s = getItem(i);

        // TODO : là je met tout dans le premier textView mais on peut faire plus beau
        fromUser.setText("De : " + s.get(3) + "\n\n" + "Objet : " + s.get(4));

        return view;
    }
}
