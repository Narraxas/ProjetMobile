package com.example.projet_interim;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CandidatureAdaptator extends BaseAdapter {

    private Context context;
    private ArrayList<ArrayList<String>> list;
    private LayoutInflater inflater;

    // provide : id_candidature, username, titre de l'annonce
    public CandidatureAdaptator(Context context, ArrayList<ArrayList<String>> list) {
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

        view = inflater.inflate(R.layout.candidature_listview_adaptator,null);
        TextView txt = view.findViewById(R.id.candidature_text);


        ArrayList<String> s = getItem(i);

        txt.setText(s.get(1) + " candidate pour :\n" + s.get(2));

        return view;
    }
}
