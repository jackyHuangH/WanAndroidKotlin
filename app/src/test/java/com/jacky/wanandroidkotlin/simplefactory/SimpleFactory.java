package com.jacky.wanandroidkotlin.simplefactory;

/**
 * @author:Hzj
 * @date :2021/5/8
 * desc  ：简单工厂模式,加减新的pizza只需要修改此工厂类即可
 * record：
 */
public class SimpleFactory {

    /**
     * 根据pizzaType提供对应pizza对象实例,也可以改成static静态方法更方便
     * @param pizzaType
     * @return
     */
    public Pizza createPizza(String pizzaType){
        System.out.println("使用简单工厂模式");
        Pizza pizza=null;
        if (pizzaType.equalsIgnoreCase("Apple")) {
            pizza = new ApplePizza();
            pizza.setName("苹果派");
        } else if (pizzaType.equalsIgnoreCase("banana")) {
            pizza = new BananaPizza();
            pizza.setName("香蕉派");
        }
        return pizza;
    }

    /**
     * 工厂方法模式，将对象的实例化推迟到子类实现
     * 根据pizzaType提供对应pizza对象实例
     * @param pizzaType
     * @return
     */
    public Pizza createPizzaFactoryMethod(String pizzaType){
        System.out.println("使用简单工厂模式");
        Pizza pizza=null;
        if (pizzaType.equalsIgnoreCase("Apple")) {
            pizza = new ApplePizza();
            pizza.setName("苹果派");
        } else if (pizzaType.equalsIgnoreCase("banana")) {
            pizza = new BananaPizza();
            pizza.setName("香蕉派");
        }
        return pizza;
    }

}
