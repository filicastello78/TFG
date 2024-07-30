package com.systemsandcloud.tfg;

public class SenderSpeed extends Thread {
    private ConnectedThread mConnectedThread;

    public SenderSpeed(ConnectedThread m) {
        mConnectedThread = m;
    }

    @Override
    public void run() {
        while (true) {

            try {
               if ( mConnectedThread != null) {
                   mConnectedThread.write("010D 1" + "\r" + "\n");
               }
                Thread.sleep(30);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
