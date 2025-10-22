package task_3_1;

import java.util.Arrays;
import java.util.Random;

public class RandomGenerator {
    public static void main(String[] args) {
        int num = new Random().nextInt(100, 1000);
        String strNum = String.valueOf(num);
        int maxDigit = Arrays.stream(strNum.split(""))
                .map(Integer::parseInt)
                .max(Integer::compare)
                .orElse(-1);
        System.out.println("Number: " + num);
        System.out.println("Max digit: " + maxDigit);
    }
}
