package com.example.lading.applicationdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button mBtn1;
    private Button mBtn2;
    private Button mBtn3;
    private Button mBtn4;
    private Button mBtn5;
    private Button mBtn6;
    private Button mBtn7;
    private Button mBtn8;
    private Button mBtn9;
    private Observable observable;
    private Subscriber subscriber;
    private Action1 subscriberAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();


        /**
         * 创建被观察者
         */
        //正常模式
        observable = Observable.create(new Observable.OnSubscribe<String>() {
            /**
             * 当观察者订阅被观察者时调用该方法
             * @param subscriber
             */
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Log.e("yin","call被调用");
                subscriber.onNext("On");
                subscriber.onNext("Off");
                subscriber.onNext("On");
                subscriber.onNext("On");
                subscriber.onCompleted();
            }
        });
        //偷懒模式1
//        observable=Observable.just("On","Off","On","On");
        //偷懒模式2
//        String [] kk={"On","Off","On","On"};
//        Observable observable=Observable.from(kk);
        /**
         * 创建观察者
         */
        //正常模式
        subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                //被观察者的onCompleted()事件会走到这里;
//                Log.d("DDDDDD","结束观察...\n");
                Log.e("yin", "onCompleted观察结束");
            }

            @Override
            public void onError(Throwable e) {
                //出现错误会调用这个方法
                Log.e("yin", "onError观察出错");
            }

            @Override
            public void onNext(String s) {
                //处理传过来的onNext事件
                Log.e("yin", "onNext接收：" + s);
            }
        };
        //偷懒模式:Action1是和Observer没有啥关系的接口，只不过它可以当做观察者来使，专门处理onNext 事件，
        // 这是一种为了简便偷懒的写法。当然还有Action0，Action2,Action3...,0,1,2,3分别表示call()这个方法能接受几个参数
        subscriberAction = new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e("yin", "handle this---" + s);
            }
        };
    }

    private void initView() {
        findViewById(R.id.button0).setOnClickListener(this);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        findViewById(R.id.button7).setOnClickListener(this);
        findViewById(R.id.button8).setOnClickListener(this);
        findViewById(R.id.button9).setOnClickListener(this);
        findViewById(R.id.button10).setOnClickListener(this);
        findViewById(R.id.button11).setOnClickListener(this);
        findViewById(R.id.button12).setOnClickListener(this);
        findViewById(R.id.button13).setOnClickListener(this);
//        mBtn1= (Button) findViewById(R.id.button);
//        mBtn1= (Button) findViewById(R.id.button);
//        mBtn1= (Button) findViewById(R.id.button);


    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.button0:
                observable.subscribe(subscriber);
//                observable.subscribe(subscriberAction);
//                startFilter();
//                startMap();
                startThread();
                break;
            case R.id.button1:
                intent = new Intent(MainActivity.this, NormalRxActivity.class);//学习简单的RXJAVA的流程
                break;
            case R.id.button2:
                intent = new Intent(MainActivity.this, RxMapActivity.class);//操作符--MAP
                break;
            case R.id.button3:
                intent = new Intent(MainActivity.this, RxSchuderActivity.class);//RXJAVA的线程调度
                break;
            case R.id.button4:
                intent = new Intent(MainActivity.this, RxFlatMapActivity.class);//操作符--FLATMAP
                break;
            case R.id.button5:
                intent = new Intent(MainActivity.this, RxMergeActivity.class);//操作符--合并
                break;
            case R.id.button6:
                intent = new Intent(MainActivity.this, RxBindingActivity.class);//基于RXJAVA的BINDING
                break;
            case R.id.button7:
                intent = new Intent(MainActivity.this, RxFilterActivity.class);//操作符--FILTER
                break;
            case R.id.button8:
                intent = new Intent(MainActivity.this, RxTakeActivity.class);//操作符--TAKE,DOONNEXT
                break;
            case R.id.button9:
                intent = new Intent(MainActivity.this, RxTimerActivity.class);//操作符--INTERVAL,取消订阅
                break;
            case R.id.button10:
                intent = new Intent(MainActivity.this, RxSortActivity.class);//操作符--TOSORTEDLIST
                break;
            case R.id.button11:
                intent = new Intent(MainActivity.this, RxConnetActivity.class);//操作符--CONNECT
                break;
            case R.id.button12:
                intent = new Intent(MainActivity.this, TimestampActivity.class);//操作符--
                break;
            case R.id.button13:
                intent = new Intent(MainActivity.this, BackPressureActivity.class);//背压
                break;
        }
        startActivity(intent);
    }

    /**
     * 过滤操作:filter
     */
    private void startFilter() {
//创建被观察者，是事件传递的起点
        Observable.just("On", "Off")
                //这就是在传递过程中对事件进行过滤操作
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        Log.e("yin", "call:" + s);
                        return s.equals("Off");
                    }
                })
                //实现订阅
                .subscribe(
                        //创建观察者，作为事件传递的终点处理事件
                        new Subscriber<String>() {
                            @Override
                            public void onCompleted() {
                                Log.e("yin", "onCompleted结束");
                            }

                            @Override
                            public void onError(Throwable e) {
                                //出现错误会调用这个方法
                                Log.e("yin", "onError出错");
                            }

                            @Override
                            public void onNext(String s) {
                                //处理事件
                                Log.e("yin", "onNext:" + s);
                            }
                        }
                );
    }

    /**
     * 转换操作:map
     */
    private void startMap() {
        Observable.just("9527")
                //使用map操作来完成类型转换
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        return Integer.valueOf(s);
                    }
                })
                .subscribe(
                        //创建观察者，作为事件传递的终点处理事件
                        new Subscriber<Integer>() {
                            @Override
                            public void onCompleted() {
                                Log.e("yin", "onCompleted");
                            }

                            @Override
                            public void onError(Throwable e) {
                                //出现错误会调用这个方法
                                Log.e("yin", "onError");
                            }

                            @Override
                            public void onNext(Integer integer) {
                                //处理事件,接收转换的结果
                                Log.e("yin", "转换的结果：" + integer);
                            }
                        }
                );
    }

    /**
     * 开启新的线程
     */
    private void startThread() {
        Observable.just("9528")
                //指定了被观察者执行的线程环境
                .subscribeOn(Schedulers.newThread())
                //将接下来执行的线程环境指定为io线程
                .observeOn(Schedulers.io())
                //使用map操作来完成类型转换
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        return Integer.valueOf(s);
                    }
                })
                //将后面执行的线程环境切换为主线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        //创建观察者，作为事件传递的终点处理事件
                        new Subscriber<Integer>() {
                            @Override
                            public void onCompleted() {
                                Log.e("yin", "onCompleted");
                            }

                            @Override
                            public void onError(Throwable e) {
                                //出现错误会调用这个方法
                                Log.e("yin","onError");
                            }

                            @Override
                            public void onNext(Integer integer) {
                                //处理事件
                                Log.e("yin","转换的结果："+integer);
                            }
                        }
                );
    }
}
