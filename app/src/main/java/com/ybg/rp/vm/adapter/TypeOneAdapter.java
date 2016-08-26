package com.ybg.rp.vm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ybg.rp.vm.R;
import com.ybg.rp.vmbase.bean.TypeOne;

import java.util.List;

/**
 * Created by yangbagang on 16/8/26.
 */
public class TypeOneAdapter extends RecyclerView.Adapter<TypeOneAdapter.TypeOneHolder> {

    private Context mContext;
    private List<TypeOne> data;
    private ItemClickListener mItemClickListener;
    private int selectIndex = -1;

    public TypeOneAdapter(Context context, List<TypeOne> data) {
        this.mContext = context;
        this.data = data;
    }

    @Override
    public TypeOneHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemvView = View.inflate(mContext, R.layout.item_type_one, null);
        return new TypeOneHolder(itemvView, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(final TypeOneHolder holder, final int position) {
        TypeOne typeOne = data.get(position);
        String name = typeOne.getName();
        holder.tv_name.setText(name);

        if (position == selectIndex) {
            holder.view.setSelected(true);
        } else {
            holder.view.setSelected(false);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemCLick(v, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (data.size() > 0) {
            return data.size();
        }
        return 0;
    }

    public class TypeOneHolder extends RecyclerView.ViewHolder {

        private View view;
        private ImageView iv_image; //商品图片
        private TextView tv_name;      //名称

        private ItemClickListener mItemClickListener;

        public TypeOneHolder(View itemView, ItemClickListener listener) {
            super(itemView);
            this.mItemClickListener = listener;
            //找id
            iv_image = (ImageView) itemView.findViewById(R.id.typeOne_iv_image);
            tv_name = (TextView) itemView.findViewById(R.id.typeOne_tv_name);
            view = itemView;
        }
    }

    /**
     * 设置item点击事件
     */
    public void setOnItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemCLick(View view, int position);
    }

    /**
     * 设置选中item 改变背景
     * @param i
     */
    public void setSelectIndex(int i) {
        selectIndex = i;
    }

}
