package palie.splist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sharie on 6/13/2017.
 */

class WaitingAdapter extends RecyclerView.Adapter<WaitingAdapter.ViewHolder> {

    private ArrayList<List> mLists;
    private Context mContext;

    WaitingAdapter(ArrayList<List> mLists, Context mContext) {
        super();
        this.mLists = mLists;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        List list = mLists.get(position);
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView name;

        ViewHolder(View v) {
            super(v);
            icon = (ImageView) v.findViewById(R.id.icon);
            name = (TextView) v.findViewById(R.id.name);
        }
    }

}
