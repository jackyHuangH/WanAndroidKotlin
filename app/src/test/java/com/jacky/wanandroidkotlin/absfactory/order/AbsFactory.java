package com.jacky.wanandroidkotlin.absfactory.order;

import com.jacky.wanandroidkotlin.absfactory.Pizza;

/**
 * @author:Hzj
 * @date :2021/5/8
 * desc  ：
 * record：
 */
interface AbsFactory {
    Pizza createPizza(String type);
}
