package com.example.Fast_Generic_keyword_finder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Keyword_RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder>{
    Context c;
    ArrayList<Keyword_schema> keywordsList; ;
    public Keyword_RecyclerAdapter(Context c, ArrayList<Keyword_schema> keywordsList){
        this.c=c;
        this.keywordsList=keywordsList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.item_keyword,parent,false);
        return new MyViewHolder(v);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.name_keyword.setText(keywordsList.get(position).getName());
        holder.percent.setText(keywordsList.get(position).getPercent());
        boolean chk=keywordsList.get(position).getStatus();
        if(chk){
            holder.cbox.setChecked(true);
        }else{
            holder.cbox.setChecked(false);
        }
    }
    @Override
    public int getItemCount() {
        return keywordsList.size();
    }
}
