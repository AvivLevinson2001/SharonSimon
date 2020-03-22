package com.example.sharonsimon.Classes;

import java.io.Serializable;
import java.util.ArrayList;

public class Ken implements Serializable,Comparable {

    String name;
    ArrayList<Task> tasks;
    int points;

    public Ken() {
    }

    public Ken(String name, ArrayList<Task> allTasks, int points) {
        this.name = name;
        this.tasks = allTasks;
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
                ", completedTasks=" + tasks +
                ", points=" + points +
                '}';
    }

    public void removeTasks(ArrayList<Task> tasks){
        this.tasks.removeAll(tasks);
    }

    public void removeTask(Task task){
        if(task == null) tasks = new ArrayList<>();
        tasks.remove(task);
    }

    public void addTasks(ArrayList<Task> tasks){
        this.tasks.addAll(tasks);
    }

    public void addTask(Task task){
        if(tasks == null) tasks = new ArrayList<>();
        tasks.add(task);
    }

    public void calculatePoints(){
        this.points = 0;
        for(Task task : this.tasks){
            if(task.isCompleted()) this.points += task.getPoints();
        }
    }

    @Override
    public int compareTo(Object o)
    {
        int comparePoints=((Ken)o).getPoints();

        return comparePoints-this.points;
    }
}
