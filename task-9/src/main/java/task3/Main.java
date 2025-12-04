package task3;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Main {

    private static final Object lock = new Object();
    private static final Queue<Integer> buffer = new LinkedList<>();
    private static final int MAX_CAPACITY = 5;
    private static final Random random = new Random();

    public static void main(String[] args) throws InterruptedException {
        Thread producer = new Thread(() -> {
            while (true) {
                int num = random.nextInt(100) + 1;
                synchronized (lock) {
                    try {
                        while (buffer.size() == MAX_CAPACITY) {
                            lock.wait();
                        }

                        buffer.add(num);
                        System.out.println("[Поток производитель] добавил " + num + " (Размер буфера: " + buffer.size() + ")");

                        lock.notifyAll();

                        Thread.sleep(500);

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        Thread consumer = new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    try {
                        while (buffer.isEmpty()) {
                            lock.wait();
                        }

                        int num = buffer.poll();
                        System.out.println("[Поток потребитель] забрал " + num + " (Осталось в буфере: " + buffer.size() + ")");

                        lock.notifyAll();

                        Thread.sleep(500);

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        consumer.setDaemon(true);
        producer.setDaemon(true);

        consumer.start();
        producer.start();

        Thread.sleep(10 * 1000);

    }
}
