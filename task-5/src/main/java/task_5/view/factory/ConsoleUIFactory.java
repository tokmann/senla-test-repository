package task_5.view.factory;

import task_5.view.ConsoleView;

public class ConsoleUIFactory implements UIFactory {

    @Override
    public ConsoleView createConsoleView() {
        return new ConsoleView();
    }
}
