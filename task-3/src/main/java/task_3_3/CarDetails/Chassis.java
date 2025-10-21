package task_3_3.CarDetails;

import task_3_3.Interfaces.IProductPart;

public class Chassis implements IProductPart {

    private final String chassisType;

    public Chassis(String chassisType) {
        this.chassisType = chassisType;
        System.out.println("Создано шасси, тип: " + chassisType);
    }

    public String getChassisType() {
        return this.chassisType;
    }
}
