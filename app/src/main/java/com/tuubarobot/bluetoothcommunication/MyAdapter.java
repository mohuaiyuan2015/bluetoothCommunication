package com.tuubarobot.bluetoothcommunication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import java.util.List;
import java.util.Map;

/**
 * Created by YF-04 on 2017/7/22.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static final String TAG = "MyAdapter";
    private List<Map<String,String>> list;

    private OnItemClickListener onItemClickListener;

    public MyAdapter(List<Map<String,String>>list){
        this.list=list;

    }

    public void clear() {
        list.clear();
    }



    public interface OnItemClickListener {
        public void onItemClick(View itemView, int position);
    }



    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }



    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView question;
        TextView answer;

        public ViewHolder(final View view) {
            super(view);
            question= (TextView) view.findViewById(R.id.questionTextView);
            answer= (TextView) view.findViewById(R.id.answerTextView);
        }
    }


    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, (Integer) v.getTag());
                }
            }
        });



        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {

        ViewHolder cellViewHolder = (ViewHolder) holder;

        cellViewHolder.itemView.setTag(position);

        Map<String,String>map=list.get(position);
        holder.question.setText(map.get(Constants.QUESTION));
        holder.answer.setText(map.get(Constants.ANSWER));


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
