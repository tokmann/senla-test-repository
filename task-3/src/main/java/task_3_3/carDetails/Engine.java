package task_3_3.carDetails;

import task_3_3.interfaces.IProductPart;

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
