package com.example.phone.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.phone.R;

import java.util.List;

/**
 * @author suhu
 * @data 2017/10/19.
 * @description 地图展示表格适配器
 */

public class MapMessageAdapter extends RecyclerView.Adapter<MapMessageAdapter.MapViewHolder> {
    private Context context;
    private List<ItemMessage> list;
    private OnItemClickListener mOnItemClickListener;

    public MapMessageAdapter(Context context, List<ItemMessage> list) {
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
        holder.textView.setText(list.get(position).getMessage());
        int typ = list.get(position).getTyp();
        holder.circle.setVisibility(View.VISIBLE);
        holder.circle.setText(typ+"");
        switch (typ){
            case 0:
                holder.circle.setBackgroundDrawable(context.getDrawable(R.drawable.circle_blue_bg));
                break;
            case 1:
                holder.circle.setBackgroundDrawable(context.getDrawable(R.drawable.circle_red_bg));
                break;
            default:
                holder.circle.setVisibility(View.GONE);
                break;
        }
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
                    mOnItemClickListener.onItemClick(list.get(holder.getLayoutPosition()));
                }
            });
        }
    }

    public void setList(List<ItemMessage> list) {
        this.list = list;
        if (list!=null){
            //notifyItemRangeChanged(0,list.size());
            notifyDataSetChanged();
        }

    }

    public interface OnItemClickListener {
        /**
         * 点击回调
         *
         * @param itemMessage
         */
        void onItemClick(ItemMessage itemMessage);
    }

    class MapViewHolder extends RecyclerView.ViewHolder {
        private TextView textView,circle;

        public MapViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.map_text);
            circle = (TextView) itemView.findViewById(R.id.circle);
        }
    }

}
