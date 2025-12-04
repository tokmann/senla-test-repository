package task4;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        ServiceThread serviceThread = new ServiceThread(5);

        serviceThread.setDaemon(true);
        serviceThread.start();

        Thread.sleep(20 * 1000);
    }

}


class ServiceThread extends Thread {

    private final int n;

    public ServiceThread(int n) {
        this.n = n;
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println(LocalDateTime.now());
                Thread.sleep(n * 1000);
            } catch (InterruptedException e) {
                System.out.println("Ошибка: " + e);
            }
        }
    }

}