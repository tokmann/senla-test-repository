package task2;

public class Main {

    private final static Object lock = new Object();
    private static volatile int currentTurn = 1;

    public static void main(String[] args) throws InterruptedException {

        Thread thread1 = new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    try {

                        while (currentTurn != 1) {
                            lock.wait();
                        }

                        Thread.sleep(1000);
                        System.out.println("Текущий поток: " + Thread.currentThread().getName());

                        currentTurn = 2;
                        lock.notifyAll();

                    } catch (InterruptedException e) {
                        System.out.println("Ошибка: " + e);
                    }
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    try {

                        while (currentTurn != 2) {
                            lock.wait();
                        }

                        Thread.sleep(1000);
                        System.out.println("Текущий поток: " + Thread.currentThread().getName());

                        currentTurn = 1;
                        lock.notifyAll();

                    } catch (InterruptedException e) {
                        System.out.println("Ошибка: " + e);
                    }
                }
            }
        });

        thread1.setDaemon(true);
        thread2.setDaemon(true);

        thread1.start();
        thread2.start();

        Thread.sleep(10 * 1000);
    }

}
