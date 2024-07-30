package com.systemsandcloud.tfg;

public class SenderMotorLoad extends Thread {
    private ConnectedThread mConnectedThread;

    public SenderMotorLoad(ConnectedThread m) {
        mConnectedThread = m;
    }

    @Override
    public void run() {
        while (true) {

            try {
                mConnectedThread.write("0104 1" + "\r" + "\n");
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
