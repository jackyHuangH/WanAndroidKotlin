package com.jacky.wanandroidkotlin.designmode

//产品功能接口
interface Sender {
    fun send()
}

//发邮件功能
class MailSender : Sender {
    override fun send() {
        println("this is a mail sender")
    }
}

//发短信功能
class MsmSender : Sender {
    override fun send() {
        println("this is a msm sender")
    }
}

/**
 * @author:Hzj
 * @date  :2022/2/23
 * desc  ：简单工厂模式
 * record：
 */
class SenderFactory {
    fun produce(type: String): Sender? {
        return when (type) {
            "msm" -> {
                MsmSender()
            }
            "mail" -> {
                MailSender()
            }
            else -> {
                println("wrong type!")
                null
            }
        }
    }
}

/**
 * @author:Hzj
 * @date  :2022/2/23
 * desc  ：工厂方法模式,将工厂方法变成静态方法，就变成 静态工厂方法模式
 * record：需要扩展时，会破坏此类，不符合开闭原则，因此引申出抽象工厂模式
 */
class SenderFactoryMethod {
    //具体化生产方法
    fun produceMsm(): Sender {
        return MsmSender()
    }

    companion object {
        //静态工厂方法
        fun produceMail(): Sender {
            return MailSender()
        }
    }
}


/**
 * @author:Hzj
 * @date  :2022/2/23
 * desc  ：抽象工厂模式,将工厂方法抽象化，需要扩展工厂方法时，只需要添加对应工厂实现类即可，避免对工厂类破坏，开闭原则
 * record：
 */
interface Factory{
    fun createSender():Sender
}

class MailFactory:Factory{
    override fun createSender(): Sender {
        return MailSender()
    }
}

class MsmFactory:Factory{
    override fun createSender(): Sender {
        return MsmSender()
    }
}

class MusicSender:Sender{
    override fun send() {
        println("send a song")
    }
}

//扩展一个工厂类
class MusicFactory:Factory{
    override fun createSender(): Sender {
        return MusicSender()
    }
}

fun main() {
    //简单工厂模式测试
//    val factory=SenderFactory()
//    val msmSender = factory.produce("msm")
//    val mailSender = factory.produce("mail")
//    val unknownSender = factory.produce("999")
//    msmSender?.send()
//    mailSender?.send()
//    unknownSender?.send()

    //工厂方法模式测试
//    val factory=SenderFactoryMethod()
//    val msmSender = factory.produceMsm()
//    val mailSender = SenderFactoryMethod.produceMail()
//    msmSender.send()
//    mailSender.send()

    //抽象工厂模式
    val factory=MsmFactory()
    val msmSender = factory.createSender()
    msmSender.send()
    val factory2=MailFactory()
    factory2.createSender().send()
    val factory3=MusicFactory()
    factory3.createSender().send()
}