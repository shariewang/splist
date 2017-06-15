package palie.splist;

import android.widget.ImageView;
import android.widget.TextView;

interface GroupClickListener {
    void onGroupClick(int position, String key, ImageView sharedImageView);
}
