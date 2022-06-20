package com.example.Fast_Generic_keyword_finder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.realm.Realm;

public class Keyword_RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder>{
    Context c;
    Realm realm_d;
    ArrayList<Keyword_schema> keywordsList;
    RealmHelper helper;
    public Keyword_RecyclerAdapter(Context c, ArrayList<Keyword_schema> keywordsList,Realm realm){
        this.c=c;
        this.keywordsList=keywordsList;
        this.realm_d=realm;
        this.helper=new RealmHelper(realm_d);
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
        holder.percent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    helper.delete_keyword(keywordsList.get(position).getid(), c);
                }catch (Exception e){
                    Toast.makeText(c, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.cbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.cbox.isChecked()&&chk!=true){
                    helper.update_status(keywordsList.get(position).getid(),true);
                }else if(chk==true){
                    helper.update_status(keywordsList.get(position).getid(),false);
                }
            }
        });




    }
    @Override
    public int getItemCount() {
        return keywordsList.size();
    }
}
