package com.example.sharonsimon.classes;

public class Task {

    String desc;
    int points;
    boolean isCompleted;

    public Task() {
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
}
