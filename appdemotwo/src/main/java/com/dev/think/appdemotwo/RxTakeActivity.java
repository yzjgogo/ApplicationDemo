package com.dev.think.appdemotwo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class RxTakeActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mText;
    private Button mBtn;
    private TextView mEdit;
    private Integer [] number={1,2,3,4,5,6,7,8,9,10};
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

        mEdit.setText("输出[1,2,3,4,5,6,7,8,9,10]中第三个和第四个奇数，\n\ntake(i) 取前i个事件 \ntakeLast(i) 取后i个事件 \ndoOnNext(Action1) 每次观察者中的onNext调用之前调用");
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
                if(mText.getText().toString()!=null ||mText.getText().toString().length()>0){
                    mText.setText("");
                }
                start();
                break;
        }
    }

    private void start() {
        Observable.from(number)
                  .filter(new Func1<Integer, Boolean>() {
                      @Override
                      public Boolean call(Integer integer) {
                          Log.e("yin","执行过滤的call:"+integer);//只执行了1 2 3 4 5 6 7，而不是1 2 3 4 5 6 7 8 9 10
                          return integer%2!=0;
                      }
                  })
                    //取前四个事件，也算是一种过滤，只保留前四个
                    .take(4)//取狗4个奇数后，上面的call就不再执行
                    //取前四个中的后两个事件
                    .takeLast(2)
                    //每次执行onNext之前都会执行一次doOnNext,因此doOnNext的执行次数和onNext的执行次数相同
                    .doOnNext(new Action1<Integer>() {
                        @Override
                        public void call(Integer integer) {
                            mText.append("before onNext（）\n");
                        }
                    })
                    .subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer integer) {
                            mText.append("onNext()--->"+integer+"\n");
                        }
                    });
    }
}
