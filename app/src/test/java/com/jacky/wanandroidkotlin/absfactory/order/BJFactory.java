package com.jacky.wanandroidkotlin.absfactory.order;

import com.jacky.wanandroidkotlin.absfactory.BJApplePizza;
import com.jacky.wanandroidkotlin.absfactory.BJOrangePizza;
import com.jacky.wanandroidkotlin.absfactory.Pizza;

/**
 * @author:Hzj
 * @date :2021/5/8
 * desc  ：
 * record：
 */
public class BJFactory implements AbsFactory{

    @Override
    public Pizza createPizza(String type) {
        Pizza pizza = null;
        if ("apple".equalsIgnoreCase(type)) {
            pizza = new BJApplePizza();
        } else if ("orange".equalsIgnoreCase(type)) {
            pizza = new BJOrangePizza();
        }
        return pizza;
    }
}
