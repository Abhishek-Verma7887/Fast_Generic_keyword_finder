package com.example.Fast_Generic_keyword_finder;
import org.bson.types.ObjectId;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Keyword_schema extends RealmObject {
        @PrimaryKey
        private String id= new ObjectId().toHexString();
        private String name;
        private String percent;
        private boolean status;
        public String getName() {
            return name;
        }
    public String getid() {
        return id;
    }

        public void setName(String name) {
            this.name = name;
        }
        public String getPercent() {
            return percent;
        }

        public void setPercent(String name) {
            this.percent = name;
        }
        public boolean getStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }
}
