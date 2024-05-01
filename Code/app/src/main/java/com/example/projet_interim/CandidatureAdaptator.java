package com.example.projet_interim;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class CandidatureAdaptator extends BaseAdapter {

    private Context context;
    private ArrayList<ArrayList<String>> list;
    private LayoutInflater inflater;

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
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.candidature_adaptator, parent, false);
            holder = new ViewHolder();
            holder.candidatureText = convertView.findViewById(R.id.candidature_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ArrayList<String> item = getItem(i);
        if (item != null) {
            String username = item.get(1);
            String annonceTitle = item.get(2);
            holder.candidatureText.setText(username + " candidate pour :\n" + annonceTitle);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView candidatureText;
    }
}
