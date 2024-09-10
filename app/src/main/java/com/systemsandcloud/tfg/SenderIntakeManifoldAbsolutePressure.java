package com.systemsandcloud.tfg;

public class SenderIntakeManifoldAbsolutePressure extends Thread {
    private ConnectedThread mConnectedThread;

    public SenderIntakeManifoldAbsolutePressure(ConnectedThread m) {
        mConnectedThread = m;
    }

    @Override
    public void run() {
        while (true) {

            try {
                mConnectedThread.write("010B" + "\r" + "\n");
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
