package com.jacky.wanandroidkotlin.simplefactory.order;

import com.jacky.wanandroidkotlin.simplefactory.Pizza;
import com.jacky.wanandroidkotlin.simplefactory.SimpleFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author:Hzj
 * @date :2021/5/8
 * desc  ：订购披萨
 * record：
 */
public class PizzaOrder {
//    public PizzaOrder() {
//        Pizza pizza = null;
//        do {
//            String name = getType();
//            if (name.equalsIgnoreCase("Apple")) {
//                pizza = new ApplePizza();
//                pizza.setName("苹果派");
//            } else if (name.equalsIgnoreCase("banana")) {
//                pizza = new BananaPizza();
//                pizza.setName("香蕉派");
//            } else {
//                break;
//            }
//            pizza.prepare();
//            pizza.bake();
//            pizza.cut();
//            pizza.box();
//        } while (true);
//    }

    ////使用简单工厂模式创建对象
    private SimpleFactory simpleFactory;

    public void setSimpleFactory(SimpleFactory simpleFactory) {
        this.simpleFactory = simpleFactory;
        Pizza pizza = null;
        do {
            String name = getType();
            pizza = this.simpleFactory.createPizza(name);
            if (pizza != null) {
                pizza.prepare();
                pizza.bake();
                pizza.cut();
                pizza.box();
            } else {
                System.out.println("暂时没有这种类型的披萨哦");
            }
        } while (true);
    }

    private String getType() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("please input pizza type:");
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
