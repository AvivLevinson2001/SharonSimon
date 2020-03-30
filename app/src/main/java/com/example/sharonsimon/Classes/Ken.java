package com.example.sharonsimon.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class Ken implements Serializable,Comparable {

    String name;
    ArrayList<Task> tasks;
    int points;
    String animalImageUrl;

    public Ken() {
    }

    public Ken(String name, ArrayList<Task> tasks, int points, String animalImageUrl) {
        this.name = name;
        this.tasks = tasks;
        this.points = points;
        this.animalImageUrl = animalImageUrl;
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

    public String getAnimalImageUrl() {
        return animalImageUrl;
    }

    public void setAnimalImageUrl(String animalImageUrl) {
        this.animalImageUrl = animalImageUrl;
    }

    @Override
    public String toString() {
        return "Ken{" +
                "name='" + name + '\'' +
                ", tasks=" + tasks +
                ", points=" + points +
                ", animalImageUrl='" + animalImageUrl + '\'' +
                '}';
    }

    public void removeTaskByDesc(String desc){
        if(tasks == null) tasks = new ArrayList<>();
        Iterator<Task> iterator = tasks.iterator();
        while(iterator.hasNext()){
            Task task = iterator.next();
            if(task.getDesc().equals(desc)){
                iterator.remove();
            }
        }
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
