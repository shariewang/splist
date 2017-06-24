package palie.splist;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class InstanceIDService extends FirebaseInstanceIdService {

    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    static String token;

    @Override
    public void onTokenRefresh() {
        //get updated token
        token = FirebaseInstanceId.getInstance().getToken();
    }
}
