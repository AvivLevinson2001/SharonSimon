package com.example.sharonsimon.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharonsimon.Adapters.KenAdapter;
import com.example.sharonsimon.Classes.Ken;

import com.example.sharonsimon.R;

import java.util.ArrayList;

public class KensRecyclerViewFragment extends Fragment {

    ArrayList<Ken> kensArrayList = new ArrayList<>();
    KenAdapter adapter;
    KensRecyclerViewFragmentListener listener;

    public interface KensRecyclerViewFragmentListener{
        void onKenClick(Ken ken);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity)context;
        try{
            listener = (KensRecyclerViewFragmentListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException("Activity: " + activity.toString() + " must implement KensRecyclerViewFragmentListener");
        }
    }

    public static KensRecyclerViewFragment newInstance(ArrayList<Ken> kensArrayList){
        KensRecyclerViewFragment fragment = new KensRecyclerViewFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable("kensArrayList",kensArrayList);
        fragment.setArguments(arguments);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.kens_recycler_view_fragment,container,false);

        kensArrayList = (ArrayList<Ken>) getArguments().getSerializable("kensArrayList");

        RecyclerView recyclerView = viewGroup.findViewById(R.id.kens_rv);
        adapter = new KenAdapter(kensArrayList,getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.setListener(new KenAdapter.myKenAdapterListener() {
            @Override
            public void onKenClick(int position, View v) {
                listener.onKenClick(kensArrayList.get(position));
            }
        });

        return viewGroup;
    }
}
