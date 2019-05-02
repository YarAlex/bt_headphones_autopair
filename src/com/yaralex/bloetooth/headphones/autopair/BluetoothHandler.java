package com.yaralex.bloetooth.headphones.autopair;

import java.io.*;

public class BluetoothHandler extends Thread {

    private Process process;
    private boolean servicesResolved = false;
    private String mac = "";
    private boolean starting = true;

    public BluetoothHandler(Process process, String mac) {
        this.process = process;
        this.mac = mac;
    }

    @Override
    public void run() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        while (starting) {
            try {
                String line = bufferedReader.readLine();
                if (line != null) {
                    parseResponse(line);
                } else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

    }

    private void parseResponse(String response) {
        if (response == null || response.isEmpty()) {
            return;
        }
        if (response.contains("ServicesResolved")) {
            servicesResolved = true;
        } else if (response.contains("Connected")) {
            if (response.substring(response.lastIndexOf(" ")+1).equals("no")) {
                System.out.println("Connected no");
                connect();
            } else {
                servicesResolved = false;
                System.out.println("Connected yes");
            }
        }
    }

    private void connect() {
        if (!servicesResolved) {
            System.out.println("Connect headphones");
            PrintStream printStream = new PrintStream(process.getOutputStream(), true);
            printStream.println("connect " + mac);
        }
    }

    public void setMac (String mac) {
        this.mac = mac;
    }

    public void setStarting(boolean starting) {
        this.starting = starting;
    }

    public boolean isStarting() {
        return starting;
    }
}
