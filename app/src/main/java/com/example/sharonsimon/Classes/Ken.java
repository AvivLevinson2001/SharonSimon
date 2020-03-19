package com.example.sharonsimon.Classes;

import java.util.ArrayList;

public class Ken {

    String name;
    ArrayList<String> tasks;
    ArrayList<String> completedTasks;
    int points;

    public Ken() {
    }

    public Ken(String name, ArrayList<String> tasks, ArrayList<String> completedTasks, int points) {
        this.name = name;
        this.tasks = tasks;
        this.completedTasks = completedTasks;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<String> tasks) {
        this.tasks = tasks;
    }

    public ArrayList<String> getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(ArrayList<String> completedTasks) {
        this.completedTasks = completedTasks;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "Ken{" +
                "name='" + name + '\'' +
                ", tasks=" + tasks +
                ", completedTasks=" + completedTasks +
                ", points=" + points +
                '}';
    }
}
