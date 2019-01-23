package aj.ak.com.bitcoin.Register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import aj.ak.com.bitcoin.R;
import aj.ak.com.bitcoin.Users.Users;
import aj.ak.com.bitcoin.main_content.MainActivity;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtEmail, txtPassword;
    private Button btnRegister;
    private TextView txtView;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    boolean user_rev_status = false;
    String user_rev_uid;
    long current_coin = 0;
    String uid;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //FirebaseUser u = firebaseAuth.getCurrentUser();
        // uid = u.getUid();
        firebaseAuth = FirebaseAuth.getInstance();


        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        // user_rev_uid = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Register User ...");
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);

        btnRegister = findViewById(R.id.btnRegister);
        txtView = findViewById(R.id.txtLoginHere);

        btnRegister.setOnClickListener(this);
        txtView.setOnClickListener(this);
        RewardApp();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    void RewardApp() {

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            if (deepLink != null && deepLink.getBooleanQueryParameter("invitedby", false)) {
                                user_rev_status = true;
                                user_rev_uid = deepLink.getQueryParameter("invitedby");
                                ReadUserCoinsData(user_rev_uid);
                                Toast.makeText(getApplicationContext(), "your Friend invite you coins" + current_coin, Toast.LENGTH_SHORT).show();
                            }

                        }


                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("getDynamicLink", "getDynamicLink:onFailure", e);
                    }
                });
    }


    //region With Email and password
    void createUser(String email, String password) {
        if (isInternetOn())
            progressing(true);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            CreateNewUser(user);
                            progressing(false);
                        } else {
                            progressing(false);
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), String.format("Authentication failed. %s", task.getException().getMessage()), Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void registerUser() {
        createUser(txtEmail.getText().toString(), txtPassword.getText().toString());
    }


    /*public void openSignin(View view) {
      startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }*/


    //endregion

    //region Database Manpulation User


    void CreateNewUser(FirebaseUser u) {

        Users user = new Users();
        user.name = u.getDisplayName();
        user.email = u.getEmail();
        mDatabase.child("USERS").child(u.getUid()).setValue(user);
        checkUserReval(u);
    }

    void checkUserReval(FirebaseUser user) {
        if (user_rev_status) {
            // new user reward coins
            WriteUserCoinsData(0, 100000, user.getUid());
            // old user reward coins
            WriteUserCoinsData(current_coin, 100000, user_rev_uid);

            Toast.makeText(getApplicationContext(), "You Win 100000 from your Friend!", Toast.LENGTH_SHORT).show();

            OpenScreen();

        } else {

            OpenScreen();
        }
    }

    void WriteUserCoinsData(long current_coins, long coins, String uid) {

        long sum;
        sum = current_coins + coins;
        mDatabase.child("USERS").child(uid).child("coins").setValue(sum);
    }

    void ReadUserCoinsData(final String uid) {

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Users user = dataSnapshot.child("USERS").child(uid).getValue(Users.class);
                if (user != null) {
                    current_coin = user.coins;

                    Toast.makeText(getApplicationContext(), user.coins + "", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }

        };
        mDatabase.addValueEventListener(userListener);
    }


    //endregion

    //region Accessories
    void OpenScreen() {

        Toast.makeText(getApplicationContext(), "Sign up Seccussfully!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            // if connected with internet

            //Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

            Toast.makeText(this, " Not Connected ,please connect to internet", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    void progressing(boolean status) {

        if (status) {
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }

    }

    @Override
    public void onClick(View view) {
        if (view == btnRegister) {
            registerUser();
        }
        if (view == txtView) {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        }

    }
}
//endregion

