package com.jacky.wanandroidkotlin.absfactory.order;

import com.jacky.wanandroidkotlin.absfactory.Pizza;
import com.jacky.wanandroidkotlin.absfactory.WHApplePizza;
import com.jacky.wanandroidkotlin.absfactory.WHOrangePizza;

/**
 * @author:Hzj
 * @date :2021/5/8
 * desc  ：
 * record：
 */
public class WHFactory implements AbsFactory{

    @Override
    public Pizza createPizza(String type) {
        Pizza pizza = null;
        if ("apple".equalsIgnoreCase(type)) {
            pizza = new WHApplePizza();
        } else if ("orange".equalsIgnoreCase(type)) {
            pizza = new WHOrangePizza();
        }
        return pizza;
    }
}
