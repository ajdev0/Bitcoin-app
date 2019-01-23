package aj.ak.com.bitcoin.main_content;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import aj.ak.com.bitcoin.R;
import aj.ak.com.bitcoin.Users.Users;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    TextView wel_email;
    Users user_winner;
    Uri mInvitationUrl;
    FirebaseUser u ;
    private EditText editBtc,editXapo;
    private Button btnBtc,btnXapo,withdraw,Share,Rate;
    long current_coin= 0;
    FirebaseDatabase database;
    private static final int REQUEST_INVITE = 1;
    String bit , xapo;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private static boolean  userCoins =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        StartAppSDK.init(this, "202580835", true);

        database = FirebaseDatabase.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        u = firebaseAuth.getCurrentUser();
        wel_email= findViewById(R.id.wel_emil);
        wel_email.setText(""+ u.getEmail());
        editBtc = findViewById(R.id.editBtc);
        editBtc.setSaveEnabled(true);

        editXapo = findViewById(R.id.editXapo);
        btnBtc = findViewById(R.id.btnBtc);
        btnXapo = findViewById(R.id.btnXapo);
        withdraw = findViewById(R.id.Withdraw);
        Share=findViewById(R.id.btnShare);
        Rate=findViewById(R.id.btnRate);

        btnXapo.setOnClickListener(this);
        btnBtc.setOnClickListener(this);
        withdraw.setOnClickListener(this);
        Share.setOnClickListener(this);
        Rate.setOnClickListener(this);
        adsBlocker();
        ReadUserData();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Rate.setVisibility(sharedPreferences.getInt("ratebtn", View.VISIBLE));
    }
    private void adsBlocker(){
        ReadUserData();
        BufferedReader in = null;
        boolean result = true;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream("/etc/hosts")));
            String line;
            while ((line = in.readLine()) != null){
                if (line.contains("admob")){
                    result = false;
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(result == false){
            //message here
            ReadUserData();
            FirebaseUser u = firebaseAuth.getCurrentUser();
            Users users = new Users();
            users.userId = u.getUid();
            users.coins=current_coin;
            users.email = u.getEmail();
            users.bitAddress=bit;
            users.xapoAddress=xapo;

            databaseReference.child("AdsBlocker").child(u.getUid()).setValue(users);

        }
    }
    //the withdraw process
    public void Withdraw(){

        if ( current_coin >= 100000000) {
            ReadUserData();
            WriteUserCoinsData();
            FirebaseUser u = firebaseAuth.getCurrentUser();
            Users users = new Users();
            users.email = u.getEmail();
            users.name = u.getDisplayName();
            users.coins=current_coin;
            users.Winnercoin = 100000000;
            users.bitAddress=bit;
            users.xapoAddress=xapo;

            databaseReference.child("WInner").child(u.getUid()).setValue(users);
            //current_coin -=5000;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(" Balance");
            builder.setMessage("Your request for withdraw has been submit.  \nThe process will take 5 day to arrive.\nPlease Check the payment page to track your payment");

            //negative message
            builder.setNegativeButton("Exit",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            //positive message
            builder.show();

        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(" Balance");
            builder.setMessage("Minimum withdraw balance 100000000 KH/s \nThe withdraw will proceed manually Every 20th of month  ");

            //negative message
            builder.setNegativeButton("Exit",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            //positive message
            builder.show();
        }

    }
    //saving biccoin addrees & xapo address
    public void Addresses(){
        String BtcAddress = editBtc.getText().toString().trim();
        String XapoAddress = editXapo.getText().toString().trim();
        FirebaseUser u = firebaseAuth.getCurrentUser();
        databaseReference.child("USERS").child(u.getUid()).child("bitAddress").setValue(BtcAddress);
        databaseReference.child("USERS").child(u.getUid()).child("xapoAddress").setValue(XapoAddress);
        Toast.makeText(getApplicationContext(),"Address Save Successfully",Toast.LENGTH_SHORT).show();



    }
    //read user data
    public void ReadUserData(){
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser u = firebaseAuth.getCurrentUser();
                Users user =      dataSnapshot.child("USERS").child(u.getUid()).getValue(Users.class);
                if (user!=null&&user.bitAddress!=""&&user.xapoAddress!=""){
                    editBtc.setText(Html.fromHtml( "<font color='#000'>"+"<b>" +user.bitAddress+"</b>"+"</font>"));
                    editXapo.setText(Html.fromHtml( "<font color='#000'>"+"<b>" +user.xapoAddress+"</b>"+"</font>"));
                    bit = user.bitAddress;
                    xapo= user.xapoAddress;
                    current_coin=user.coins;

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }

        };
        databaseReference.addValueEventListener(userListener);
    }
    //Write User data
    void WriteUserCoinsData(){

        long sub ;
        FirebaseUser u = firebaseAuth.getCurrentUser();
        sub= current_coin - 100000000;
        databaseReference.child("USERS").child(u.getUid()).child("coins").setValue(sub);
    }
    @Override
    public void onStop() {
        super.onStop();
        if(Rate.isPressed()){
            Rate.setClickable(false);
        }else {
            Rate.setClickable(true);
        }
    }

    void ReadData(){
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if(dataSnapshot.)

                Users user =      dataSnapshot.child("USERS").child(u.getUid()).getValue(Users.class);
                if (user!=null) {
                    current_coin = user.coins;
                    // current_coin = 1;
                    userCoins=true;

                }else {
                    userCoins = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }

        };
        databaseReference.addValueEventListener(userListener);

    }
    void GetUserData(){
        u.getEmail();
    }

    @Override
    public void onStart() {
        super.onStart();

        // check user
        if (firebaseAuth.getCurrentUser() == null){
            finish();
            // startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
        }

        //get data from Firebase object
        GetUserData();

        // get coins form FirebaseDatabase
        ReadUserCoinsData();
    }

    void  ReadUserCoinsData(){

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Users user =      dataSnapshot.child("USERS").child(u.getUid()).getValue(Users.class);
                if (user!=null){
                    current_coin = user.coins;
                    user_winner = user;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }

        };
        databaseReference.addValueEventListener(userListener);
    }

    private void onInviteClicked(String deeplink) {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(deeplink))
                .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    void newlink(final int i){

        String link = "https://litecoinminer.com/minier/?invited by=" + u.getUid();
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDynamicLinkDomain("whp7j.app.goo.gl")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("aj.ak.com.litecoin")
                                .setMinimumVersion(125)
                                .build())

                .buildShortDynamicLink()
                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        mInvitationUrl =shortDynamicLink.getShortLink();

                        if (i == 1){
                            ShareMedia(mInvitationUrl.toString());
                        }
                        else {
                            onInviteClicked(mInvitationUrl.toString());
                        }
                    }
                });

    }

    void  ShareMedia( String deep_link){
        //region df
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Free BTCMining");
        intent.putExtra(Intent.EXTRA_TITLE,"Free BTCMining");
        intent.putExtra(Intent.EXTRA_TEXT,"Play and Earn Money! " +deep_link);
        startActivity(Intent.createChooser(intent,"Share Using"));
        //endregion
    }

    public void share() {

        newlink(1);
    }

    void  Read(){

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Users user =      dataSnapshot.child("USERS").child(u.getUid()).getValue(Users.class);
                if (user!=null){
                    current_coin = user.coins;
                    //  user_winner = user;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }

        };
        databaseReference.addValueEventListener(userListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    //       Toast.makeText(getApplicationContext(), "sent invitation "+ id ,Toast.LENGTH_LONG).show();

                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }
    }
    public void setRate(){
        Read();
        ReadData();
        if(userCoins == true) {
            WriteeUserCoinsData();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=aj.ak.com.litecoin")));
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putInt("ratebtn",Rate.INVISIBLE);
            editor.commit();

        }else {
            Toast.makeText(getApplicationContext(),"Plz Wait to fetch data",Toast.LENGTH_SHORT).show();
        }
    }

    void WriteeUserCoinsData(){

        long sum ;
        FirebaseUser u = firebaseAuth.getCurrentUser();
        sum = current_coin +100000;
        databaseReference.child("USERS").child(u.getUid()).child("coins").setValue(sum);
    }

    @Override
    public void onBackPressed() {
        StartAppAd.onBackPressed(this);
        super.onBackPressed();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.close:
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;

        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view == btnBtc){
            Addresses();
        }
        if(view == btnXapo){
            Addresses();
        }
        if(view == withdraw){
            Withdraw();
        }
        if(view == Share){
            share();
        }
        if(view == Rate){
            setRate();
            ViewGroup viewGroup= (ViewGroup) view.getParent();
            viewGroup.removeView(view);
        }
    }
}
