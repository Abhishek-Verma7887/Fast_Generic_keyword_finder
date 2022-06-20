package com.example.Fast_Generic_keyword_finder;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

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
