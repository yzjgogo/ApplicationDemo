package com.dev.think.appdemotwo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

//import io.reactivex.Observable;
//import io.reactivex.ObservableEmitter;
//import io.reactivex.ObservableOnSubscribe;
//import io.reactivex.Observer;
//import io.reactivex.disposables.Disposable;


/**
 * Created by think on 2017/3/28.
 */

public class RxRelease2Activity extends AppCompatActivity{
    /*
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rx_release_two);
        findViewById(R.id.bt_no_back_pressure).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_no_back_pressure:
                normalNoBackPressureObservable();
                break;
        }
    }

    private void normalNoBackPressureObservable() {
        //被观察者
        Observable mObservable=Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onComplete();
            }
        });
        //观察者
        Observer observer = new Observer<Integer>(){
            //这是新加入的方法，在订阅后发送数据之前，
            //回首先调用这个方法，而Disposable可用于取消订阅
            @Override
            public void onSubscribe(Disposable d) {
                Log.e("yin","onSubscribe:"+d.isDisposed());
            }

            @Override
            public void onNext(Integer value) {
                Log.e("yin","onNext:"+value);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("yin","onError:"+e.toString());
            }

            @Override
            public void onComplete() {
                Log.e("yin","onComplete");
            }
        };
        mObservable.subscribe(observer);
    }
    */
}
