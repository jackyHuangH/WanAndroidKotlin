package com.jacky.wanandroidkotlin.factorymethod.order;


import com.jacky.wanandroidkotlin.factorymethod.Pizza;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author:Hzj
 * @date :2021/5/8
 * desc  ：订购披萨
 * record：
 */
public abstract class PizzaOrder {

    ////使用工厂方法模式创建对象
    abstract Pizza createPizza(String type);

    public PizzaOrder() {
        Pizza pizza = null;
        do {
            String name = getType();
            pizza = createPizza(name);
            if (pizza != null) {
                pizza.prepare();
                pizza.bake();
                pizza.cut();
                pizza.box();
            } else {
                System.out.println("暂时没有这种类型的披萨哦");
                break;
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
