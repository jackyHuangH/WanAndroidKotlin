package com.jacky.wanandroidkotlin.factorymethod.order;

/**
 * @author:Hzj
 * @date :2021/5/8
 * desc  ：
 * record：
 */
public class PizzaStore {

    public static void main(String[] args) {
        //使用工厂方法模式
        PizzaOrder bjorder=new BJPizzaOrder();
        PizzaOrder whorder=new WHPizzaOrder();
    }
}
