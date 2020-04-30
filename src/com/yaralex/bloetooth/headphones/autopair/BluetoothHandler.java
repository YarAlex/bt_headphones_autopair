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
    private boolean connectingBt = false;

    private Process pacmdProcess;
    private BufferedReader pacmdBufferedReader;

    private String mac = "";
    private boolean starting = true;

    public BluetoothHandler(String mac) {
        btProcess = runBluetoothProcess();
        pacmdProcess = runPacmdProcess();
        this.mac = mac;
    }

    @Override
    public void run() {
        try {
            String line = readLine(bufferedReader);
            System.out.println("TEST2: "+line);
            parseResponse(line);
            pacmd();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private String readLine(BufferedReader br) throws IOException {
        return (br != null && br.ready()) ? br.readLine() : null;
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
        } else if (response.contains("No card found")) {

        }
    }

    private void connectBt() {
        //System.out.println("Connecting to headphones...");
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
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return process;
    }

    private Process runPacmdProcess() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("pacmd");
        processBuilder.redirectErrorStream(true);
        Process process = null;
        try {
            process = processBuilder.start();
            pacmdBufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return process;
    }

    private void pacmd() {
        //System.out.println("Set card profile...");
        PrintStream printStream = new PrintStream(pacmdProcess.getOutputStream(), true);
        printStream.println("set-card-profile bluez_card.1C_52_16_5E_04_46 a2dp_sink");
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
