package task_7.view.factory;

import task_7.view.ConsoleView;

/**
 * Интерфейс фабрики пользовательского интерфейса.
 * Определяет контракт для создания конкретной реализации UI.
 */
public interface UIFactory {
    ConsoleView createConsoleView();
}
