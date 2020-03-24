package com.example.sharonsimon.Classes;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

public class Task implements Serializable {

    String desc;
    int points;
    boolean isCompleted;

    public Task() {
    }

    public Task(Task task){
        this.desc = task.desc;
        this.points = task.points;
        this.isCompleted = task.isCompleted;
    }

    public Task(String desc, int points, boolean isCompleted) {
        this.desc = desc;
        this.points = points;
        this.isCompleted = isCompleted;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    @Override
    public String toString() {
        return "Task{" +
                "desc='" + desc + '\'' +
                ", points=" + points +
                ", isCompleted=" + isCompleted +
                '}';
    }

    public boolean isSameTask(Task t){
        return (this.desc.equals(t.desc) && this.points == t.points);
    }

    public boolean taskExists(ArrayList<Task> tasks){
        for(Task task : tasks){
            if(task.getDesc().equals(this.getDesc()))
                return true;
        }
        return false;
    }
}
