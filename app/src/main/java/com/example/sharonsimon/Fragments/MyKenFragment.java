package com.example.sharonsimon.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharonsimon.R;

public class MyKenFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.my_ken_fragment,container,false);

        ImageView myKenImage = viewGroup.findViewById(R.id.my_ken_image);
        TextView myKenPointsTV = viewGroup.findViewById(R.id.my_ken_points_tv);
        RecyclerView recycler = viewGroup.findViewById(R.id.tasks_rv);

        return viewGroup;
    }
}
