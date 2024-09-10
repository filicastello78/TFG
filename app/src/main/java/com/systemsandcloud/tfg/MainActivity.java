package com.systemsandcloud.tfg;


import android.annotation.SuppressLint;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Formatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static boolean user_permissions = false;
    private final String TAG = MainActivity.class.getSimpleName();

    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    
    private final static int REQUEST_ENABLE_BT = 1; 
    public final static int MESSAGE_READ = 2;
    private final static int CONNECTING_STATUS = 3; 

    
    private  TextView mBluetoothStatus;
    private  TextView txt_latitude;
    private  TextView txt_longitude ;
    private  TextView txt_temp_coolant;
    private  TextView txt_speedd;
    private  TextView txt_rpmm;

    private  TextView txt_intake_manifold_absolute_pressuree;
    private  TextView txt_load_motorr;
    private  TextView txt_intake_manifold_air_temperaturee;
    private  TextView txt_air_flow_speed_maff;
    public  TextView txt_vinn;
    private  Button mListPairedDevicesBtn;
    private  Button start_cmdd;

    private  ProgressBar progressBar;

    private  ListView mDevicesListView;


    private  static BluetoothAdapter mBTAdapter;
    private static Set<BluetoothDevice> mPairedDevices;
    private static ArrayAdapter<String> mBTArrayAdapter;

    private static Handler mHandler; 
    private static ConnectedThread mConnectedThread;
    private static BluetoothSocket mBTSocket = null; 
    private  Toolbar myToolbar ;
    private static final String patern_vin="87F1104902";

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        myToolbar =(Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar );

        progressBar = findViewById(R.id.pbHorizontal);

        start_cmdd = (Button) findViewById(R.id.btn_start);
        mBluetoothStatus = (TextView) findViewById(R.id.txt_bluetooth_status);
        txt_temp_coolant = (TextView) findViewById(R.id.txt_temp);
        txt_speedd = (TextView) findViewById(R.id.txt_speed);
        txt_rpmm = (TextView) findViewById(R.id.txt_rpm);
        txt_vinn = (TextView) findViewById(R.id.txt_vin);

        txt_intake_manifold_absolute_pressuree = (TextView) findViewById(R.id.txt_intake_manifold_absolute_pressure);
        txt_load_motorr = (TextView) findViewById(R.id.txt_motor_load);
        txt_intake_manifold_air_temperaturee = (TextView) findViewById(R.id.txt_intake_manifold_air_temperature);
        txt_air_flow_speed_maff = (TextView) findViewById(R.id.txt_air_flow_speed_maf);

        txt_latitude = (TextView) findViewById(R.id.txt_latitude_activity);
        txt_longitude = (TextView) findViewById(R.id.txt_longitude_activity);

        mListPairedDevicesBtn = (Button) findViewById(R.id.btn_paireds);
        mBTArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        mDevicesListView = (ListView) findViewById(R.id.devices_list_view);
        mDevicesListView.setAdapter(mBTArrayAdapter); 
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            user_permissions=true;
        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        double latitude = intent.getDoubleExtra(GpsSenderService.MSG_LATITUDE, 0);
                        double longitude = intent.getDoubleExtra(GpsSenderService.MSG_LONGITUDE, 0);
                        txt_latitude.setText(String.valueOf(latitude));
                        txt_longitude.setText(String.valueOf(longitude));
                        
                    }
                }, new IntentFilter(GpsSenderService.ACTION_LOCATION_BROADCAST)
        );
        progressBar.setMax(4000);      

        mHandler = new Handler(Looper.getMainLooper()) {
            double j=0.0;
            int A,B,value=0;
            String readMessage,aux1,aux2="";
            

            @Override
            public void handleMessage(Message msg) {

                if (msg.what == MESSAGE_READ) {
                    readMessage = new String((byte[]) msg.obj, StandardCharsets.UTF_8);                   

                    switch (msg.arg1) {
                        case 0://coolant temp
                            aux1 = readMessage.substring(10, 12);
                            A = Integer.parseInt(aux1, 16);
                            txt_temp_coolant.setText(String.valueOf(Math.abs(A - 40)));
                            break;

                        case 1://rpm
                            aux1 = readMessage.substring(10, 12);
                            aux2 = readMessage.substring(12, 14);
                            A = Integer.parseInt(aux1, 16);
                            B = Integer.parseInt(aux2, 16);
                            value = (((256 * A) + B) / 4);
                            txt_rpmm.setText(String.valueOf(value));
                            progressBar.setProgress(value);
                            if (value > 2000) {
                                    progressBar.getProgressDrawable().setColorFilter(
                                            Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
                                   
                                }
                                else{
                                    progressBar.getProgressDrawable().setColorFilter(
                                            Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);


                                }

                            break;


                        case 2://speed
                            aux1 = readMessage.substring(10, 12);
                            A = Integer.parseInt(aux1, 16);
                            txt_speedd.setText(String.valueOf(A));
                            break;

                        case 3://VIN
                            aux1 = readMessage;
                            String[] chunks = aux1.split(patern_vin);
                            aux2="";
                            for (int i=1;i<6;i++) {
                                byte[] byteArray = hexStringToByteArray(chunks[i].substring(2));
                                String s2 = new String(byteArray, StandardCharsets.UTF_8);
                                aux2+=cleanTextContent(s2);
                            }
                            txt_vinn.setText(aux2.substring(2));
                            break;
                        case 4://intake_manifold_absolute_pressure
                            aux1 = readMessage.substring(10, 12);
                            A = Integer.parseInt(aux1, 16);
                            txt_intake_manifold_absolute_pressuree.setText(String.valueOf(A));
                            break;
                        case 5://motor_load
                            aux1 = readMessage.substring(10, 12);
                            A = Integer.parseInt(aux1, 16);
                            j = A/2.55;
                            txt_load_motorr.setText(String.valueOf((int)Math.round(j)));
                            break;
                        case 6://intake_manifold_air_temperature
                            aux1 = readMessage.substring(10, 12);
                            A = Integer.parseInt(aux1, 16);
                            txt_intake_manifold_air_temperaturee.setText(String.valueOf(Math.abs(A - 40)));
                            break;
                        case 7://air_flow_speed_maf
                            aux1 = readMessage.substring(10, 12);
                            aux2 = readMessage.substring(12, 14);
                            A = Integer.parseInt(aux1, 16);
                            B = Integer.parseInt(aux2, 16);
                            txt_air_flow_speed_maff.setText(String.valueOf(((256 * A) + B) / 100));
                            break;
                        default:
                            Log.v(TAG, "msg.arg1:" + msg.arg1);
                    }

                }

                if (msg.what == CONNECTING_STATUS) {
                    
                    if (msg.arg1 == 1)
                        mBluetoothStatus.setText(getString(R.string.BTConnected) + msg.obj);
                    else
                        mBluetoothStatus.setText(getString(R.string.BTconnFail));
                }
                j=0.0;
                A=0;
                B=0;
                value=0;
                readMessage="";
                aux1="";
                aux2="";
            }
        };

        if (mBTArrayAdapter == null) {
            mBluetoothStatus.setText(getString(R.string.sBTstaNF));
            Toast.makeText(getApplicationContext(), getString(R.string.sBTdevNF), Toast.LENGTH_SHORT).show();
        } else {

            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listPairedDevices();
                }
            });

            start_cmdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initialize();
                }
            });


        }
    }

    @NonNull
    private static String cleanTextContent(String text) {
        // strips off all non-ASCII characters
        text = text.replaceAll("[^\\x00-\\x7F]", "");

        // erases all the ASCII control characters
        text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

        // removes non-printable characters from Unicode
        text = text.replaceAll("\\p{C}", "");

        return text.trim();
    }
    @NonNull
    public static byte[] hexStringToByteArray(@NonNull String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    @SuppressLint("MissingPermission")
    private void initialize() {

        if (mConnectedThread != null) {
            mConnectedThread.write("ATZ"+ "\r" + "\n");//Reset
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            mConnectedThread.write("ATPP0CSV23" + "\r" + "\n");//115200 baudios
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            mConnectedThread.write("ATPP0CON"+ "\r" + "\n" );//Enable Programable Parameter
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            mConnectedThread.write("ATZ" + "\r" + "\n");//Reset
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            mConnectedThread.write("ATSP0"+ "\r" + "\n");//Automatic protocol
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            mConnectedThread.write("ATE0"+ "\r" + "\n" );//echo OFF
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            mConnectedThread.write("ATH1"+ "\r" + "\n" );//Enable header
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
          
            mConnectedThread.write("0105"+ "\r" + "\n"+ "\r" + "\n" );//TEMP
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            mConnectedThread.write("0902"+ "\r" + "\n" );//VIN
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }             

            SenderCoolantTemp worker_temp_coolant = new SenderCoolantTemp(mConnectedThread);
            worker_temp_coolant.start();
            worker_temp_coolant.setPriority(5);

            SenderSpeed worker_speed = new SenderSpeed(mConnectedThread);
            worker_speed.start();
            worker_speed.setPriority(5);

            SenderRpm worker_rpm = new SenderRpm(mConnectedThread);
            worker_rpm.start();
            worker_rpm.setPriority(5);


            SenderIntakeManifoldAbsolutePressure worker_intake_manifold_absolute_pressure = new SenderIntakeManifoldAbsolutePressure(mConnectedThread);
            worker_intake_manifold_absolute_pressure.start();
            worker_intake_manifold_absolute_pressure.setPriority(5);


            SenderMotorLoad worker_motor_load = new SenderMotorLoad( mConnectedThread);
            worker_motor_load.start();
            worker_motor_load.setPriority(5);

            SenderIntakeManifoldAirTemperature worker_intake_manifold_air_temperature = new SenderIntakeManifoldAirTemperature(mConnectedThread);
            worker_intake_manifold_air_temperature.start();
            worker_intake_manifold_air_temperature.setPriority(5);

            SenderAirFlowSpeedMaf worker_air_flow_speed_maf = new SenderAirFlowSpeedMaf(mConnectedThread);
            worker_air_flow_speed_maf.start();
            worker_air_flow_speed_maf.setPriority(5);

            SenderInflux worker_influx = new SenderInflux(this, mConnectedThread);
            worker_influx.start();
            worker_influx.setPriority(5);

        }
    }



    @SuppressLint("MissingPermission")
    private void listPairedDevices() {
        mBTArrayAdapter.clear();

        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            Toast.makeText(getApplicationContext(), getString(R.string.show_paired_devices), Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), getString(R.string.BTnotOn), Toast.LENGTH_SHORT).show();
    }

    private final AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), getString(R.string.BTnotOn), Toast.LENGTH_SHORT).show();
                return;
            }

            mBluetoothStatus.setText(getString(R.string.cConnet));
           
            String info = ((TextView) view).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

           
            new Thread()
            {
                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), getString(R.string.ErrSockCrea), Toast.LENGTH_SHORT).show();
                    }
                    
                    try {
                        mBTSocket.connect();

                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                        } catch (IOException e2) {                           
                            Toast.makeText(getBaseContext(), getString(R.string.ErrSockCrea), Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(!fail) {
                        try {
                            mConnectedThread = new ConnectedThread(mBTSocket, mHandler);
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        mConnectedThread.start();
                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name).sendToTarget();

                    }
                }
            }.start();


        }
    };

    @SuppressLint("MissingPermission")
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==123) {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    user_permissions = true;
                    onResume();
                } else {
                    user_permissions = false;
                    f_exit();
                }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (user_permissions)
            startService(new Intent(this, GpsSenderService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, GpsSenderService.class));


    }

public void f_exit(){
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    MainActivity.this.finish();
    System.exit(0);
}
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tfg, menu);
        return true;
  
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.item_salir) {
            f_exit();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

}
