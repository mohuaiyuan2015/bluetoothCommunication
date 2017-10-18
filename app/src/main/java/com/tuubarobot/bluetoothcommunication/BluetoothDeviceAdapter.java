package com.tuubarobot.bluetoothcommunication;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by YF-04 on 2017/7/22.
 */

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder> {
    private static final String TAG = "BluetoothDeviceAdapter";
    private List<BluetoothDevice> list;

    private OnItemClickListener onItemClickListener;

    public BluetoothDeviceAdapter(List<BluetoothDevice>list){
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

        TextView address;
        TextView name;

        public ViewHolder(final View view) {
            super(view);
            name= (TextView) view.findViewById(R.id.name);
            address= (TextView) view.findViewById(R.id.address);
        }
    }


    @Override
    public BluetoothDeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetoothdevice_item_layout,parent,false);
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
    public void onBindViewHolder(BluetoothDeviceAdapter.ViewHolder holder, int position) {

        ViewHolder cellViewHolder = (ViewHolder) holder;

        cellViewHolder.itemView.setTag(position);

        BluetoothDevice bluetoothDevice=list.get(position);

        holder.name.setText("蓝牙名称："+bluetoothDevice.getName());
        holder.address.setText("蓝牙地址"+bluetoothDevice.getAddress());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
