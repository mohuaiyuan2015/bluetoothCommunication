package com.tuubarobot.bluetoothcommunication;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.security.PublicKey;
import java.util.List;

/**
 * Created by YF-04 on 2017/7/22.
 */

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder> {
    private static final String TAG = "BluetoothDeviceAdapter";
    private List<BluetoothDeviceModel> list;

    private OnItemClickListener onItemClickListener;
    private OnSelectListener onSelectListener;

    public BluetoothDeviceAdapter(List<BluetoothDeviceModel>list){
        this.list=list;

    }

    public void clear() {
        list.clear();
    }



    public interface OnItemClickListener {
        public void onItemClick(View itemView, int position);
    }

    public interface OnSelectListener{
        public void onSelectClick(View itemView, int position);
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnSelectListener getOnSelectListener() {
        return onSelectListener;
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public List<BluetoothDeviceModel> getList() {
        return list;
    }

    public void setList(List<BluetoothDeviceModel> list) {
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView address;
        TextView name;
        TextView boundState;
        CheckBox selectedState;

        public ViewHolder(final View view) {
            super(view);
            name= (TextView) view.findViewById(R.id.name);
            address= (TextView) view.findViewById(R.id.address);
            boundState= (TextView) view.findViewById(R.id.boundState);
            selectedState= (CheckBox) view.findViewById(R.id.selectedState);
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
        viewHolder.selectedState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSelectListener!=null){
                    onSelectListener.onSelectClick(v,(Integer) v.getTag());
                }

            }
        });


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BluetoothDeviceAdapter.ViewHolder holder, int position) {

        ViewHolder cellViewHolder = (ViewHolder) holder;

        cellViewHolder.itemView.setTag(position);
        cellViewHolder.selectedState.setTag(position);

        BluetoothDeviceModel deviceModel=list.get(position);

        holder.name.setText("蓝牙名称："+deviceModel.getDevice().getName());
        holder.address.setText("蓝牙地址："+deviceModel.getDevice().getAddress());

        int bondState=deviceModel.getDevice().getBondState();
        String bondString="";
        if (bondState==BluetoothDevice.BOND_BONDED){
            bondString="已经配对";
        }else if (bondState==BluetoothDevice.BOND_NONE){
            bondString="未 配对";
        }else {
            bondString="未知状态";
        }
        holder.boundState.setText("绑定状态："+bondString);

        holder.selectedState.setChecked(deviceModel.isSelectState());
        //若未配对，则不显示
//        if (bondState!=BluetoothDevice.BOND_BONDED){
//            holder.selectedState.setVisibility(View.GONE);
//        }else {
//            holder.selectedState.setVisibility(View.VISIBLE);
//        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
