package com.banana.projectapp.main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class ClientDiProva_NonUtilizzato {
	
	protected static final String TAG = "ClientDiProva_NonUtilizzato";
	private static Thread t;
	static String server = "192.168.1.100";
	static int port = 11111;
	
	public void sendObject(final Object o){
		t = new Thread(new Runnable(){
			public void run(){
				Socket socket = null;
		        try {
		        	InetAddress ip = InetAddress.getByName(server);
		        	Log.i(TAG, "provo a aprire il socket verso "+ip.getHostAddress());
					socket = new Socket (ip,port);
					Log.i(TAG, "aperto socket");
					ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
					
					Log.i(TAG, "vorrei scrivere il token, se possibile");
					out.writeObject(o);
					out.flush();
					Log.i(TAG, "scritto!!!");
					String fromServer = (String) in.readObject();
					
					Log.i(TAG, "il server ha risposto: " + fromServer);				
				} catch (UnknownHostException e) {
					Log.i(TAG, "aaa"+e.toString());
				} catch (IOException e) {
					Log.i(TAG,"aaa"+e.toString());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
		        finally{
					try {
						if (socket!=null)
							socket.close();
						Log.i(TAG, "chiuso socket");
					} catch (IOException e) {
						e.printStackTrace();
					}
		        }
			}
		});
		t.start();
	}
}	
