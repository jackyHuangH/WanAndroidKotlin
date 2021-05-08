package com.jacky.wanandroidkotlin.factorymethod.order;

import com.jacky.wanandroidkotlin.factorymethod.Pizza;
import com.jacky.wanandroidkotlin.factorymethod.WHApplePizza;
import com.jacky.wanandroidkotlin.factorymethod.WHOrangePizza;

/**
 * @author:Hzj
 * @date :2021/5/8
 * desc  ：北京披萨订购
 * record：
 */
public class WHPizzaOrder extends PizzaOrder {

    @Override
    Pizza createPizza(String type) {
        Pizza pizza = null;
        if ("apple".equalsIgnoreCase(type)) {
            pizza = new WHApplePizza();
        } else if ("orange".equalsIgnoreCase(type)) {
            pizza = new WHOrangePizza();
        }
        return pizza;
    }
}
