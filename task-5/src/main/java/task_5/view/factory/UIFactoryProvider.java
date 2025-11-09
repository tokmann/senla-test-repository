package task_5.view.factory;

public class UIFactoryProvider  {

    private static UIFactory instance;

    private UIFactoryProvider() {}

    public static UIFactory getInstance() {
        if (instance == null) {
            instance = new ConsoleUIFactory();
        }
        return instance;
    }
}
