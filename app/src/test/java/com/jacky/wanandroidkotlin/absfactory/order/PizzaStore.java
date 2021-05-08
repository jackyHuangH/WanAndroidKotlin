package com.jacky.wanandroidkotlin.absfactory.order;

/**
 * @author:Hzj
 * @date :2021/5/8
 * desc  ：
 * record：
 */
public class PizzaStore {
    public static void main(String[] args) {
        BJFactory bjFactory = new BJFactory();
        WHFactory whFactory = new WHFactory();
        new PizzaOrder().setFactory(bjFactory);
    }
}
