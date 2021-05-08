package com.jacky.wanandroidkotlin.simplefactory.order;

import com.jacky.wanandroidkotlin.simplefactory.SimpleFactory;

/**
 * @author:Hzj
 * @date :2021/5/8
 * desc  ：
 * record：
 */
public class PizzaStore {

    public static void main(String[] args) {
//        new PizzaOrder();
        //使用简单工厂模式
        new PizzaOrder().setSimpleFactory(new SimpleFactory());
    }
}
