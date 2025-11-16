package task_6.view.factory;

/**
 * Провайдер для получения единственного экземпляра фабрики UI.
 * Реализует паттерн Singleton.
 * Позволяет централизованно управлять типом интерфейса без изменения бизнес-логики.
 */
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
