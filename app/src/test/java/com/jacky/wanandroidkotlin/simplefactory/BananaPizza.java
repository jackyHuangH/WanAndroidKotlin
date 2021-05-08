package com.jacky.wanandroidkotlin.simplefactory;

/**
 * @author:Hzj
 * @date :2021/5/8
 * desc  ：
 * record：
 */
public class BananaPizza extends Pizza{

    @Override
    public void prepare() {
        System.out.println("给香蕉披萨准备原材料");
    }
}
