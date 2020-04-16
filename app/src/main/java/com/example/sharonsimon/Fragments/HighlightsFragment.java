package com.example.sharonsimon.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharonsimon.Adapters.HighlightAdapter;
import com.example.sharonsimon.Classes.Highlight;
import com.example.sharonsimon.Interfaces.FirebaseChangesListener;
import com.example.sharonsimon.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class HighlightsFragment extends Fragment {

    RecyclerView recycler;
    HighlightAdapter adapter;
    FirebaseChangesListener firebaseChangesListener;
    LinearLayout noHighlightsLL;

    ArrayList<Highlight> highlights;
    boolean isAdmin;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity)context;
        try{
            firebaseChangesListener = (FirebaseChangesListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException("Activity: " + activity.toString() + " must implement FirebaseChangesListener");
        }
    }

    public static HighlightsFragment newInstance(ArrayList<Highlight> highlights, boolean isAdmin){
        HighlightsFragment fragment = new HighlightsFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable("highlights",highlights);
        arguments.putBoolean("isAdmin", isAdmin);
        fragment.setArguments(arguments);
        return fragment;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.highlights_fragment,container,false);

        isAdmin = getArguments().getBoolean("isAdmin", false);
        if (isAdmin) setHasOptionsMenu(true);
        highlights = (ArrayList<Highlight>) getArguments().getSerializable("highlights");
        if (highlights == null) highlights = new ArrayList<>();

        noHighlightsLL = viewGroup.findViewById(R.id.no_highlights_ll);
        setNoHighlightsLLVisibility();

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
                                firebaseChangesListener.removeTaskFromHighlights(highlightToRemove);
                                adapter.notifyItemRemoved(position);
                                setNoHighlightsLLVisibility();
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
                else{
                    Snackbar.make(container,"חכה שהעלאת הסרטון תסתיים", BaseTransientBottomBar.LENGTH_LONG).show();
                }
            }
        });

        recycler.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(mLayoutManager);

        return viewGroup;
    }

    public void notifyAdapter(){
        adapter.notifyDataSetChanged();
    }

    private void setNoHighlightsLLVisibility(){
        if(highlights.size() == 0){
            noHighlightsLL.setVisibility(View.VISIBLE);
        }
        else{
            noHighlightsLL.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        if (isAdmin)
        {
            inflater.inflate(R.menu.delete_highlights_menu, menu);

        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == R.id.action_delete_highlights)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("מחיקה").setMessage("האם אתה בטוח שברצונך למחוק את כל הקטעים החמים?").setNegativeButton("לא", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).setPositiveButton("כן", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    int size = highlights.size();

                    for (int j = 0;j <size;j++)
                    {
                        firebaseChangesListener.removeTaskFromHighlights(highlights.get(0));
                        adapter.notifyItemRemoved(0);
                    }
                    setNoHighlightsLLVisibility();
                }
            }).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
