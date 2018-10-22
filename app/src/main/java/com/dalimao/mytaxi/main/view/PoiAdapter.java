package com.dalimao.mytaxi.main.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dalimao.mytaxi.R;

import java.util.List;

/**
 * author: apple
 * created on: 2018/10/22 下午4:07
 * description:
 */
public class PoiAdapter extends ArrayAdapter {
    private List<String> data;
    private LayoutInflater inflater;
    private AdapterView.OnItemClickListener mOnItemClickListener;

    public PoiAdapter( Context context,List data) {
        super(context, R.layout.poi_list_item);
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    public void setOnItemClicklistener(AdapterView.OnItemClickListener listener){
        mOnItemClickListener = listener;
    }
    public void setData (List<String> data){
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        Holder holder = null;
        if (convertView==null){
            convertView = inflater.inflate(R.layout.poi_list_item,null);

            holder = new Holder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }
        holder.id = position;
        holder.name.setText(data.get(position));

        return convertView;
    }
    class Holder {
        int id;
        TextView name;
    }
}

