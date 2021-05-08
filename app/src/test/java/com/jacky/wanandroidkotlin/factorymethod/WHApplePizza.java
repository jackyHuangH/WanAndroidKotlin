package com.jacky.wanandroidkotlin.factorymethod;

/**
 * @author:Hzj
 * @date :2021/5/8
 * desc  ：
 * record：
 */
public class WHApplePizza extends Pizza{

    @Override
    public void prepare() {
        setName("武汉苹果派");
        System.out.println("武汉苹果派准备");
    }
}
