package rahulkumardas.rangde;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class DashBoardActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference ref = RangDeApplication.reference;
    private String email;
    private TextView name, type;
    private CardView profile, events;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        email = getIntent().getStringExtra("email");
        name = (TextView) findViewById(R.id.name);
        type = (TextView) findViewById(R.id.type);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();
        ref.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    Iterator<DataSnapshot> users = dataSnapshot.getChildren().iterator();
                    while (users.hasNext()) {
                        DataSnapshot user = users.next();
                        if (user.child("email").getValue(String.class).equals(email)) {
                            String name = user.child("name").getValue(String.class);
                            String type = user.child("type").getValue(String.class);

                            DashBoardActivity.this.name.setText(name);
                            DashBoardActivity.this.type.setText(type);
                        }
                    }
                }catch (Exception e){

                }
                pd.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        profile = (CardView) findViewById(R.id.profile);
        profile.setOnClickListener(this);
        events = (CardView) findViewById(R.id.events);
        events.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile:
                Intent i1;
                if (type.getText().toString().equals("Tutor")) {
                    i1 = new Intent(this, TutorProfileActivity.class);
                } else {
                    i1 = new Intent(this, ProfileActivity.class);
                }
                i1.putExtra("email", email);
                startActivity(i1);
                break;
            case R.id.events:
                Intent i2;
                if (type.getText().toString().equals("Tutor")) {
                    i2 = new Intent(this, TutorEventActivity.class);
                } else {
                    i2 = new Intent(this, EventActivity.class);
                }
                i2.putExtra("email", email);
                startActivity(i2);
                break;
        }
    }
}
