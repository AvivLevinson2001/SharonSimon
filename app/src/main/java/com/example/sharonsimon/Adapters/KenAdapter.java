package com.example.sharonsimon.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sharonsimon.R;
import com.example.sharonsimon.Classes.Ken;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ronto on 18-Dec-18.
 */

public class KenAdapter extends RecyclerView.Adapter<KenAdapter.CreateKenViewHolder>
{

    private ArrayList<Ken> kens;
    private myKenAdapterListener listener;

    public interface myKenAdapterListener
    {
        void onKenClick(int position, View v);
    }

    public void setListener(myKenAdapterListener listener)
    {
        this.listener = listener;
    }

    public KenAdapter(ArrayList<Ken> kens)
    {
        Collections.sort(kens);
        this.kens = kens;
        if(this.kens == null)
            this.kens = new ArrayList<>();
    }

    @Override
    public int getItemCount()
    {
        if(kens == null) return 0;
        return kens.size();
    }

    public class CreateKenViewHolder extends RecyclerView.ViewHolder
    {

        TextView nameTv;
        TextView pointsTv;

        public CreateKenViewHolder(final View itemView)
        {
            super(itemView);

            nameTv = itemView.findViewById(R.id.card_view_ken_name_tv);
            pointsTv = itemView.findViewById(R.id.card_view_ken_points_tv);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(listener != null)
                        listener.onKenClick(getAdapterPosition(), view);
                }
            });
        }
    }

    @Override
    public CreateKenViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_kens, parent, false);
        CreateKenViewHolder createKenViewHolder = new CreateKenViewHolder(view);
        return createKenViewHolder;
    }

    @Override
    public void onBindViewHolder(CreateKenViewHolder holder, int position)
    {
        Ken ken = kens.get(position);
        holder.nameTv.setText(ken.getName());
        holder.pointsTv.setText(ken.getPoints() + "");


    }

    public void setKens(ArrayList<Ken> kens) {
        this.kens = kens;
    }
}
