package task_3_3;

import task_3_3.interfaces.IAssemblyLine;
import task_3_3.interfaces.ILineStep;
import task_3_3.interfaces.IProduct;
import task_3_3.interfaces.IProductPart;

public class CarAssemblyLine implements IAssemblyLine {

    private final ILineStep firstStep;
    private final ILineStep secondStep;
    private final ILineStep thirdStep;

    public CarAssemblyLine(ILineStep bodyStep, ILineStep chassisStep, ILineStep engineStep) {
        this.firstStep = bodyStep;
        this.secondStep = chassisStep;
        this.thirdStep = engineStep;
    }

    @Override
    public IProduct assembleProduct(IProduct product) {
        System.out.println("=== НАЧАЛО СБОРКИ АВТОМОБИЛЯ ===");

        System.out.println("1: Установка кузова");
        IProductPart body = firstStep.buildProductPart();
        product.installFirstPart(body);

        System.out.println("\n2: Установка шасси");
        IProductPart chassis = secondStep.buildProductPart();
        product.installSecondPart(chassis);

        System.out.println("\n3: Установка двигателя");
        IProductPart engine = thirdStep.buildProductPart();
        product.installThirdPart(engine);

        return product;
    }
}
