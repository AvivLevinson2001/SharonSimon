package com.example.sharonsimon.Interfaces;

import com.example.sharonsimon.Classes.Ken;
import com.example.sharonsimon.Classes.Task;

import java.util.ArrayList;

public interface FirebaseChangesListener {
    void saveKenToFirebase(Ken kenToSave);
    void addTaskToFirebase(Task task);
    void removeTaskFromFirebase(Task task);
    void addTaskToHighlights(String taskDesc, String kenName);
}
