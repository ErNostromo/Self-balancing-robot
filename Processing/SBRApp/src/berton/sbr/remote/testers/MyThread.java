package berton.sbr.remote.testers;

public class MyThread extends Thread {
    private volatile boolean quit;
    private long count;

    public MyThread() {
        quit = false;
        count = Long.MIN_VALUE;
    }

    @Override
    public void run() {
        System.out.println("MyThread started");
        long time = System.currentTimeMillis();
        while (!quit && System.currentTimeMillis() < time + 20000) {
        }
        System.out.println("MyThread stopped (" + quit + ")");
    }

    public void quit() {
        quit = true;
        System.out.println("quit");
    }
}