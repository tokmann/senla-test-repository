package task_3_3.carDetails;

import task_3_3.interfaces.IProductPart;

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
