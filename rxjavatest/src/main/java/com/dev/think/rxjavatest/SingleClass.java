package com.dev.think.rxjavatest;

import java.util.List;

/**
 * Created by think on 2017/3/7.
 */

public class SingleClass {
    private List<Student> students;

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    @Override
    public String toString() {
        return "SingleClass{" +
                "students=" + students +
                '}';
    }
}
