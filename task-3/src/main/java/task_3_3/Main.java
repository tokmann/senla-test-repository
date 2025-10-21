package task_3_3;

import task_3_3.CarBuilders.CarBodyInstaller;
import task_3_3.CarBuilders.ChassisInstaller;
import task_3_3.CarBuilders.EngineInstaller;
import task_3_3.Interfaces.IAssemblyLine;
import task_3_3.Interfaces.ILineStep;
import task_3_3.Interfaces.IProduct;

public class Main {
    public static void main(String[] args) {
        ILineStep bodySpecialist = new CarBodyInstaller("Кузов седан");
        ILineStep chassisSpecialist = new ChassisInstaller("Рамное шасси");
        ILineStep engineSpecialist = new EngineInstaller("Двигатель V8");
        IAssemblyLine assemblyLine = new CarAssemblyLine(bodySpecialist, chassisSpecialist, engineSpecialist);

        IProduct car = new Car();
        IProduct finishedCar = assemblyLine.assembleProduct(car);

        ((Car) finishedCar).showCar();
    }
}
