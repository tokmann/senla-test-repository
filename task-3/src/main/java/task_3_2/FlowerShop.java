package task_3_2;

import task_3_2.flowerTypes.Chrysanthemum;
import task_3_2.flowerTypes.Lily;
import task_3_2.flowerTypes.Rose;
import task_3_2.flowerTypes.Tulip;

import java.util.Arrays;
import java.util.Comparator;

public class FlowerShop {
    public static void main(String[] args) {
        Rose redRose = new Rose("красный", 150.0, true);
        Rose whiteRose = new Rose("белый", 140.0, false);
        Tulip yellowTulip = new Tulip("желтый", 80.0, "в крапинку");
        Tulip stripedTulip = new Tulip("розовый", 95.0, "полосатый");
        Lily whiteLily = new Lily("белый", 200.0, 6);
        Chrysanthemum largeChrysanthemum = new Chrysanthemum("фиолетовый", 120.0, true);

        Bouquet romanticBouquet = new Bouquet("Романтический букет");
        romanticBouquet.addFlower(redRose);
        romanticBouquet.addFlower(redRose);
        romanticBouquet.addFlower(whiteRose);
        romanticBouquet.addFlower(whiteLily);
        Packaging ribbonPack = new Packaging("лента с бантиком", 100.0);
        romanticBouquet.setPackaging(ribbonPack);

        Bouquet springBouquet = new Bouquet("Весенний букет");
        springBouquet.addFlower(yellowTulip);
        springBouquet.addFlower(yellowTulip);
        springBouquet.addFlower(stripedTulip);
        springBouquet.addFlower(stripedTulip);
        springBouquet.addFlower(largeChrysanthemum);
        Packaging paperPack = new Packaging("бумага", 50.0);
        springBouquet.setPackaging(paperPack);

        Bouquet luxuryBouquet = new Bouquet("Дорогой букет");
        luxuryBouquet.addFlower(redRose);
        luxuryBouquet.addFlower(redRose);
        luxuryBouquet.addFlower(redRose);
        luxuryBouquet.addFlower(whiteLily);
        luxuryBouquet.addFlower(whiteLily);
        luxuryBouquet.addFlower(largeChrysanthemum);
        luxuryBouquet.addFlower(largeChrysanthemum);
        Packaging basketPack = new Packaging("корзина", 300.0);
        luxuryBouquet.setPackaging(basketPack);

        romanticBouquet.displayBouquetInfo();
        springBouquet.displayBouquetInfo();
        luxuryBouquet.displayBouquetInfo();
    }
}