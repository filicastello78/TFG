package com.systemsandcloud.tfg;

import static com.systemsandcloud.tfg.MainActivity.*;

import static java.lang.System.exit;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class ConnectedThread extends Thread {
    private String TAG = "ConnectedThread";
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final Handler mHandler;
    private  BufferedReader br;
    private  BufferedWriter bw;

    public ConnectedThread(BluetoothSocket socket, Handler handler) throws UnsupportedEncodingException {
        mmSocket = socket;
        mHandler = handler;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
       
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }

        mmInStream = tmpIn;

        mmOutStream = tmpOut;
        bw = new BufferedWriter(new OutputStreamWriter(mmOutStream));
        br = new BufferedReader(new InputStreamReader(mmInStream));

    }

    @Override
    public  void run() {

        String partialMessage = "";

        
        while (true) {
            try {
                if ((mmSocket!=null) && (br.ready())) {
                    partialMessage = br.readLine();
                    
                    if (!partialMessage.isEmpty()) {
                        if (partialMessage.startsWith(">") && !partialMessage.contains("ATZ") && !partialMessage.contains("ELM327") && !partialMessage.contains("ATPP0CSV23") && !partialMessage.contains("ATPP0CON") && !partialMessage.contains("ATSP0") && !partialMessage.contains("ATSP0") && !partialMessage.contains("ATE0") && !partialMessage.contains("ATH1") && !partialMessage.contains("SEARCHING") && !partialMessage.contains("OK")) {

                            partialMessage = partialMessage.replaceAll(">", "");
                            partialMessage = partialMessage.replaceAll("\\s+", "");

                            
                            if (partialMessage.length() >= 10) {
                                String code = partialMessage.substring(6, 10);                                

                                switch (code) {
                                    case "4105":
                                        //Log.v(TAG","he recibido TEMP desde el coche");
                                        Message readMsg0 = mHandler.obtainMessage(MainActivity.MESSAGE_READ, 0, -1, partialMessage.getBytes());
                                        readMsg0.sendToTarget();
                                        break;
                                    case "410C":
                                        //Log.v(TAG,"he recibido RPM desde el coche");
                                        Message readMsg1 = mHandler.obtainMessage(MainActivity.MESSAGE_READ, 1, -1, partialMessage.getBytes());
                                        readMsg1.sendToTarget();
                                        break;
                                    case "410D":
                                        //Log.v(TAG,"he recibido SPEED desde el coche");
                                        Message readMsg2 = mHandler.obtainMessage(MainActivity.MESSAGE_READ, 2, -1, partialMessage.getBytes());
                                        readMsg2.sendToTarget();
                                        break;

                                    case "4902":
                                        //Log.v(TAG,"he recibido VIN desde el coche");
                                        String res_aux;
                                        for (int i = 1; i <= 4; i++) {
                                            res_aux = br.readLine();
                                            res_aux = res_aux.replaceAll(">", "");
                                            res_aux = res_aux.replaceAll("\\s+", "");

                                            partialMessage += res_aux;

                                            res_aux = "";
                                        }
                                        Message readMsg3 = mHandler.obtainMessage(MainActivity.MESSAGE_READ, 3, -1, partialMessage.getBytes());
                                        readMsg3.sendToTarget();
                                        break;


                                    case "410B":
                                        //Log.v(TAG,"he recibido intake_manifold_absolute_pressure desde el coche");
                                        Message readMsg4 = mHandler.obtainMessage(MainActivity.MESSAGE_READ, 4, -1, partialMessage.getBytes());
                                        readMsg4.sendToTarget();
                                        break;

                                    case "4104":
                                        //Log.v(TAG,"he recibido motor_load desde el coche");
                                        Message readMsg5 = mHandler.obtainMessage(MainActivity.MESSAGE_READ, 5, -1, partialMessage.getBytes());
                                        readMsg5.sendToTarget();
                                        break;

                                    case "410F":
                                        //Log.v(TAG,"he recibido intake_manifold_air_temperature desde el coche");
                                        Message readMsg6 = mHandler.obtainMessage(MainActivity.MESSAGE_READ, 6, -1, partialMessage.getBytes());
                                        readMsg6.sendToTarget();
                                        break;

                                    case "4110":
                                        //Log.v(TAG,"he recibido air_flow_speed_maf desde el coche");
                                        Message readMsg7 = mHandler.obtainMessage(MainActivity.MESSAGE_READ, 7, -1, partialMessage.getBytes());
                                        readMsg7.sendToTarget();
                                        break;
                                    default:
                                        Log.v(TAG, "Code not identified: " + code);
                                        Log.v(TAG, "Message:" + partialMessage);
                                }
                            } else {

                                Log.v(TAG, "Message not found: " + partialMessage);
                            }
                        }
                    }

                    partialMessage = "";
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

    }


  
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }

    public  void write(String input) {
        try {
                bw.write(input);
                bw.flush();
                //Log.v(TAG, "envio cadena:" + input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}