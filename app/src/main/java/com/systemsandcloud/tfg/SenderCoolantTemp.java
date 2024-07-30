package com.systemsandcloud.tfg;

public class SenderCoolantTemp extends Thread {
    private ConnectedThread mConnectedThread;

    public SenderCoolantTemp(ConnectedThread m) {
        mConnectedThread = m;
    }

    @Override
    public void run() {
        while (true) {

            try {
                mConnectedThread.write("0105"+ "\r" + "\n");
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
