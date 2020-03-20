package com.example.sharonsimon.Classes;

import java.io.Serializable;
import java.util.ArrayList;

public class Ken implements Serializable {

    String name;
    ArrayList<Task> todaysTasks;
    ArrayList<Task> allTasks;
    int points;

    public Ken() {
    }

    public Ken(String name, ArrayList<Task> todaysTasks, ArrayList<Task> allTasks, int points) {
        this.name = name;
        this.todaysTasks = todaysTasks;
        this.allTasks = allTasks;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Task> getTodaysTasks() {
        return todaysTasks;
    }

    public void setTodaysTasks(ArrayList<Task> todaysTasks) {
        this.todaysTasks = todaysTasks;
    }

    public ArrayList<Task> getAllTasks() {
        return allTasks;
    }

    public void setAllTasks(ArrayList<Task> allTasks) {
        this.allTasks = allTasks;
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
                ", tasks=" + todaysTasks +
                ", completedTasks=" + allTasks +
                ", points=" + points +
                '}';
    }
}
