package berton.sbr.remote.testers;

public class MyThreadTester {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("MainThread started");
        MyThread t = new MyThread();
        t.start();
        Thread.sleep(500);
        t.quit();
        System.out.println("MainThread ended");
    }
}