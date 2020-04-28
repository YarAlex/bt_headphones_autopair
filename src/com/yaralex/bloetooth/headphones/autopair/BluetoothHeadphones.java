package com.yaralex.bloetooth.headphones.autopair;

public class BluetoothHeadphones {

    private static boolean start = true;

    public static void main(String[] args) {
        String mac = args[0];
        BluetoothHandler t = new BluetoothHandler(mac);
        t.start();
    }
}
