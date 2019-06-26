package com.jacky.wanandroidkotlin;

import org.junit.Test;

import java.util.Queue;

/**
 * @author:Hzj
 * @date :2019/6/26/026
 * desc  ：
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

        //死锁例子
        Thread t1 = new Thread(new DeadLock(true), "线程1");
        Thread t2 = new Thread(new DeadLock(false), "线程2");

        t1.start();
        t2.start();
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
