package berton.sbr.remote.testers;

public class MyThread extends Thread {
    public MyThread() {

    }

    @Override
    public void run() {
        System.out.println("MyThread started");
        try {
            Thread.sleep(2000);
            System.out.println("MyThread ended");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}