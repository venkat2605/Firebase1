package com.venkat.firebase1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    RecyclerView recyclerView;
    Context context;
    ArrayList<String> item = new ArrayList<>();
    ArrayList<String> urls = new ArrayList<>();

    public MyAdapter(RecyclerView recyclerView, Context context, ArrayList<String> item,ArrayList<String> urls) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.item = item;
        this.urls = urls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.nameofFile.setText(item.get(position));

    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public void update(String filename, String url) {
        item.add(filename);
        urls.add(url);
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameofFile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameofFile = itemView.findViewById(R.id.Filename);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = recyclerView.getChildLayoutPosition(v);
                    Intent intent = new Intent();
                    intent.setType(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(urls.get(position)));
                    context.startActivity(intent);
                }
            });

        }


    }
}
