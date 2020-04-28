package com.yaralex.bloetooth.headphones.autopair;

import com.google.common.base.Strings;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BluetoothHandler implements Runnable {

    private Process btProcess;
    private BufferedReader bufferedReader;
    private boolean servicesResolved = false;

    private Process pacmdProcess;

    private String mac = "";
    private boolean starting = true;

    public BluetoothHandler(String mac) {
        this.btProcess = runBluetoothProcess();
        this.mac = mac;
    }

    @Override
    public void run() {
        try {
            String line = bufferedReader.readLine();
            parseResponse(line);
        } catch (IOException e) {
            System.err.println(e.getMessage());
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
        bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        return process;
    }

    public void start() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(this, 0, 100, TimeUnit.MILLISECONDS);
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
