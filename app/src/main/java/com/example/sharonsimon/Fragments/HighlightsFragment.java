package com.example.sharonsimon.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

import com.example.sharonsimon.Adapters.HighlightAdapter;
import com.example.sharonsimon.Classes.Highlight;
import com.example.sharonsimon.Interfaces.FirebaseChangesListener;
import com.example.sharonsimon.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


public class HighlightsFragment extends Fragment {

    RecyclerView recycler;
    HighlightAdapter adapter;
    FirebaseChangesListener listener;

    ArrayList<Highlight> highlights;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity)context;
        try{
            listener = (FirebaseChangesListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException("Activity: " + activity.toString() + " must implement KensRecyclerViewFragmentListener");
        }
    }

    public static HighlightsFragment newInstance(ArrayList<Highlight> highlights){
        HighlightsFragment fragment = new HighlightsFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable("highlights",highlights);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.highlights_fragment,container,false);

        highlights = (ArrayList<Highlight>) getArguments().getSerializable("highlights");
        if (highlights == null) highlights = new ArrayList<>();

        recycler = viewGroup.findViewById(R.id.highlights_recycler);
        adapter = new HighlightAdapter(highlights, getActivity());
        adapter.setListener(new HighlightAdapter.HighlightAdapterListener() {
            @Override
            public void onHighlightLongClick(final int position, View v) {
                if(highlights.get(position).getVideoURL() != null && !highlights.get(position).getVideoURL().equals("")){
                    PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                    popupMenu.getMenuInflater().inflate(R.menu.long_click_delete_menu, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if(menuItem.getItemId() == R.id.action_delete){
                                Highlight highlightToRemove = highlights.get(position);
                                highlights.remove(highlightToRemove);
                                listener.removeTaskFromHighlights(highlightToRemove);
                                adapter.notifyDataSetChanged();
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
                else{
                    Snackbar.make(container,"חכה שהעלאת הסרטון תסתיים", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        });
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

    public void notifyAdapter(){
        adapter.notifyDataSetChanged();
    }
}
