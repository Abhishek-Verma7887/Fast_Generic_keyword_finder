package com.example.Fast_Generic_keyword_finder;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmHelper {

    Realm realm;

    public RealmHelper(Realm realm) {
        this.realm = realm;
    }

    //WRITE
    public void save(final Keyword_schema keyword_object)
    {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Keyword_schema s=realm.copyToRealm(keyword_object);

            }
        });

    }

    //READ
    public ArrayList<Keyword_schema> retrieve()
    {
        ArrayList<Keyword_schema> KeywordList=new ArrayList<>();
        RealmResults<Keyword_schema> spacecrafts=realm.where(Keyword_schema.class).findAll();

        for(Keyword_schema s:spacecrafts)
        {
            KeywordList.add(s);
        }

        return KeywordList;
    }
}