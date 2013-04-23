package edu.buffalo.cse.cse486_586.simpledht;

import java.io.PrintWriter;
import java.net.Socket;

import android.util.Log;

class Temp 
{
  public static boolean val = false;
  public Temp ()
  {
  }
  public void TempEnt (boolean val2)
  {
	Log.v("Temp","Temp value changed to "+val2);  
	val = val2;  
  }
  public boolean TempRtrv ()
  {
	return val;  
  }
}

class Temp_localport
{
  static int localPort;
  public Temp_localport ()
  {
  }
  public Temp_localport (int localPort)
  { this.localPort = localPort;
  }
  public int localport()
  {
	return localPort;  
  }
}


