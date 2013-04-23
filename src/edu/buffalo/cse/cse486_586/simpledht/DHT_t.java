package edu.buffalo.cse.cse486_586.simpledht;

import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public final class DHT_t {
    public static final String AUTHORITY = "edu.buffalo.cse.cse486_586.simpledht.provider";
    
    
    public DHT_t() {
      Log.v("DHT_t","Table parameters");	
    }

    public static final class DHT_tableval implements BaseColumns {

        private DHT_tableval() {}
        public static final String TABLE_NAME = "DHT_table";
        public static final String COLUMN_1= "_id";
        public static final String COLUMN_2= "provider_value";
        public static final Uri CONTENT_URI =  Uri.parse("content://" + AUTHORITY +"/" +TABLE_NAME);
    }
}
