package com.example.Fast_Generic_keyword_finder;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmResults;
import io.realm.RealmSchema;

public class Automanton_building extends AppCompatActivity {

    Realm realm;
    ArrayList<Keyword_schema> keywordList;
    Keyword_RecyclerAdapter adapter;
    RecyclerView rv;
    TextInputEditText name_keyword,percent_keyword;
    Button save_bt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automanton_building);
        name_keyword=(TextInputEditText)findViewById(R.id.textView2);
        percent_keyword=(TextInputEditText)findViewById(R.id.textViewpercent);
        save_bt=(Button)findViewById(R.id.button3);
        rv=(RecyclerView)findViewById(R.id.recycler_view);
        //SETUP RECYCLERVIEW
        rv.setLayoutManager(new LinearLayoutManager(this));
        Realm.init(this);
        //SETUP REEALM
        RealmConfiguration config=new RealmConfiguration.Builder().schemaVersion(1).migration(new Migration()).build();
        realm=Realm.getInstance(config);

        //RETRIEVE
        RealmHelper helper=new RealmHelper(realm);
        keywordList=helper.retrieve();

        //BIND
        adapter=new Keyword_RecyclerAdapter(this,keywordList,realm);
        rv.setAdapter(adapter);

        RealmResults<Keyword_schema> mykeywordList = realm.where(Keyword_schema.class).findAllAsync();

        mykeywordList.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Keyword_schema>>() {
            @Override
            public void onChange(RealmResults<Keyword_schema> keyword_schemas, OrderedCollectionChangeSet changeSet) {
                keywordList=helper.retrieve();
                adapter=new Keyword_RecyclerAdapter(Automanton_building.this,keywordList,realm);
                rv.setAdapter(adapter);
            }
        });


        //save button action
        save_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Keyword_schema keyword=new Keyword_schema();
                String word=name_keyword.getText().toString();
                String tmp="";
                int ff=-1;
                for(int i=word.length()-1;i>=0;i--){
                    if(word.charAt(i)!=' ') {
                        ff = i; break;
                    }
                }
                for(int i=0;i<ff+1;i++){
                    tmp+=word.charAt(i);
                }
                String Cent=percent_keyword.getText().toString();
                if(word.length()!=0&&Cent.length()!=0){
                    keyword.setName(tmp);
                    keyword.setPercent(Cent);
                    keyword.setStatus(true);
                    name_keyword.setText("");
                    percent_keyword.setText("");
                    //SAVE
                    RealmHelper helper=new RealmHelper(realm);
                    helper.save(keyword);
                    //REFRESH
                    keywordList=helper.retrieve();
                    adapter=new Keyword_RecyclerAdapter(Automanton_building.this,keywordList,realm);
                    rv.setAdapter(adapter);
                }else{
                    Toast.makeText(getApplicationContext(), "no text or percent", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}