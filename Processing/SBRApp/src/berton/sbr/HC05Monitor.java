package berton.sbr;

import berton.sbr.remote.HC05;

public class HC05Monitor {
    public static void main(String[] args) {
        HC05 h = new HC05();
        String recv;
        try {
            h.connect();
            while (true) {
                recv = h.getStringFromHC05();
                if (!recv.equals("")) {
                    System.out.println(recv);
                    recv = "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
}