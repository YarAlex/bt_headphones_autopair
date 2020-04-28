package com.yaralex.bloetooth.headphones.autopair;

import java.io.*;

public class BluetoothHeadphones {

    private static boolean start = true;

    public static void main(String[] args) throws IOException, InterruptedException {
        String mac = args[0];
        BluetoothHandler t = new BluetoothHandler(mac);
        t.start();

        /*while (start) {
            Thread.sleep(100);
        }
        t.setStarting(false);*/
    }
}
