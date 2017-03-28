package com.dev.think.appdemotwo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 背压是指在异步场景中，被观察者发送事件速度远快于观察者的处理速度的情况下，一种告诉上游的被观察者降低发送速度的策略
 * 简而言之，背压是流速控制的一种策略
 *
 * Observable分为cold Observable和hot Observable，其中cold Observable在观察者订阅之后才会发送事件；而hot Observable在被观察者创建之后就会发送事件；
 * hot Observable不支持backpressure策略，cold Obervable中也有一些不支持backPressure策略（interval、timer等操作符创建的Obervable）；
 * 对于那些不支持背压策略的操作符中使用响应式拉取数据的话，还是会抛出MissingBackpressureException
 */

public class BackPressureActivity extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backpressure_layout);
        findViewById(R.id.bt_no_back_pressure).setOnClickListener(this);
        findViewById(R.id.bt_back_pressure_method).setOnClickListener(this);
        findViewById(R.id.bt_filter).setOnClickListener(this);
        findViewById(R.id.bt_buffer).setOnClickListener(this);
        findViewById(R.id.bt_buffer2).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.bt_no_back_pressure){
            testNoBackpressure();
        }
        if(v.getId() == R.id.bt_back_pressure_method){
            testBackpressure();
        }
        if(v.getId() == R.id.bt_filter){
            testSampleBackpressure();
        }
        if(v.getId() == R.id.bt_buffer){
            testBuffer();
        }
        if(v.getId() == R.id.bt_buffer2){
            testOnBackpressureDrop();
        }
    }

    /**
     * 被观察者产生时间的速度大于观察者处理事件的速度，导致rx.exceptions.MissingBackpressureException
     */
    private void testNoBackpressure() {
        Observable.interval(1, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e("yin", "结果：" + aLong);
                    }
                }
                        /*
                        , new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("yin","出错："+throwable.toString());//rx.exceptions.MissingBackpressureException
                    }
                }
                */
                );
    }

    /**
     * 响应式拉取:观察者主动从被观察者那里去拉取数据，而被观察者变成被动的等待通知再发送数据。
     * 实际上本代买中，即使你不使用request()方法拉取数据，这段代码仍然能完美运行不会出现MissingBackpressureException，这是
     * 因为observeOn这个操作符内部有一个缓冲区，Android环境下长度是16，它会告诉range最多发送16个事件，充满缓冲区即可。
     *
     * 这段代码的目的是演示怎么使用响应式拉取，主要是request()的使用
     */
    private void testBackpressure() {
        Observable observable = Observable.range(1,10000);
        observable.observeOn(Schedulers.newThread())
                .subscribe(new MySubscriber());

    }
    class MySubscriber extends Subscriber<Integer>{
        @Override
        public void onStart() {
            super.onStart();
            request(1);//先从被观察者那里去一个数据
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(Integer integer) {
            Log.e("yin","处理开始："+integer);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.e("yin","处理结束："+integer);
            request(1);//处理完成后再取一个
        }
    }

    /**
     * 流速控制的操作符：过滤
     * 就是虽然生产者产生事件的速度很快，但是把大部分的事件都直接过滤（浪费）掉，从而间接的降低事件发送的速度。
     */

    private void testSampleBackpressure() {
        Observable.interval(1,TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.newThread())
                .sample(1000,TimeUnit.MILLISECONDS)//这个操作符简单理解就是每隔1000ms发送里时间点最近那个事件，其它的事件浪费掉
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e("yin","处理："+aLong);
                    }
                });
    }

    private void testBuffer() {
        Observable.interval(1,TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.newThread())
                .buffer(2000,TimeUnit.MILLISECONDS)//这个操作符简单理解就是把2000毫秒内的事件打包成list发送
                .subscribe(new Action1<List<Long>>() {
                    @Override
                    public void call(List<Long> aLong) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e("yin","buffer:"+aLong.size());
                    }
                });
    }

    /**
     * 03-24 19:09:00.216 23669-23669/com.example.lading.applicationdemo E/yin: start
     03-24 19:09:00.225 23669-25852/com.example.lading.applicationdemo E/yin: ---->0
     03-24 19:09:00.325 23669-25852/com.example.lading.applicationdemo E/yin: ---->1
     03-24 19:09:00.425 23669-25852/com.example.lading.applicationdemo E/yin: ---->2
     03-24 19:09:00.526 23669-25852/com.example.lading.applicationdemo E/yin: ---->3
     03-24 19:09:00.626 23669-25852/com.example.lading.applicationdemo E/yin: ---->4
     03-24 19:09:00.727 23669-25852/com.example.lading.applicationdemo E/yin: ---->5
     03-24 19:09:00.827 23669-25852/com.example.lading.applicationdemo E/yin: ---->6
     03-24 19:09:00.927 23669-25852/com.example.lading.applicationdemo E/yin: ---->7
     03-24 19:09:01.027 23669-25852/com.example.lading.applicationdemo E/yin: ---->8
     03-24 19:09:01.127 23669-25852/com.example.lading.applicationdemo E/yin: ---->9
     03-24 19:09:01.228 23669-25852/com.example.lading.applicationdemo E/yin: ---->10
     03-24 19:09:01.329 23669-25852/com.example.lading.applicationdemo E/yin: ---->11
     03-24 19:09:01.429 23669-25852/com.example.lading.applicationdemo E/yin: ---->12
     03-24 19:09:01.534 23669-25852/com.example.lading.applicationdemo E/yin: ---->13
     03-24 19:09:01.634 23669-25852/com.example.lading.applicationdemo E/yin: ---->14
     03-24 19:09:01.734 23669-25852/com.example.lading.applicationdemo E/yin: ---->15
     03-24 19:09:01.835 23669-25852/com.example.lading.applicationdemo E/yin: ---->1209
     03-24 19:09:01.935 23669-25852/com.example.lading.applicationdemo E/yin: ---->1210
     03-24 19:09:02.035 23669-25852/com.example.lading.applicationdemo E/yin: ---->1211
     03-24 19:09:02.135 23669-25852/com.example.lading.applicationdemo E/yin: ---->1212
     03-24 19:09:02.236 23669-25852/com.example.lading.applicationdemo E/yin: ---->1213

     之所以出现0-15这样连贯的数据，就是是因为observeOn操作符内部有一个长度为16的缓存区，它会首先请求16个事件缓存起来.

     */
    private void testOnBackpressureDrop() {
        Observable.interval(1, TimeUnit.MILLISECONDS)
                .onBackpressureDrop()
//                .onBackpressureBuffer()把observable发送出来的事件做缓存，当request方法被调用的时候，给下层流发送一个item(如果给这个缓存区设置了大小，那么超过了这个大小就会抛出异常)
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Long>() {

                    @Override
                    public void onStart() {
                        Log.e("yin","start");
//                        request(1);
                    }

                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.e("yin",e.toString());
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e("yin","---->"+aLong);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
