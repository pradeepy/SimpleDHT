
/*
 * 
 * 
 * 
 * 
 * 
 *    Use SimpleDHTActivity2.java instead of this file
 * 
 * 
 * 
 * 
 * 
 */



/*
package edu.buffalo.cse.cse486_586.simpledht;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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

public class SimpleDHTActivity extends Activity {
	
	private static final String TAG = "MyActivity";	
	public static Button button1,button2,button3;
	public static EditText   txtbox1,txtbox2,txtbox3;
	public static TextView tv, tv2;
	public int localPort,sequenceNumber = 0, sequenceRecv = 0, Tcount = 3, sequenceExp = 0;
	public Pattern pattern = Pattern.compile("[\\:]");
	Queue queueMsg = new PriorityQueue();
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	String portStr = null;
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tel.getLine1Number();
        portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        localPort = Integer.parseInt(portStr);
        txtbox1 = (EditText) findViewById(R.id.editText1);
        txtbox2 = (EditText) findViewById(R.id.editText2);
        tv2 = (TextView) findViewById(R.id.textView2);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button1.setText("Serverv1.0");
        button1.setOnClickListener(new clicker1());
        button2.setOnClickListener(new clicker2());
        button3.setOnClickListener(new clicker3());
        button3.setText("test01");
    } // end public void onCreate
    
    class clicker1 implements Button.OnClickListener
    {
		@Override
		public void onClick(View v) {
		}
    } // end class clicker1

    class clicker2 implements Button.OnClickListener
    {
		@Override
		public void onClick(View v) {
		}
    } // end class clicker2
    
    class clicker3 implements Button.OnClickListener
    {
		@Override
		public void onClick(View v) {
		}
    } // end class clicker3
    
    class clicker4 implements Button.OnClickListener
    {
		@Override
		public void onClick(View v) {
		}
    } // end class clicker4
    
    class ServerThread implements Runnable {
    	ServerSocket Ss = null;
		@Override
		public void run() {
	      try {
			Ss = new ServerSocket(10000);
		  } catch (IOException e) {
			Log.v("ServerSocket",e.getMessage());
		  }		
		}
    } // end class ServerThread
    
    class ClientThread implements Runnable {
		@Override
		public void run() {
		}
    }

} // end SimpleDHTActivity

*/
