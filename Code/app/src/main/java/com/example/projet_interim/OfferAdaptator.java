package com.example.projet_interim;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class OfferAdaptator extends BaseAdapter {

    private Context context;
    private ArrayList<ArrayList<String>> list;
    private LayoutInflater inflater;

    public OfferAdaptator(Context context, ArrayList<ArrayList<String>> list) {
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

        view = inflater.inflate(R.layout.offre_listview_adaptator, null);
        TextView title = view.findViewById(R.id.offre_title);
        TextView description = view.findViewById(R.id.offre_description);
        TextView coord = view.findViewById(R.id.offre_coord);


        ArrayList<String> s = getItem(i);
        /*title.setText(s[0]);
        description.setText(s[1]);
        coord.setText(s[2]);*/
        title.setText(s.get(2) + "\n\n" + s.get(3) + "\n\n" + s.get(4));

        return view;
    }
}
