package com.jacky.wanandroidkotlin.simplefactory;

/**
 * @author:Hzj
 * @date :2021/5/8
 * desc  ：工厂模式练习
 * record：
 */
public abstract class Pizza {

    protected String name;

    public void setName(String name) {
        this.name = name;
    }

    public abstract void prepare();

    public void bake() {
        System.out.println(name + "is bake");
    }

    public void cut() {
        System.out.println(name + "is cut");
    }

    public void box() {
        System.out.println(name + "is box");
    }
}
