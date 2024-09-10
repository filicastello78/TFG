package com.systemsandcloud.tfg;

public class SenderIntakeManifoldAirTemperature extends Thread {
    private ConnectedThread mConnectedThread;

    public SenderIntakeManifoldAirTemperature(ConnectedThread m) {
        mConnectedThread = m;
    }

    @Override
    public void run() {
        while (true) {

            try {
                mConnectedThread.write("010F" + "\r" + "\n");
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
