package com.jacky.wanandroidkotlin;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author:Hzj
 * @date :2022/2/21
 * desc  ：
 * record：
 */
public class MyTest {

    @Test
    public void test() {
//        String s2="1";
//        String s1=new String("1");
//        String sR=s1.intern();
//
//        //s1是new对象，对象实例在heap中，s2直接存放在常量池，所以s1和s2的地址不同，sR是s1调用intern()方法后返回的字符串常量s2的引用，故sR==s2
//        System.out.println("s1==s2:"+(s1==s2));//false
//        System.out.println("sR==s2:"+(sR==s2));//true
//
//        String s3 = new String("1") + new String("1");
//        String s4 = "11";
//        s3.intern();
//
//        System.out.println(s3 == s4);

//        int age = getAge();
//        System.out.println("getAge：" + age);

        testLinkedHashMap();

        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private void testLinkedHashMap() {
        //accessOrder 表示是否开启访问排序，空参构造默认为false
        LinkedHashMap<Integer, Integer> map = new LinkedHashMap<>(16, 0.75F, true);
        for (int i = 0; i < 8; i++) {
            map.put(i, i);
        }

        System.out.println(map);

        map.get(1);
        map.get(2);

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }


    private int getAge() {
        int a = 90;
        //此处发生异常，try代码块都无法执行
//            a=a/0;
        try {
            System.out.println("try");
            a = a / 0;
            return a;
        } catch (Exception e) {
            System.out.println("catch:" + e.getMessage());
            return 9;
        } finally {
            System.out.println("finally");
            //不要在finally中return,finally的return会屏蔽上面的return语句
            return 0;
        }
//        return 0;
    }
}
