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
    TextInputEditText name_keyword;
    Button save_bt;

    public class Migration implements RealmMigration {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
            Long version = oldVersion;
            final int[] currentKey = {0};
            // DynamicRealm exposes an editable schema
            RealmSchema schema = realm.getSchema();
            // Changes from version 0 to 1: Adding lastName.
            // All properties will be initialized with the default value "".
            if (version == 0L) {
                schema.get("Keyword_schema")
                        .addField("id", String.class, FieldAttribute.INDEXED)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.setString("id", String.valueOf((currentKey[0]++)));
                            }
                        })
                        .addPrimaryKey("id");;
                version++;
            }
        }
        @Override
        public boolean equals(Object obj) {
            return obj != null && obj instanceof Migration; // obj instance of your Migration class name, here My class is Migration.
        }

        @Override
        public int hashCode() {
            return Migration.class.hashCode();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automanton_building);
        name_keyword=(TextInputEditText)findViewById(R.id.textView2);
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
                if(word.length()!=0){
                    keyword.setName(word);
                    keyword.setPercent("50%");
                    keyword.setStatus(true);
                    name_keyword.setText("");
                    //SAVE
                    RealmHelper helper=new RealmHelper(realm);
                    helper.save(keyword);
                    //REFRESH
                    keywordList=helper.retrieve();
                    adapter=new Keyword_RecyclerAdapter(Automanton_building.this,keywordList,realm);
                    rv.setAdapter(adapter);
                }else{
                    Toast.makeText(getApplicationContext(), "invalid input", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}