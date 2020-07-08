package com.gfd.eshop.base;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * 简单列表适配器基类.
 *
 * @param <T> 数据实体的类型.
 * @param <V> ViewHolder的类型.
 */
public abstract class BaseListAdapter<T, V extends BaseListAdapter.ViewHolder> extends BaseAdapter {

    private final List<T> mDataSet = new ArrayList<>();//数据源


    @Override public final int getCount() {
        return mDataSet.size();
    }//获得适配器的大小

    @Override public final T getItem(int position) {//获得第几个数据
        return mDataSet.get(position);
    }
    //获得第几个数据id
    @Override public long getItemId(int position) {
        return 0;
    }

    @Override public final View getView(int position, View convertView, ViewGroup parent) {//获得item

        View itemView = createItemViewIfNotExist(convertView, parent);

        // noinspection unchecked
        ViewHolder viewHolder = (ViewHolder) itemView.getTag();
        viewHolder.bind(position);
        return itemView;
    }

    //重新设置数据源
    public void reset(@Nullable List<T> data) {
        mDataSet.clear();
        if (data != null) mDataSet.addAll(data);
        notifyDataSetChanged();
    }

    //添加数据源
    public void addAll(@Nullable List<T> data) {
        if (data != null) mDataSet.addAll(data);
        notifyDataSetChanged();
    }

    //获得xml布局视图
    @LayoutRes protected abstract int getItemViewLayout();

    //
    protected abstract V getItemViewHolder(View itemView);

    private View createItemViewIfNotExist(View itemView, ViewGroup parent) {
        if (itemView == null) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(getItemViewLayout(), parent, false);
            itemView.setTag(getItemViewHolder(itemView));
        }
        return itemView;
    }
/*

要想使用 ListView 就需要编写一个 Adapter 将数据适配到 ListView上，而为了节省资源提高运行效率，
一般自定义类 ViewHolder 来减少 findViewById() 的使用以及避免过多地 inflate view，从而实现目标。

作者：李铭淋
链接：https://www.jianshu.com/p/a14feb480804
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 */
    public abstract class ViewHolder {

        protected final View mItemView;

        protected ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
            this.mItemView = itemView;
        }

        protected final Context getContext() {
            return mItemView.getContext();
        }

        protected abstract void bind(int position);
    }

}
