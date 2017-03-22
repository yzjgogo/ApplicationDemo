package com.example.lading.applicationdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 背压是指在异步场景中，被观察者发送事件速度远快于观察者的处理速度的情况下，一种告诉上游的被观察者降低发送速度的策略
 * 简而言之，背压是流速控制的一种策略
 */

public class BackPressureActivity extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backpressure_layout);
        findViewById(R.id.bt_no_back_pressure).setOnClickListener(this);
        findViewById(R.id.bt_back_pressure_method).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.bt_no_back_pressure){
            testNoBackpressure();
        }
        if(v.getId() == R.id.bt_back_pressure_method){
            testBackpressure();
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
}
