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
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.regex.Pattern;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class SimpleDHTActivity2 extends Activity {
	
	private static final String TAG = "MyActivity";	
	public static Button button1,button2,button3,button4;
	public static EditText   txtbox1,txtbox2,txtbox3;
	public static TextView tv, tv2;
	//public int localPort,sequenceNumber = 0, sequenceRecv = 0, Tcount = 3, sequenceExp = 0;
	
	// variables below this pt to be used in ContentProvider
	public Pattern pattern = Pattern.compile("[\\:]");
	public int sequenceRecv = -1, msgSeq = 0;
	public int localPort = 0, Successor = 0, Predecessor = 0;
	public boolean join = false, sendDataOK = false, mNode = false;
	public String IP_ADDR = "10.0.2.2";
    public String[] dataValues = null;
    public String sendMessage = null;
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	String portStr = null;
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        tel.getLine1Number();
//        portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
//        localPort = Integer.parseInt(portStr);
        txtbox1 = (EditText) findViewById(R.id.editText1);
        txtbox2 = (EditText) findViewById(R.id.editText2);
        tv2 = (TextView) findViewById(R.id.textView2);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button3.setText("DEL");
        button1.setText("INSERT");
        button2.setText("QUERY");
        button4.setText("DUMP");
        txtbox1.setText("Add key to query");
        txtbox2.setText("query output");
        tv2.setText("");
//		TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        tel.getLine1Number();
//        String temp = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
//        localPort = Integer.parseInt(temp);
//        Temp_localport t = new Temp_localport(localPort);
        button1.setOnClickListener(new clicker1());
        button2.setOnClickListener(new clicker12());
        button3.setOnClickListener(new clicker3());
        button4.setOnClickListener(new clicker4());
//        try {
//          Log.v("main","hash Value is "+genHash(Integer.toString(localPort))+"     "+localPort);
//        } catch (Exception e) {
//          Log.v("main",e.getMessage());	
//        }
    } // end public void onCreate
    
    class  clicker1 implements Button.OnClickListener
    {   int port = localPort;
    	@Override
        public void onClick(View v) {
    	  
    	  //if(sequenceRecv == -1)
    	  //{	
    		/*  
  	        ContentValues values = new ContentValues();
            values.put(DHT_t.DHT_tableval.COLUMN_1, sequenceRecv);
            //values.put(DHT_t.DHT_tableval.COLUMN_2, "sample"+sequenceRecv);
            values.put(DHT_t.DHT_tableval.COLUMN_2, "port"+port+":");
            Log.v("clicker1","   "+values+"   ... "+port);
            getContentResolver().insert(DHT_t.DHT_tableval.CONTENT_URI, values);
            sequenceRecv++;*/
    	  //} else {
    		Log.v("clicker1","adding values ......");  
      	    ContentValues values = new ContentValues();
      	    for (int i = 0; i <10; i++) {
              values.put(DHT_t.DHT_tableval.COLUMN_1, i);
              values.put(DHT_t.DHT_tableval.COLUMN_2, "Test"+i);
              //values.put(DHT_t.DHT_tableval.COLUMN_2, "port"+port+":");
              getContentResolver().insert(DHT_t.DHT_tableval.CONTENT_URI, values);
              //Log.v("clicker1","   "+values+"   ... "+port);
              try {
            	Thread.sleep(2000);  
              } catch (Exception e) {
            	Log.v("clicker1",e.getMessage());  
              }
    	    //}
    	  }
          //sequenceRecv = sequenceRecv%2;
          
//          Thread St = new Thread(new ServerThread());
//          Thread Ct = new Thread(new ClientThread());
//          St.start();
//          Ct.start();
        }
    } // end clicker1
    
    
    class clicker12 implements Button.OnClickListener
    {   @Override
    	public void onClick(View v) {
    	  Log.v("clicker2","clicked....");
    	  
    	  int val = Integer.parseInt(txtbox1.getText().toString());
    	  Cursor cursor = null;
    	  cursor = getContentResolver().query(DHT_t.DHT_tableval.CONTENT_URI,null,txtbox1.getText().toString(),null,null);
          String[] result = cursor.getColumnNames();
    	  Log.v("clicker12",result[0]+"   "+result[1]);
    	  String temp;
    	  boolean found = false;
    	  if (cursor.moveToFirst())
    	  do
    	  {
    		Log.v("cursor...",cursor.getString(0)+"    "+cursor.getString(1));
    		if (cursor.getString(0).equals(txtbox1.getText().toString()))
    		{ Log.v("cursor...","found ...    "+cursor.getString(0)+"    "+cursor.getString(1));
    		  txtbox2.setText(cursor.getString(1));
    		  found = true;
    		  break;
    		}
    		if (cursor.getString(1) != null)
    		{ txtbox2.setText(cursor.getString(1));
    		  found = true;
    		}
    	  } while (cursor.moveToNext());
    	  if (found == false)
    		txtbox2.setText("key not found");  
    	  /*
    	  if (cursor != null)
        	txtbox2.setText(cursor.getString(1));
          else 
        	txtbox2.setText("not found");
          */  
    	  /*if (cursor.moveToFirst()) {
            do {
              String temp = cursor.getString(0)+"    "+cursor.getString(1);
              Log.v("Querying...",temp);
              if (val == Integer.parseInt(cursor.getString(0)))
                txtbox2.setText(temp); 
            } while (cursor.moveToNext());
          } */
          cursor.close();
        }
    } // end clicker2
    
    class clicker2 implements Button.OnClickListener
    {    @Override
    	 public void onClick(View V) {
    	   Log.v("clicker2","sending data ...");
    	   for (int i = 0; i < 10; i++)
    	   {
    		 sendMessage = ""+i;
    		 sendDataOK = true;
    		 try {
    		   Thread.sleep(1000);
    		 } catch (Exception e) {
    		   Log.v("clicker2",e.getMessage());	 
    		 }
    	   }
         }
    }
    class clicker3 implements Button.OnClickListener
    {
		@Override
		public void onClick(View v) {
		  //Cursor cursor = null;
		  Log.v("clicker3",""+DHT_t.DHT_tableval.CONTENT_URI);	
	      getContentResolver().delete(DHT_t.DHT_tableval.CONTENT_URI,null,null);
		  //txtbox2.setText("Successor "+Successor+"   Pred "+Predecessor);
		}   
    } // end clicker3
    
    class clicker4 implements Button.OnClickListener
    {
        @Override
        public void onClick(View v) {
      	  Cursor cursor = null;
      	  tv2.setText("");
      	  cursor = getContentResolver().query(DHT_t.DHT_tableval.CONTENT_URI,null,null,null,null);
      	  if (cursor.moveToFirst()) {
            do {
              String temp = cursor.getString(0)+"    "+cursor.getString(1);
              Log.v("Querying...",temp);
              //tv2 = tv2.append("\n"+cursor.getString(1));
              tv2.setText(tv2.getText().toString()+"\n"+cursor.getString(1));
            } while (cursor.moveToNext());
          }
      	  cursor.close();
        }  
    }
    /*
    private String genHash(String input) throws NoSuchAlgorithmException {
		MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
		byte[] sha1Hash = sha1.digest(input.getBytes());
		Formatter formatter = new Formatter();
		for (byte b : sha1Hash) {
		formatter.format("%02x", b);
		}
		return formatter.toString();
    } // end String genHash
	*/

} // end SimpleDHTActivity

