package com.jacky.wanandroidkotlin.absfactory;

/**
 * @author:Hzj
 * @date :2021/5/8
 * desc  ：
 * record：
 */
public class BJApplePizza extends Pizza {

    @Override
    public void prepare() {
        setName("北京苹果派");
        System.out.println("北京苹果派准备");
    }
}
