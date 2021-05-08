package com.jacky.wanandroidkotlin.factorymethod.order;

import com.jacky.wanandroidkotlin.factorymethod.BJApplePizza;
import com.jacky.wanandroidkotlin.factorymethod.BJOrangePizza;
import com.jacky.wanandroidkotlin.factorymethod.Pizza;

/**
 * @author:Hzj
 * @date :2021/5/8
 * desc  ：北京披萨订购
 * record：
 */
public class BJPizzaOrder extends PizzaOrder {

    @Override
    Pizza createPizza(String type) {
        Pizza pizza = null;
        if ("apple".equalsIgnoreCase(type)) {
            pizza = new BJApplePizza();
        } else if ("orange".equalsIgnoreCase(type)) {
            pizza = new BJOrangePizza();
        }
        return pizza;
    }
}
