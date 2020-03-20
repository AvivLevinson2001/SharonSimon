package com.example.sharonsimon.Interfaces;

import com.example.sharonsimon.Classes.Ken;

import java.util.ArrayList;

public interface FirebaseChangesListener {
    void reloadInfoFromFirebase();
    void saveKensToFirebase(ArrayList<Ken> kens);
}
