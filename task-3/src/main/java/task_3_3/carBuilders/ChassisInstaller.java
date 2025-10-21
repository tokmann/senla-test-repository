package task_3_3.carBuilders;

import task_3_3.carDetails.Chassis;
import task_3_3.interfaces.ILineStep;
import task_3_3.interfaces.IProductPart;

public class ChassisInstaller implements ILineStep {

    private final String chassisType;

    public ChassisInstaller(String chassisType) {
        this.chassisType = chassisType;
    }

    @Override
    public IProductPart buildProductPart() {
        System.out.println(">>> Происходит создание и установка шасси автомобиля...");
        return new Chassis(chassisType);
    }
}
