package com.systemsandcloud.tfg;

public class SenderRpm extends Thread {
    private ConnectedThread mConnectedThread;

    public SenderRpm(ConnectedThread m) {
        mConnectedThread = m;
    }

    @Override
    public void run() {
        while (true) {

            try {
                if ( mConnectedThread != null) {
                    mConnectedThread.write("010C 1" + "\r" + "\n");                   
                    Thread.sleep(5);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
