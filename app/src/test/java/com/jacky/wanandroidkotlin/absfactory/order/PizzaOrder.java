package com.jacky.wanandroidkotlin.absfactory.order;


import com.jacky.wanandroidkotlin.absfactory.Pizza;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author:Hzj
 * @date :2021/5/8
 * desc  ：
 * record：
 */
public class PizzaOrder {
   AbsFactory factory;

    public void setFactory(AbsFactory factory) {
        this.factory = factory;
        Pizza pizza = null;
        do {
            String name = getType();
            pizza = this.factory.createPizza(name);
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
