package task_8.view.factory;

import task_8.view.ConsoleView;

/**
 * Интерфейс фабрики пользовательского интерфейса.
 * Определяет контракт для создания конкретной реализации UI.
 */
public interface UIFactory {
    ConsoleView createConsoleView();
}
