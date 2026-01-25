package hotel.view;

import di.Component;

/**
 * Консольное представление системы.
 * Отвечает за вывод информации пользователю и чтение ввода.
 * Является частью слоя представления в архитектуре MVC.
 * Все сообщения пользователю проходят через данный класс.
 */
@Component
public class ConsoleView {

    public void printWelcome() {
        System.out.println("Добро пожаловать в систему управления отелем!");
    }

    public void printGoodbye() {
        System.out.println("До свидания!");
    }

    public void printInvalidOption() {
        System.out.println("Неверный выбор, попробуйте снова.");
    }

    public void printError(String message) {
        System.out.println(message);
    }

    public void print(String message) {
        System.out.print(message);
    }

    public void println(String message) {
        System.out.println(message);
    }

    public <T> void println(T obj) {
        System.out.println(obj);
    }
}
