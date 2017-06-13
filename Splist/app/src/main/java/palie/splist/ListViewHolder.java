package palie.splist;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


class ListViewHolder extends RecyclerView.ViewHolder {

    ImageView icon;
    TextView name;
    String listKey;
    int position;

    ListViewHolder(View v, final ListClickListener listClickListener) {
        super(v);
        icon = (ImageView) v.findViewById(R.id.icon);
        name = (TextView) v.findViewById(R.id.name);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listClickListener.onGroupClick(position, listKey);
            }
        });
    }
}