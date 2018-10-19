package com.lzm;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App
{
    public static void main( String[] args )
    {
        /***********************
//       一.每个demo方法都是一种用法示例demo1-4都是简单的建立起来观察者和被观察者,然后通过subscribe关联的例子
         二.
        *********************/
//        1.第一种通过Observable.create来创建被观察者,
// 从业务的角度来看,sender.subscribe(receiver);这句最后的绑定语法中肯定是模板式的用法,这个模板是会
// 调用被观察者sender的call方法,而sender的call方法为override,入参是观察者,call方法就是调用了观察者的3个方法
//        而3个方法onNext,onComplete,onError就是钩子函数
//        从这里看到被观察者和观察者都是独立的,知道最后通过subscribe方法来关联起来,关联后就能自动调用了被观察者的call方法来发送数据了
//        ok，有了Observable和Obsever，我们就可以随便玩了，任取一个已创建的Observable和Observer关联上，即形成一个RxJava的例子
        demo1();
//        2.简化了被观察者,直接通过被观察者的just就发送了数据出去
        demo2();
//       3.简化了被观察者,直接通过被观察者的from就发送了list数据出去
        demo3();
//        4.如果你不在意数据是否接收完或者是否出现错误，即不需要Observer的onCompleted()和onError()方法，
// 可使用Action1，subscribe()支持将Action1作为参数传入,RxJava将会调用它的call方法来接收数据
        demo4();
//        5.变通一下,可以把发送和接收放在一起来写,了解原理后,这些就简单了
        demo5();
//        6.其实和5是一样的用法,不过把被观察者用另外的方式来体现一下
        demo6();
//        被观察者发送出去给观察者的数据可以被截胡的,他们中间先通过flatMap(new Func1(上个被观察者的返回对象,加工后需要传递出去被观察者对象)
        demo7();
//        可以对每个发出来的被观察最者数据进行判断,通过filter来过滤,判断的结果是true或false,如果false,则不继续往下传递了
        demo8();
//      和demo7对比的话,map方法可以直接逐一转换类型,将转换后的类型传递到下个节点
        demo9();
//        改一下通过对象来传递试试
        demo10();
    }

    private static void demo1(){
        //        发送源
        Observable<String> sender = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("Hi，Weavey---1！");  //发送数据"Hi，Weavey！"
                subscriber.onNext("Hi，Weavey---2！");  //发送数据"Hi，Weavey！"
                subscriber.onCompleted();
            }
        });
        //接收源
        Observer<String> receiver = new Observer<String>() {
            @Override
            public void onCompleted() {
                //数据接收完成时调用
                System.out.println("调用完成");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("调用出错");
                //发生错误调用
            }

            @Override
            public void onNext(String s) {
                //正常接收数据调用
                System.out.println("接收者demo1:"+s);  //将接收到来自sender的问候"Hi，Weavey！"
            }
        };
//        绑定
        Subscription subscription = sender.subscribe(receiver);
        subscription.unsubscribe();
    }

    private static void demo2(){
        Observable<String> sender = Observable.just("发送的数据1","发送的数据2");
        Observer<String> receiver = new Observer<String>() {
            @Override
            public void onCompleted() {
                //数据接收完成时调用
                System.out.println("调用完成");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("调用出错");
                //发生错误调用
            }

            @Override
            public void onNext(String s) {
                //正常接收数据调用
                System.out.println("接收者demo2:"+s);  //将接收到来自sender的问候"Hi，Weavey！"
            }
        };
//        绑定
        Subscription subscription = sender.subscribe(receiver);
        subscription.unsubscribe();

    }

    private static void demo3(){
        List<String> data = new ArrayList();
        data.add("发送的数据1");
        data.add("发送的数据2");
        Observable<String> sender = Observable.from(data);

        Observer<String> receiver = new Observer<String>() {
            @Override
            public void onCompleted() {
                //数据接收完成时调用
                System.out.println("调用完成");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("调用出错");
                //发生错误调用
            }

            @Override
            public void onNext(String s) {
                //正常接收数据调用
                System.out.println("接收者demo3:"+s);  //将接收到来自sender的问候"Hi，Weavey！"
            }
        };
//        绑定
        Subscription subscription = sender.subscribe(receiver);
        subscription.unsubscribe();
    }

    private static void demo4(){
//        被观察者
        Observable<String> sender = Observable.just("data1","data2");
//        事件绑定,这里就直接通过Action来接收数据了,前提是不在意是否接收成功或失败
//        代替了其他demo中的定义接收者observer,然后进行绑定的操作,进一步简化了
        sender.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println("demo4:" + s);
            }
        });

    }

    private static void demo5(){
//        Observable<String>
        List<String> data = new ArrayList<>();
        data.add("数据1");
        data.add("数据2");
        data.add("数据3");
        data.add("数据4");

        Observable.just(data).subscribe(new Action1<List<String>>() {
            @Override
            public void call(List<String> strings) {
                System.out.println("demo5:" + strings);
            }
        });
    }
    private static void demo6(){
        Observable.create(new Observable.OnSubscribe<List>() {
            @Override
            public void call(Subscriber<? super List> subscriber) {
                List<String> data = new ArrayList<>();
                data.add("数据1");
                data.add("数据2");
                data.add("数据3");
                data.add("数据4");
                subscriber.onNext(data);
            }
        }).subscribe(new Action1<List>() {
            @Override
            public void call(List list) {
                System.out.println("demo6:" + list);
            }
        });
    }
    private static void demo7(){
        Observable.just("这个是第一次发送的字符串").flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String s) {
                String rtn = "这个是第二次发送的字符串:" + s;
//                以下两种方式都可以的,就是新生成一个被观察者然后返回该观察者对象即可,回传的可以是任意对象,泛型中指定即可
//                当然也可以通过Observable.from(数组)来依次返回被观察者
//                Observable<String> observable = Observable.just(rtn);
                return Observable.just(rtn);
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println("demo7:" + s);
            }
        });
    }
    private static void demo8(){
        Observable.just("数据1","数据2","数据2","数据4").filter(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                if (s.equals("数据2")){   //只是允许数据2往下传递
                    return true;
                }else{
                    return false;
                }
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println("demo8:" +s );
            }
        });
    }
    private static void demo9(){
        Observable.just("data1","data2").map(new Func1<String, Map<String,String>>() {
            @Override
            public Map<String, String> call(String s) {
                Map<String,String> map = new HashMap<String, String>();
                map.put("key",s);
                return map;
            }
        }).subscribe(new Action1<Map<String, String>>() {
            @Override
            public void call(Map<String, String> stringStringMap) {
                System.out.println("demo9:" + stringStringMap);
            }
        });
    }
    private static void demo10(){
        User user = new User("001","lzm");
        User user2 = new User("002","lwf");

        Observable.just(user,user2).subscribe(new Action1<User>() {
            @Override
            public void call(User user) {
                System.out.println("demo10:"+ user);
            }
        });
    }

}

class User{
    private String userNo;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public User(String userNo, String userName) {
        this.userNo = userNo;
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", userNo='" + userNo + '\'' +
                '}';
    }
}
