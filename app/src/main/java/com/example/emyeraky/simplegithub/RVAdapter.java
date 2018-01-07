package com.example.emyeraky.simplegithub;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//1
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.DataViewHolder> {
    Data[] data;

    //add a constructor to the custom adapter so that it has a handle to the data that the RecyclerView displays.
    RVAdapter(Data[] data) {
        this.data = data;
    }

    //this method is called when the custom ViewHolder needs to be initialized after layout item called.
    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        DataViewHolder pvh = new DataViewHolder(v);
        return pvh;
    }

    // to specify the contents of each item of the RecyclerView.      4
    @Override
    public void onBindViewHolder(DataViewHolder DataViewHolder, int position) {
        DataViewHolder.tvrepo.setText(data[position].getRepoName());
        DataViewHolder.tvdesp.setText(data[position].getDescription());
        DataViewHolder.tvuserName.setText(data[position].getUser_Name());

        DataViewHolder.cv.setTag(position);
        DataViewHolder.tvuserName.setTag(position);
        DataViewHolder.tvrepo.setTag(position);
        DataViewHolder.tvdesp.setTag(position);

        if (data[position].getFork() == false) {
            DataViewHolder.cv.setCardBackgroundColor(Color.GREEN);
        }
        DataViewHolder.tvuserName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDialog(view);
                return false;
            }
        });
        DataViewHolder.cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                showDialog(view);
                return false;
            }
        });
        DataViewHolder.tvdesp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDialog(view);
                return false;
            }
        });
        DataViewHolder.tvrepo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDialog(view);
                return false;
            }
        });

    }

    public void showDialog(final View view) {
        final int position = (int) view.getTag();
        Log.v("long clicked", "position: " + position);
        AlertDialog alertDialog = new AlertDialog.Builder(view.getContext()).create();
        alertDialog.setTitle("If go to Repository");
        alertDialog.setMessage(data[position].getHtml_url());
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String url = data[position].getHtml_url();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        view.getContext().startActivity(i);

                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    //Returns the total number of items in the data set held by the adapter
    @Override
    public int getItemCount() {
        return data.length;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    //view holder design pattern    2
    public static class DataViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView tvrepo;
        TextView tvuserName;
        TextView tvdesp;

        DataViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardview);
            tvrepo = (TextView) itemView.findViewById(R.id.repo);
            tvuserName = (TextView) itemView.findViewById(R.id.username);
            tvdesp = (TextView) itemView.findViewById(R.id.desp);

        }


    }

}
