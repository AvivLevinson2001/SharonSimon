package com.example.sharonsimon.classes;

import java.util.ArrayList;

public class Ken {

    String name;
    ArrayList<Task> tasks;
    ArrayList<Task> completedTasks;
    int points;

    public Ken() {
    }

    public Ken(String name, ArrayList<Task> tasks, ArrayList<Task> completedTasks, int points) {
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

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public ArrayList<Task> getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(ArrayList<Task> completedTasks) {
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
