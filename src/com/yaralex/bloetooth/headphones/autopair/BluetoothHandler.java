package com.yaralex.bloetooth.headphones.autopair;

import com.google.common.base.Strings;

import java.io.*;

public class BluetoothHandler extends Thread {

    private Process btProcess;
    private Process pacmdProcess;
    private boolean servicesResolved = false;
    private String mac = "";
    private boolean starting = true;

    public BluetoothHandler(String mac) {
        this.btProcess = runBluetoothProcess();
        this.mac = mac;
    }

    @Override
    public void run() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(btProcess.getInputStream()));

        while (starting) {
            try {
                String line = bufferedReader.readLine();
                parseResponse(line);
                Thread.sleep(100);
            } catch (IOException | InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }

    }

    private void parseResponse(String response) {
        if (Strings.isNullOrEmpty(response)) {
            return;
        }

        if (response.contains("ServicesResolved")) {
            servicesResolved = true;
        } else if (response.contains("Connected: yes")){
            servicesResolved = false;
        } else if (response.contains("Connected: no") && !servicesResolved) {
            connectBt();
        }
    }

    private void connectBt() {
        System.out.println("Connecting to headphones...");
        PrintStream printStream = new PrintStream(btProcess.getOutputStream(), true);
        printStream.println("connect " + mac);
    }

    private Process runBluetoothProcess() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bluetoothctl");
        processBuilder.redirectErrorStream(true);
        Process process = null;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return process;
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
