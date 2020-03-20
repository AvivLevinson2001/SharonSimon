package com.example.sharonsimon.Interfaces;

import com.example.sharonsimon.Classes.Ken;
import com.example.sharonsimon.Classes.Task;

import java.util.ArrayList;

public interface FirebaseChangesListener {
    void saveKenToFirebase(Ken kenToSave);
    void addTasksToFirebase(ArrayList<Task> tasks);
}
