package com.example.sharonsimon.Fragments;

import android.graphics.Bitmap;
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
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

import com.example.sharonsimon.Adapters.HighlightAdapter;
import com.example.sharonsimon.Classes.Highlight;
import com.example.sharonsimon.R;

import java.util.ArrayList;


public class HighlightsFragment extends Fragment {

    RecyclerView recycler;
    HighlightAdapter adapter;

    ArrayList<Highlight> highlights;

    public static HighlightsFragment newInstance(ArrayList<Highlight> highlights){
        HighlightsFragment fragment = new HighlightsFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable("highlights",highlights);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.highlights_fragment,container,false);

        highlights = (ArrayList<Highlight>) getArguments().getSerializable("highlights");
        if (highlights == null) highlights = new ArrayList<>();

        recycler = viewGroup.findViewById(R.id.highlights_recycler);
        adapter = new HighlightAdapter(highlights, getActivity());
        recycler.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(mLayoutManager);

        return viewGroup;
    }

    @Override
    public void onPause() {
        JCVideoPlayer.releaseAllVideos();
        super.onPause();
    }
}
