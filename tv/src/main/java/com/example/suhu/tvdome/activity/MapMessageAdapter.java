package com.example.suhu.tvdome.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.suhu.tvdome.R;

import java.util.List;

/**
 * @author suhu
 * @data 2017/10/19.
 * @description 地图展示表格适配器
 */

public class MapMessageAdapter extends RecyclerView.Adapter<MapMessageAdapter.MapViewHolder> {
    private Context context;
    private List<String> list;
    private OnItemClickListener mOnItemClickListener;

    public MapMessageAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public MapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MapViewHolder holder = new MapViewHolder(LayoutInflater.from(context).inflate(R.layout.item_map, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MapViewHolder holder, int position) {
        holder.textView.setText(list.get(position));
        addListener(holder);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    private void addListener(final MapViewHolder holder) {
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(holder.getLayoutPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        /**
         * 点击回调
         *
         * @param position
         */
        void onItemClick(int position);
    }

    class MapViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public MapViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.map_text);
        }
    }

}
