package com.yaralex.bloetooth.headphones.autopair;

import java.io.*;

public class BluetoothHeadphones {

    private static boolean start = true;

    public static void main(String[] args) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bluetoothctl");
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        String mac = args[0];
        BluetoothHandler t = new BluetoothHandler(process, mac);
        t.start();

        while (start) {
            Thread.sleep(100);
        }
        t.setStarting(false);
    }
}
