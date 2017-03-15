package com.dev.think.rxjavatest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<SingleClass> singleClassList = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            SingleClass singleClass = new SingleClass();
            List<Student> studentList = new ArrayList<>();
            for (int j = 1; j <= 3; j++) {
                studentList.add(new Student("学生" + i + "" + j));
            }
            singleClass.setStudents(studentList);
            singleClassList.add(singleClass);
        }
        Log.e("yin", "集合：" + singleClassList.toString());


        //创建被观察者，获取所有班级
        Observable.from(singleClassList)
                //类似于转换操作Map将SingleClass转换为Observable<Student>
                .flatMap(new Func1<SingleClass, Observable<Student>>() {
                    @Override
                    public Observable<Student> call(SingleClass singleClass) {
                        //将每个班级的所有学生作为一列表包装成一列Observable<Student>，将学生一个一个传递出去
                        return Observable.from(singleClass.getStudents());
                    }
                })
                .subscribe(
                        //创建观察者，作为事件传递的终点处理事件
                        new Subscriber<Student>() {
                            @Override
                            public void onCompleted() {
                                Log.e("yin", "结束观察...\n");
                            }

                            @Override
                            public void onError(Throwable e) {
                                //出现错误会调用这个方法
                            }

                            @Override
                            public void onNext(Student student) {
                                //接受到每个学生类
                                Log.e("yin", student.getName());
                            }
                        }
                );
    }
}
