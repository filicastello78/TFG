package com.systemsandcloud.tfg;

public class SenderAirFlowSpeedMaf extends Thread {
    private ConnectedThread mConnectedThread;

    public SenderAirFlowSpeedMaf(ConnectedThread m) {
        mConnectedThread = m;
    }

    @Override
    public void run() {
        while (true) {

            try {
                mConnectedThread.write("0110" + "\r" + "\n");
                Thread.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
