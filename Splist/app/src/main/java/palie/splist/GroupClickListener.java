package palie.splist;

import android.widget.ImageView;
import android.widget.TextView;

public interface GroupClickListener {
    void onGroupClick(String key, ImageView sharedImageView, TextView sharedTextView);
}
