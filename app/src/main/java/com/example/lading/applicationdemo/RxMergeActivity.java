package com.example.lading.applicationdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class RxMergeActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mText;
    private Button mBtn;
    private TextView mEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout1);
        initView();
    }

    private void initView() {
        mText= (TextView) findViewById(R.id.text1);
        mEdit= (TextView) findViewById(R.id.edit1);
        mBtn= (Button) findViewById(R.id.button);
        mEdit.setText("两个任务并发进行，全部处理完毕之后在更新数据");
        mBtn.setOnClickListener(this);
        mText.setOnClickListener(this);
        mEdit.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.text1:
                break;
            case R.id.edit1:
                break;
            case R.id.button:
                if(mText.getText().toString().length()>0){
                    mText.setText("");
                }
                start();
                break;
        }
    }

    private void start() {
        Observable obs1=Observable.create(new Observable.OnSubscribe<String>(){

            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    Log.e("yin","第一个call所在线程："+Thread.currentThread().getName());
                    Thread.sleep(500);
                    subscriber.onNext(" aaa");
                    subscriber.onCompleted();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.newThread());//指定call方法执行的线程

        Observable obs2=Observable.create(new Observable.OnSubscribe<String>(){

            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    Log.e("yin","第二个call所在线程："+Thread.currentThread().getName());
                    Thread.sleep(1500);
                    subscriber.onNext("bbb");
                    subscriber.onCompleted();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.newThread());

        Observable.merge(obs1,obs2)
                .observeOn(AndroidSchedulers.mainThread())//(必须)切换线程到主线程，即onNext,onCompleted,onError执行的线程
                .subscribe(new Subscriber<String>() {
                    StringBuffer sb=new StringBuffer();
                    //合并完成onCompleted只执行一次
                    @Override
                    public void onCompleted() {
                        mText.append("两个任务都处理完毕！！\n");
                        mText.append("更新数据："+sb+"\n");
                        Log.e("yin","onCompleted所在线程："+Thread.currentThread().getName());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                    //该执行多少次就执行多少次
                    @Override
                    public void onNext(String s) {
                        Log.e("yin","onNext所在线程："+Thread.currentThread().getName());
                        sb.append( s+",");
                        mText.append("得到一个数据："+s+"\n");
                    }
                });
    }
}
