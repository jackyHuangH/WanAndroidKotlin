package com.jacky.wanandroidkotlin.factorymethod;

/**
 * @author:Hzj
 * @date :2021/5/8
 * desc  ：
 * record：
 */
public class WHOrangePizza extends Pizza{

    @Override
    public void prepare() {
        setName("武汉橘子派");
        System.out.println("武汉橘子派准备");
    }
}
