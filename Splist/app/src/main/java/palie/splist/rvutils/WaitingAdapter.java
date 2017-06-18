package palie.splist.rvutils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

import palie.splist.ListClickListener;
import palie.splist.R;
import palie.splist.model.List;

public class WaitingAdapter extends RecyclerView.Adapter<ListViewHolder> {

    private ArrayList<List> mLists;
    private Context mContext;
    private ListClickListener listClickListener;

    public WaitingAdapter(ArrayList<List> mLists, Context mContext, ListClickListener listClickListener) {
        super();
        this.mLists = mLists;
        this.mContext = mContext;
        this.listClickListener = listClickListener;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view, parent, false);
        return new ListViewHolder(v, listClickListener);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        List list = mLists.get(position);
        holder.position = position;
        holder.listKey = list.getKey();
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }
}
