package rahulkumardas.rangde;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

/**
 * Created by Rahul Kumar Das on 14-01-2017.
 */

public class RangDeApplication extends Application {

    public static DatabaseReference reference;
    private FirebaseDatabase myRef;
    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        myRef = FirebaseDatabase.getInstance();
        myRef.setLogLevel(Logger.Level.DEBUG);
        reference = myRef.getReference();
    }

}
