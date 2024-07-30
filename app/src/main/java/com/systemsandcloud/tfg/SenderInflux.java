package com.systemsandcloud.tfg;

import android.app.Activity;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.domain.WritePrecision;
import java.time.Instant;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;

public class SenderInflux extends Thread {
    private static final String TAG="SenderInflux";
    private String TEMP, RPM, SPEED,LON,LAT,VIN,intake_manifold_absolute_pressure,motor_load,intake_manifold_air_temperature,air_flow_speed_maf,server;
    private TextView temp_tv, rpm_tv, speed_tv,latitude_tv,longitude_tv,vin_tv,intake_manifold_absolute_pressure_tv,motor_load_tv,intake_manifold_air_temperature_tv,air_flow_speed_maf_tv,server_tv;
    private Switch sw_sender_enable;
    private ConnectedThread mConnectedThread;
    static char[] token = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx".toCharArray();
    private static String org = "xxxxxxxx";
    private static String bucket = "xxxxxxxx";
    private Point punto = null;
    private InfluxDBClient influxDBClient;
    private WriteApiBlocking writeApi;
    private Activity activity;


    public SenderInflux(Activity m, ConnectedThread c) {
        activity = m;
        mConnectedThread = c;
        server_tv= (TextView) activity.findViewById(R.id.txt_server);
        influxDBClient = InfluxDBClientFactory.create(server_tv.getText().toString(), token, org, bucket);
        writeApi = influxDBClient.getWriteApiBlocking();
        temp_tv = (TextView) activity.findViewById(R.id.txt_temp);
        rpm_tv = (TextView) activity.findViewById(R.id.txt_rpm);
        speed_tv = (TextView) activity.findViewById(R.id.txt_speed);
        latitude_tv = (TextView) activity.findViewById(R.id.txt_latitude_activity);
        longitude_tv = (TextView) activity.findViewById(R.id.txt_longitude_activity);
        vin_tv = (TextView) activity.findViewById(R.id.txt_vin);

        intake_manifold_absolute_pressure_tv= (TextView) activity.findViewById(R.id.txt_intake_manifold_absolute_pressure);
        motor_load_tv= (TextView) activity.findViewById(R.id.txt_motor_load);
        intake_manifold_air_temperature_tv= (TextView) activity.findViewById(R.id.txt_intake_manifold_air_temperature);
        air_flow_speed_maf_tv= (TextView) activity.findViewById(R.id.txt_air_flow_speed_maf);

        sw_sender_enable = (Switch) activity.findViewById(R.id.switch1);

    }
   public void run() {
        while (true) {

                            try {
                                if ((mConnectedThread != null) && sw_sender_enable.isChecked()) {

                                    LAT = (String) latitude_tv.getText();
                                    LON = (String) longitude_tv.getText();


                                    TEMP = (String) temp_tv.getText();
                                    RPM = (String) rpm_tv.getText();
                                    SPEED = (String) speed_tv.getText();
                                    VIN = (String) vin_tv.getText();
                                    intake_manifold_absolute_pressure = (String) intake_manifold_absolute_pressure_tv.getText();
                                    motor_load=(String) motor_load_tv.getText();
                                    intake_manifold_air_temperature=(String) intake_manifold_air_temperature_tv.getText();
                                    air_flow_speed_maf=(String) air_flow_speed_maf_tv.getText();


                                    punto = Point
                                        .measurement("coches")
                                        .addTag("vin",VIN)                                        
                                        .addField("temperatura_refrigerante", Integer.parseInt(TEMP))
                                        .addField("revoluciones_por_minuto", Integer.parseInt(RPM))
                                        .addField("velocidad", Integer.parseInt(SPEED))
                                        .addField("latitud", Float.parseFloat(LAT))
                                        .addField("longitud", Float.parseFloat(LON))

                                        .addField("presion_absoluta_colector_admisión", Integer.parseInt(intake_manifold_absolute_pressure))
                                        .addField("carga_motor", Integer.parseInt(motor_load))
                                        .addField("temperatura_aire_colector_admisión", Integer.parseInt(intake_manifold_air_temperature))
                                        .addField("caudal_aire_sensor_maf", Integer.parseInt(air_flow_speed_maf))

                                        .time(Instant.now(), WritePrecision.NS);
                                if (sw_sender_enable.isChecked()) {
                                    writeApi.writePoint(bucket, org, punto);                                   
                                }
                                Thread.sleep(250);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            punto = null;
        }
   }
}

