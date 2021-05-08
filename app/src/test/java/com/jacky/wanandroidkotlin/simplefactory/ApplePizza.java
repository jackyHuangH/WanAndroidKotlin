package com.jacky.wanandroidkotlin.simplefactory;

/**
 * @author:Hzj
 * @date :2021/5/8
 * desc  ：
 * record：
 */
public class ApplePizza extends Pizza {

    @Override
    public void prepare() {
        System.out.println("给苹果披萨准备原材料");
    }
}
