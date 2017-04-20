package rahulkumardas.rangde;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "Rahul Debug";
    private Spinner typeSinner;
    private String type[] = {"Options(parent, tutor)", "Parent", "Tutor"};
    EditText name, email, password;
    Button create;

    private DatabaseReference ref = RangDeApplication.reference;
    private ProgressDialog pd;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    // [END declare_auth_listener]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.i(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.i(TAG, "onAuthStateChanged:signed_out");
                }
                // [START_EXCLUDE]
                updateUI(user);
                // [END_EXCLUDE]
            }
        };
        // [END auth_state_listener]

        pd = new ProgressDialog(this);
        pd.setMessage("Please wait...");
        typeSinner = (Spinner)findViewById(R.id.typeSpinner);
        typeSinner.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, type));

        name = (EditText)findViewById(R.id.name);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.paddword);
        create  = (Button)findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(name.getText())||TextUtils.isEmpty(email.getText())||TextUtils.isEmpty(password.getText())){
                    Toast.makeText(RegisterActivity.this, "Empty field(s)", Toast.LENGTH_SHORT).show();
                }else if(typeSinner.getSelectedItem().toString().equals("Options(parent, tutor)")){
                    Toast.makeText(RegisterActivity.this, "Please select account type", Toast.LENGTH_SHORT).show();
                }else{
                    createAccount(email.getText().toString().trim(), password.getText().toString().trim());
                }
            }
        });
    }

    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    // [END on_stop_remove_listener]

    private void createAccount(final String email, String password) {
        Log.d(TAG, "createAccount:" + email);

        pd.show();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            String key = ref.child("User").push().getKey();
                            ref.child("User/"+key+"/name").setValue(name.getText().toString());
                            ref.child("User/"+key +"/email").setValue(email);
                            ref.child("User/"+key+"/type").setValue(typeSinner.getSelectedItem().toString(), new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if(databaseError==null){
                                        Toast.makeText(RegisterActivity.this, "Registration success!",
                                                Toast.LENGTH_SHORT).show();
                                        mAuth.addAuthStateListener(mAuthListener);
                                    }else{
                                        Toast.makeText(RegisterActivity.this, "Error occurred!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        // [START_EXCLUDE]
                        pd.dismiss();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }


    private void updateUI(FirebaseUser user) {
        pd.dismiss();
        if (user != null) {
            String userEmail = user.getEmail();
            String userId = user.getUid();
            Toast.makeText(this, userId+" Registration Success!", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, userId+" "+name+" "+typeSinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, DashBoardActivity.class);
            i.putExtra("email", userEmail);
            startActivity(i);
        } else {
            Toast.makeText(this, "Registration Failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
