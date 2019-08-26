package berton.sbr.remote.testers;

import java.io.IOException;

import berton.sbr.remote.HC05;

public class HC05Tester {
    public static void main(String[] args) {
        HC05 a = new HC05();
        try {
            a.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long time;
        String toPrint = "";
        while (a.isConnected()) {
            time = System.currentTimeMillis();
            try {
                toPrint = a.getStringFromHC05();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(toPrint);
            System.out.println(System.currentTimeMillis() - time);
            time = System.currentTimeMillis();
        }
    }
}