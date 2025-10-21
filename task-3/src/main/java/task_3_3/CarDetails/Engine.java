package task_3_3.CarDetails;

import task_3_3.Interfaces.IProductPart;

public class Engine implements IProductPart {

    private final String engineModel;

    public Engine(String engineModel) {
        this.engineModel = engineModel;
        System.out.println("Создан двигатель модели: " + engineModel);
    }

    public String getModel() {
        return engineModel;
    }
}
