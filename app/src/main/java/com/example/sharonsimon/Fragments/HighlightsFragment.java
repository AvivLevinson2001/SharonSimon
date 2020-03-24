package com.example.sharonsimon.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharonsimon.Adapters.HighlightAdapter;
import com.example.sharonsimon.Classes.Highlight;
import com.example.sharonsimon.R;

import java.util.ArrayList;


public class HighlightsFragment extends Fragment {

    RecyclerView recycler;
    HighlightAdapter adapter;

    ArrayList<Highlight> highlights;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.highlights_fragment,container,false);

        highlights = new ArrayList<>();

        recycler = viewGroup.findViewById(R.id.highlights_recycler);
        adapter = new HighlightAdapter(highlights, getActivity());
        recycler.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(mLayoutManager);


        return viewGroup;
    }
}
