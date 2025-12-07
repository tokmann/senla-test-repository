package task1;

public class Main {

    private static final Object lock1 = new Object();
    private static final Object lock2 = new Object();

    public static void main(String[] args) throws InterruptedException {
         Thread thread = new Thread(() -> {

             try {
                 // RUNNABLE
                 System.out.println("[Новый поток] Начинаю работу...");

                 synchronized (lock1) {
                     // WAITING
                     System.out.println("[Новый поток] Вызываю lock1.wait()...");
                     lock1.wait();

                     // TIMED_WAITING
                     System.out.println("[Новый поток] Вызываю Thread.sleep(2000)...");
                     Thread.sleep(2000);

                     // BLOCKED
                     System.out.println("[Новый поток] Пытаюсь взять lock2...");
                 }

                 synchronized (lock2) {
                     System.out.println("[Новый Поток] Получил lock2");
                 }

                 System.out.println("[Новый поток] Завершаю работу...");

             } catch (InterruptedException e) {
                 System.out.println("Ошибка: " + e);
             }
         });

         thread.setName("[Новый поток]");

         // NEW
         System.out.println(thread.getName() + ": " + thread.getState());

         // RUNNABLE
         thread.start();
         System.out.println(thread.getName() + ": " + thread.getState());

         // WAITING
         Thread.sleep(200);
         System.out.println(thread.getName() + ": " + thread.getState());

         synchronized (lock2) {

             synchronized (lock1) {
                 lock1.notify();
             }

             // TIMED_WAITING
             Thread.sleep(300);
             System.out.println(thread.getName() + ": " + thread.getState());

             // BLOCKED
             Thread.sleep(2200);
             System.out.println(thread.getName() + ": " + thread.getState());

         }

         // TERMINATED
         thread.join();
         System.out.println(thread.getName() + ": " + thread.getState());
    }
}
