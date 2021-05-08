package com.jacky.wanandroidkotlin.factorymethod;

/**
 * @author:Hzj
 * @date :2021/5/8
 * desc  ：
 * record：
 */
public class BJOrangePizza extends Pizza{

    @Override
    public void prepare() {
        setName("北京橘子派");
        System.out.println("北京橘子派准备");
    }
}
