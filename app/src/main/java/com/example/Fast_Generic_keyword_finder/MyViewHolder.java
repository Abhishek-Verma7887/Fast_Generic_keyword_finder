package com.example.Fast_Generic_keyword_finder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView name_keyword,percent;
    CheckBox cbox;

    MyViewHolder(View itemView){
        super(itemView);
        name_keyword=(TextView)itemView.findViewById(R.id.cardviewtext1);
        percent=(TextView)itemView.findViewById(R.id.cardviewtext2);
        cbox=(CheckBox)itemView.findViewById(R.id.checkBox3);
    }
}
