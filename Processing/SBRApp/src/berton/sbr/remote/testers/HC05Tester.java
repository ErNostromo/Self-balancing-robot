package berton.sbr.remote.testers;

import berton.sbr.HC05;

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
            } catch (Exception e) {
                e.printStackTrace();
            }
            toPrint += "  -  " + (System.currentTimeMillis() - time) + " ms";
            System.out.println(toPrint);
        }
    }
}