package com.jacky.wanandroidkotlin;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * @author:Hzj
 * @date :2019/6/26/026
 * desc  ：Thread.stop()强制停止线程，不推荐
 * Thread.interrupt()设置停止标记，配合isInterrupted()在runnable中使用
 * record：
 */
public class ThreadTest {

    @Test
    public void test() {
//        final Queue<Integer> sharedQueue = new LinkedList();
//        Thread producer = new Producer(sharedQueue);
//        Thread consumer = new Consumer(sharedQueue);
//        producer.start();
//        consumer.start();

        //死锁例子，2个线程互相持有对方的对象锁，无法释放
       /* Thread t1 = new Thread(new DeadLock(true), "线程1");
        Thread t2 = new Thread(new DeadLock(false), "线程2");

        t1.start();
        t2.start();*/

        //设置当前线程为终止状态，不管之前是否已终止
//        Thread.interrupted();


        //测试高CPU占用线程
        List<HighCpu> highCpuList = new ArrayList<>();
        Thread highCpuThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int a = 0;
                while (true) {
                    highCpuList.add(new HighCpu("Java日志", a));
                    System.out.println("high cpu size:" + highCpuList.size());
                    a++;
                }
            }
        });
        highCpuThread.setName("HighCPU");
        highCpuThread.start();
    }

    class HighCpu {
        private String name;
        private int age;

        public HighCpu(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    class Producer extends Thread {

        private static final int MAX_QUEUE_SIZE = 5;
        private final Queue sharedQueue;

        public Producer(Queue sharedQueue) {
            super();
            this.sharedQueue = sharedQueue;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                synchronized (sharedQueue) {
                    while (sharedQueue.size() >= MAX_QUEUE_SIZE) {
                        System.out.println("队列满了，等待消费");
                        try {
                            sharedQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    sharedQueue.add(i);
                    System.out.println("进行生产 : " + i);
                    sharedQueue.notify();
                }
            }
        }
    }

    class Consumer extends Thread {
        private final Queue sharedQueue;

        public Consumer(Queue sharedQueue) {
            super();
            this.sharedQueue = sharedQueue;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (sharedQueue) {
                    while (sharedQueue.size() == 0) {
                        try {
                            System.out.println("队列空了，等待生产");
                            sharedQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    int number = (int) sharedQueue.poll();
                    System.out.println("进行消费 : " + number);
                    sharedQueue.notify();
                }
            }
        }
    }

}
