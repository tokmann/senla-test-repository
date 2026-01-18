package hotel.view.factory;

import hotel.view.ConsoleView;

/**
 * Реализация фабрики UI для консольного интерфейса.
 * Отвечает за создание экземпляра {@link ConsoleView}.
 */
public class ConsoleUIFactory implements UIFactory {

    @Override
    public ConsoleView createConsoleView() {
        return new ConsoleView();
    }
}
