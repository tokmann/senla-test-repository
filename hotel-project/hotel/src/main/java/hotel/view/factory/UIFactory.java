package hotel.view.factory;

import hotel.view.ConsoleView;

/**
 * Интерфейс фабрики пользовательского интерфейса.
 * Определяет контракт для создания конкретной реализации UI.
 */
public interface UIFactory {
    ConsoleView createConsoleView();
}
