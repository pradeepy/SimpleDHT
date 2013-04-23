

/*
 * 
 *     Sources :http://developer.android.com/reference/android/content/ContentProvider.html
 *              http://www.vogella.de/articles/AndroidSQLite/article.html
 * 
 * 
 * 
 */

package edu.buffalo.cse.cse486_586.simpledht;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.regex.Pattern;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;

public class ContentProviderDHT extends ContentProvider  {
    // Used for debugging and logging
	int[] peers;
	private static final String TAG = "NotePadProvider";
    String[] sendData = null;
    Queue dataQueue = new PriorityQueue();
    boolean sendConnect = false;
    boolean sendValues = false;
    boolean fwdValues = false;
    boolean insertOK = false;
    String fwdData = null;

    private static final String DATABASE_NAME = "DHT_db.db";
    private static final int DATABASE_VERSION = 2;
    private static HashMap<String, String> tableprojection = new HashMap<String, String>();
    private static final int VAL = 1;
    private static final UriMatcher sUriMatcher;
    private DatabaseHelper dbHelper;
	public Pattern pattern = Pattern.compile("[\\:]");
	public int sequenceRecv = 0, msgSeq = 0;
	public int localPort = 0, Successor = 0, Predecessor = 0;
	public boolean join = false, sendDataOK = false, mNode = true, tStart = false, sKey = false, sKeyC = false;
	public String IP_ADDR = "10.0.2.2";
    public String[] dataValues = null;
    public String sendMessage = null;
    public String cVal = null, keyVal = null;
    public MatrixCursor cur_key = null;
    public String localport_tmanager(Context context) {
      TelephonyManager tel = (TelephonyManager) this.getContext().getSystemService(Context.TELEPHONY_SERVICE);
      String uid = tel.getDeviceId();
      return uid;	
    }
    
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(DHT_t.AUTHORITY, "DHT_table", VAL);
        //tableprojection.put("provider_key", "provider_key");
        tableprojection.put("_id", "_id");
        tableprojection.put("provider_value", "provider_value");
    }

    
   static class DatabaseHelper extends SQLiteOpenHelper {
       DatabaseHelper(Context context) {
           super(context, DATABASE_NAME, null, DATABASE_VERSION);
       }
       @Override
       public void onCreate(SQLiteDatabase db) {
           db.execSQL("CREATE TABLE " + DHT_t.DHT_tableval.TABLE_NAME + " ("
                   + DHT_t.DHT_tableval.COLUMN_1+ " INTEGER ,"
                   + DHT_t.DHT_tableval.COLUMN_2+ " TEXT"
                   + ");");
           //tempfn();
       }
       @Override
       public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
           Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                   + newVersion + ", which will destroy all old data");
           db.execSQL("DROP TABLE IF EXISTS notes");
           onCreate(db);
       }
   }        

   @Override
   public boolean onCreate() {
       dbHelper = new DatabaseHelper(getContext());
       // insert code here ...
       TelephonyManager tel = (TelephonyManager) this.getContext().getSystemService(Context.TELEPHONY_SERVICE);
       tel.getLine1Number();
       String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
       Log.v("onCreate","tel    "+portStr);
       localPort = Integer.parseInt(portStr);
       Thread tempT = new Thread (new tempfn());
       tempT.start();
       return true;
   }

   @Override
   public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
           String sortOrder) {
       SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
       qb.setTables(DHT_t.DHT_tableval.TABLE_NAME);
       switch (sUriMatcher.match(uri)) {
       case VAL:
               qb.setProjectionMap(tableprojection);
               break;
           default:
               throw new IllegalArgumentException("Unknown URI " + uri);
       }

       SQLiteDatabase db = dbHelper.getReadableDatabase();
       Cursor c = qb.query(
           db,            // The database to query
           projection,    // The columns to return from the query
           null,     // The columns for the where clause
           selectionArgs, // The values for the where clause
           null,          // don't group the rows
           null,          // don't filter by row groups
           null        // The sort order
       );
  
       //String[] tempStr = {"ab"};
       //Cursor c2 = qb.query(db, null, null, tempStr, null, null, null);
       //Log.v("cursor",c.getColumnName(0));
       c.setNotificationUri(getContext().getContentResolver(), uri);
       if (selection == null)
    	 return c;
       keyVal = selection;
       Log.v("ContentProvider","C is .....   "+c.getCount());
       boolean found = false;
       if (c.moveToFirst()) {
           do {
             String temp = c.getString(0)+"    "+c.getString(1);
             //Log.v("Querying...",temp);
             if (selection.compareTo(c.getString(0)) == 0)
             { Log.v("Found the value ....",""+selection);
               found = true;
               return c;
             }
           } while (c.moveToNext());
       }
       
       
       if (found == false)
       {
    	 sKeyC = true;  
    	 while(true)
    	 {
    	   if (sKey == true)
    	   { sKey = false;
    	     return cur_key;
    	     //break;
    	   }
    	 }
       }
       return null;
   }
       
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sUriMatcher.match(uri) != VAL) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        String temp = initialValues.toString();
        Log.v("insert",""+temp);
        //insertOK = true;
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
//            if (temp.contains("port"))
//            { String portRecv = temp.substring(19, temp.indexOf(":"));
//              Log.v("insert","port no = "+portRecv+".");
//              localPort = Integer.parseInt(portRecv);
//            }  
        } else {
            values = new ContentValues();
        }
        
        if (temp.contains("port"))
        { tStart = true;
          return null;
        }
        
		String input3 = temp.substring(15, temp.indexOf(" "));
		String input4 = temp.substring(temp.indexOf("_id=")+4,temp.length());
        sendMessage = "key"+input4+"value"+input3;
        if (mNode == true)
          sendDataOK = true; 	
        /*
        if (!sendMessage.contains("dontFwd"))
        {  sendDataOK = true;
        } else {
          temp.replace("dontFwd","");
        }
        */
        mNode = true;
        if (insertOK  == false)
          return null; 	
        insertOK = false;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(
           DHT_t.DHT_tableval
           .TABLE_NAME,        // The table to insert into.
            null,  // A hack, SQLite sets this column value to null
                                             // if values is empty.
            values                           // A map of column names, and the values to insert
                                             // into the columns.
        );
        //System.out.println(rowId+"Insert succeeds");
        Log.v("ContentProvider","rowId is "+rowId);
        Log.v("ContentProvider",""+initialValues);
        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            // Creates a URI with the note ID pattern and the new row ID appended to it.
            Uri noteUri = ContentUris.withAppendedId(DHT_t.DHT_tableval.CONTENT_URI, rowId);

            // Notifies observers registered against this provider that the data changed.
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }
        // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
        throw new SQLException("Failed to insert row into " + uri);
    }

    
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(DHT_t.DHT_tableval.TABLE_NAME, selection, selectionArgs);
		return 0;
		
	}

	
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
//	public void tempfn () {
//	  Temp_localport t = new Temp_localport();
//	  localPort = t.localport();
//	  Log.v("temfn","localport is   "+localPort);
//	  try {
//	    if (localPort == 5554)
//	    {
//		  //ServerSocket S = new ServerSocket(10000);
//		  
//	    }
//	  } catch (Exception e) {
//		Log.v("tempfn",e.getMessage());  
//	  }
//	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
    
	class tempfn implements Runnable{
	  @Override
      public void run()
	  {
		Log.v("tempfn","inside temp fn ... ");  
//        while(true)
//        {
//          if (tStart == true)
//        	break;  
//        }
        Thread St = new Thread(new ServerThread());
        Thread Ct = new Thread(new ClientThread());
        St.start();
        Ct.start();
        tStart = false;
	  }
    }
    
	class ServerThread implements Runnable {
		ServerSocket Ss = null;
		@Override
		public void run() {
		  Log.v("ServerThread","server thread started   "+localPort);	
		  //Temp_localport t = new Temp_localport();
		  //localPort = t.localport();
		  Successor = localPort;
		  Predecessor = localPort;
		  try {
			Ss = new ServerSocket(10000);
			Log.v("ServerThread","ServerSocket ..... "+Ss);
			while (true)
			{
			  Socket S = new Socket();		      
			  S = Ss.accept();
			  Log.v("ServerThread","new connection accepted ..."+S);
			  BufferedReader in = new BufferedReader(
			    new InputStreamReader(
			      S.getInputStream()));
			  String inputLine = in.readLine();
			  Log.v("ServerThread","received msg "+inputLine);
			  String messageType = null;
			  int recvPort = 0;
			  String result[] = pattern.split(inputLine);
			  messageType = result[1];
			  recvPort = Integer.parseInt(result[0]);
			  boolean d = true;
			  Log.v("","");
			  if (messageType.contains("joinMessage"))
			  { d = false;
			    assignHash(inputLine);
			  } 
			  if (messageType.contains("Successor") && messageType.contains("Predecessor")) 
			  {
	            //String[] result = pattern.split(inputLine);
	            Log.v("ServerThread","received Successor and Predecessor "+inputLine); 
				int b = result[1].indexOf("Predecessor");
	            String input2 = result[1].substring(b+11, result[1].length());
	            //System.out.println("\n"+result[0]+"  "+result[1].substring(9,b)+"  "+input2);
	            int s = Integer.parseInt(result[1].substring(9,b));
	            int p = Integer.parseInt(input2);
	            if (s != 0)
	              Successor = s;
	            if (p != 0)
	              Predecessor = p;
	            Log.v("Successor","Successor = "+Successor+"  Predecessor = "+Predecessor);
	            d = false;
	          }
			  if (messageType.contains("Store"))
			  { Log.v("ServerThread","in correct node .....");
				messageType = messageType.substring(0, messageType.length()-5);
				d = false;
				Log.v("ServerThread","messageType "+messageType);
			    if (messageType.contains("Key"))
			    { keyValue(result[0]+":"+messageType); 
			    }
			    else
			    { insertValue(messageType);
			    }
			    //Log.v("ServerThreadStore","Store message "+messageType+" in min");
			  }
			  if (messageType.contains("Key") && (d == true))
			  { Log.v("ServerThread","Search for key   "+inputLine);
			    searchKey(inputLine);
				d = false;  
			  }
			  if (messageType.contains("KEYVALUE"))
			  {
				Log.v("ServerThread","got the cursor");
				d = false;
				messageType = messageType.replace("KEYVALUE", "");
			    MatrixCursor Mc = new MatrixCursor(new String[] {
			              DHT_t.DHT_tableval.COLUMN_1,
			              DHT_t.DHT_tableval.COLUMN_2});
			    Mc.newRow().add("0").add(messageType);
			    cur_key = Mc;
			    sKey = true;
			    Mc.close();
			  }
			  if (d == true) 
			  { Log.v("ServerThread","Received data for processing"); 
				// insert data. Compare hash value
				//if (recvPort == localPort)
				{
				  // insert data	
				}
				//else 
				{  assignData(inputLine);
				}
				Log.v("ServerThread","Received data for processing - end");
			  }
			  S.close();
			}
		  } catch (Exception e) {
			// TODO Auto-generated catch block
			Log.v("ServerThreadError",e.getMessage());
		  }
		}
	} // end class ServerThread

	class ClientThread implements Runnable {
		Socket Cs = null;
		
		@Override
		public void run() {
		  Log.v("ClientThread","client thread started ... "+localPort);	
		  join = true;
		  while(true) {
			try {
			  if (join == true && localPort != 5554)	
			  {
				Log.v("ClientThread","attempting to join .....");  
				join = false;
			    Cs = new Socket(IP_ADDR,5554*2);
			    PrintWriter out = new PrintWriter(Cs.getOutputStream(), true);
			    out.println(localPort+":"+"joinMessage");
			    Cs.close();
			  }
			  if (sendDataOK == true)
			  { Cs = new Socket(IP_ADDR,Successor*2);
			    PrintWriter out = new PrintWriter(Cs.getOutputStream(), true);
			    out.println(localPort+":"+sendMessage);
				sendDataOK = false;  
			  }
			  if (sKeyC == true)
			  {
				Cs = new Socket(IP_ADDR,Successor*2);  
				PrintWriter out = new PrintWriter(Cs.getOutputStream(), true);
				out.println(localPort+":"+keyVal+"Key");
				sKeyC = false;
			  }
			} catch (Exception e) {
			  Log.v("ClientThread",e.getMessage());
			}
		  }
		}
	} // end class ClientThread
	
	private String genHash(String input) throws NoSuchAlgorithmException {
		MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
		byte[] sha1Hash = sha1.digest(input.getBytes());
		Formatter formatter = new Formatter();
		for (byte b : sha1Hash) {
		formatter.format("%02x", b);
		}
		return formatter.toString();
    } // end String genHash
	
	private void assignHash (String inputLine) {
	    String[] result = pattern.split(inputLine);
	    String p_hash = null, s_hash = null, l_hash = null, recv_hash = null;
	    try {
	      p_hash = genHash(Integer.toString(Predecessor));
	      s_hash = genHash(Integer.toString(Successor));
	      l_hash = genHash(Integer.toString(localPort));
	      recv_hash = genHash(result[0]);
	      int recvPort = Integer.parseInt(result[0]);
	      Log.v("assignHash","some random comment "+result[0]+"    "+result[1]);
	      Log.v("assignHash","recvPort is     "+(Integer.toString(recvPort)));
	      Log.v("assignHash","...P  "+(Integer.toString(Predecessor)));
	      Log.v("assignHash","S  "+(Integer.toString(Predecessor)));
	      Log.v("assignHash","L  "+(Integer.toString(Predecessor)));
	      
	      boolean ins = false;
	      if (recv_hash.compareTo(l_hash) > 0 && recv_hash.compareTo(s_hash) < 0)
	      { Log.v("assignHash","inserting between "+localPort+" and "+Successor);
	        sendHash (localPort, Successor, recvPort);
	        sendHash (recvPort, 0, Successor);
	        Successor = recvPort;
	    	ins = true;
	    	return;
	      }
	      
	      if (l_hash.compareTo(s_hash) >= 0 && recv_hash.compareTo(l_hash) > 0)
	      {
	    	Log.v("assignHash","incoming node greater than max node");
	    	sendHash (localPort, Successor, recvPort);
	    	if (localPort != Successor)
	    	{
	    	  sendHash (recvPort, 0, Successor);	
	    	} else {
	    	  //Successor = recvPort;
	    	  if (Predecessor == localPort)
	    	  {
	    		Predecessor = recvPort;  
	    	  }
	    	}
	    	Successor = recvPort;
	    	Log.v("SP","Successor "+Successor+" Predecessor "+Predecessor);
	    	ins = true;
	    	return;
	      }
	      
	      if (l_hash.compareTo(p_hash) <= 0 && recv_hash.compareTo(l_hash) < 0)
	      {
		    Log.v("assignHash","incoming node less than min node");
		    sendHash (Predecessor, localPort, recvPort);
	    	if (localPort != Successor)
	    	{
	    	  sendHash (0, recvPort, Predecessor);	
	    	} else {
	    	  //Successor = recvPort;
	    	  if (Successor == localPort)
	    	  {
	    		Successor = recvPort;  
	    	  }
	    	}
	    	Predecessor = recvPort;
	    	ins = true;
	    	return;
	      }
	      
	      if (ins == false)
	      {
		    Log.v("assignHash","forwarding to successor node    "+inputLine);
		    Socket Cs = null;
		    Cs = new Socket(IP_ADDR,Successor*2);
		    PrintWriter out = new PrintWriter(Cs.getOutputStream(), true);
		    out.println(inputLine);
		    Cs.close(); 	      
	      }
	      
	      return;
	    } catch (Exception e) {
	      Log.v("assignHash",e.getMessage());	
	    }
	} // end void assignHash
	
	public void sendHash (int p, int s, int r) {
		Socket Cs = null;
		Log.v("sendHash",""+localPort+":Successor "+s+"   Predecessor "+p+"  recvPort "+r);
		try {
		  Cs = new Socket(IP_ADDR,r*2);
		  PrintWriter out = new PrintWriter(Cs.getOutputStream(), true);
	      out.println(localPort+":Successor"+s+"Predecessor"+p);
		  Cs.close();
		  } catch (Exception e) {
		  Log.v("sendHash",e.getMessage());	
		}
	}	
	
	public void assignData (String data) {
		try {
		  Log.v("assignData","received   "+data);	
		  String[] result = pattern.split(data);	
	      String input1 = result[1];
	      String input3 = input1.substring(3, input1.indexOf("value"));
		  String input4 = input1.substring(input1.indexOf("value")+5,input1.length());
		  String dHash = genHash(input3);
	      String s_hash = genHash(Integer.toString(Successor));
	      String l_hash = genHash(Integer.toString(localPort));
	      String p_hash = genHash(Integer.toString(Predecessor));
	      Log.v("assignData","data "+input3+"  Successor "+Successor+" localPort "+localPort);
	      Log.v("assignData","hash is "+dHash);
	      boolean flag = true;
	      if (dHash.compareTo(l_hash) <= 0 && dHash.compareTo(p_hash) >= 0)
	      {	sequenceRecv++;
	        insertValue(result[1]);
	        Log.v("assignDataStore","storing data ..."+result[1]);
	        flag = false;
	        return;
	      } 
          if (l_hash.compareTo(s_hash) >= 0 && dHash.compareTo(l_hash) > 0) {
	    	Log.v("assignData","fwding to min node ..."+result[1]);
	    	sendData(Successor,data+"Store");
	    	flag = false;
	    	return;
	      } 
          
          if (dHash.compareTo(s_hash) < 0 && l_hash.compareTo(s_hash) >= 0) {
        	Log.v("assignData","fwding to min node ..."+result[1]);
        	sendData(Successor,data+"Store");
        	flag = false;
        	return;
          }
          
          if (flag == true) {
	    	Log.v("assignData","sending "+result[1]+" to "+Successor);  
	    	sendData(Successor, data);
	    	return;
	      }
	      
		} catch (Exception e) {
		  Log.v("assignData",e.getMessage());	
		}
	}
	
	public void sendData (int port, String data) {
	    Socket Cs = null;
	    try {
	      Log.v("sendData","Forwarding data "+data+" to "+port);	
	      Cs = new Socket(IP_ADDR,port*2);
	      PrintWriter out = new PrintWriter(Cs.getOutputStream(), true);
	      out.println(data);
	      Cs.close();
	    } catch (Exception e) {
	      Log.v("sendData",e.getMessage());	
	    }
	}	
	
	public void insertValue (String input1) {
		mNode = false;
		String input3 = input1.substring(3, input1.indexOf("value"));
		String input4 = input1.substring(input1.indexOf("value")+5,input1.length());
		ContentValues values = new ContentValues();
		values.put(DHT_t.DHT_tableval.COLUMN_1, Integer.parseInt(input3));
		values.put(DHT_t.DHT_tableval.COLUMN_2, input4);
		insertOK = true;
		insert(DHT_t.DHT_tableval.CONTENT_URI, values);
	}
	
	public void searchKey (String inputVal)
	{
		try {
          Log.v("searchKey","received   "+inputVal);	
		  String[] result = pattern.split(inputVal);	
		  String input1 = result[1];
		  String key = input1.replace("Key","");
		  int intKey = Integer.parseInt(key);
		  Log.v("searchKey","  key "+key+" intkey "+intKey+".");
//		  String input3 = input1.substring(3, input1.indexOf("value"));
//		  String input4 = input1.substring(input1.indexOf("value")+5,input1.length());
		  String k_hash = genHash(Integer.toString(intKey));
		  String s_hash = genHash(Integer.toString(Successor));
		  String l_hash = genHash(Integer.toString(localPort));
		  String p_hash = genHash(Integer.toString(Predecessor));
		  Log.v("searchKey","Key "+key+"  Successor "+Successor+" localPort "+localPort);
		  Log.v("searchKey","hash is "+k_hash);
		  boolean flag = true;
		  if (k_hash.compareTo(l_hash) <= 0 && k_hash.compareTo(p_hash) >= 0)
		  {	Log.v("searchKeyStore","Probably the right node. storing data ..."+key);
			sequenceRecv++;
		    keyValue(inputVal);
		    Log.v("searchKeyStore","storing data ..."+key);
		    flag = false;
		    return;
		  } 
	      if (l_hash.compareTo(s_hash) >= 0 && k_hash.compareTo(l_hash) > 0) {
		    Log.v("searchKey","fwding to min node ..."+result[1]);
		    sendData(Successor,inputVal+"Store");
		    flag = false;
		    return;
		  } 
	          
	      if (k_hash.compareTo(s_hash) < 0 && l_hash.compareTo(s_hash) >= 0) {
	        Log.v("searchKey","fwding to min node ..."+result[1]);
	        sendData(Successor,inputVal+"Store");
	        flag = false;
	        return;
	      }
	          
	      if (flag == true) {
		    Log.v("searchKey","sending "+result[1]+" to "+Successor);  
		    sendData(Successor, inputVal);
		    return;
		  }
		      
		} catch (Exception e) {
		  Log.v("searchKeyError",e.getMessage());	
	    } 	
	}
	
	public void keyValue(String inputVal)
	{
      Cursor c = null;
      
      Log.v("keyValue",""+inputVal);
      String[] result = pattern.split(inputVal);	
	  String input1 = result[1];
	  String key = input1.replace("Key","");
	  Log.v("keyValue","inside keyValue .... "+key);
  	  c = query(DHT_t.DHT_tableval.CONTENT_URI,null,null,null,null);
      boolean found = false;
      Log.v("keyValue",""+c);
  	  
      if (c.moveToFirst()) {
          do {
            String temp = c.getString(0)+"    "+c.getString(1);
            
            Log.v("Querying...",temp);
            if (key.compareTo(c.getString(0)) == 0)
            { Log.v("Found the value ....",""+key+"     "+c.getString(1));
              found = true;
              //return c;
              break;
            }
          } while (c.moveToNext());
      }
    
      String tempString = null;
      if (found == true)  
    	tempString = c.getString(1);
      else
    	tempString = "not found";  
      /*
      MatrixCursor Mc = new MatrixCursor(new String[] {
              DHT_t.DHT_tableval.COLUMN_1,
              DHT_t.DHT_tableval.COLUMN_2});
      if (found == true)
    	 Mc.newRow().add(c.getString(0)).add(c.getString(1));
      else
         Mc.newRow().add("-1").add("-1");
      Log.v("","");
      */
      /*
      if (found == false)
    	c = null;  
      //cur_key = c;*/
      try {
        Socket Cs = null;
        Cs = new Socket (IP_ADDR,Integer.parseInt(result[0])*2);
        PrintWriter out = new PrintWriter(Cs.getOutputStream(), true);
	    out.println(result[0]+":"+"KEYVALUE"+tempString);
	    Cs.close();
	    c.close();
	    //sKey = true;
      } catch (Exception e) {
    	Log.v("keyValue",e.getMessage());  
      } 
	}
	
} // end class ContentProviderDHT


