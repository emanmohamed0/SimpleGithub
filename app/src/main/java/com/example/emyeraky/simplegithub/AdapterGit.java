package com.example.emyeraky.simplegithub;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

/**
 * Created by Emy Eraky on 1/3/2018.
 */

public class AdapterGit extends ArrayAdapter<Data>{


    public AdapterGit(Context context,  Data[] data) {
        super(context, android.R.layout.simple_list_item_1, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Data data = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);
        }

        TextView tvUserName = (TextView) convertView.findViewById(R.id.username);
        tvUserName.setText(String.valueOf(data.getUser_Name()));
        convertView.setTag(String.valueOf(position));

        TextView tvRepo = (TextView) convertView.findViewById(R.id.repo);
        tvRepo.setText(String.valueOf(data.getRepoName()));

        TextView tvDesp = (TextView) convertView.findViewById(R.id.desp);
        tvDesp.setText(data.getDescription());

        CardView cardView= (CardView)convertView.findViewById(R.id.cardview);
        if (data.getFork()==false){
            cardView.setCardBackgroundColor(Color.GREEN);
        }
        else{

        }




        return convertView;
    }

}


