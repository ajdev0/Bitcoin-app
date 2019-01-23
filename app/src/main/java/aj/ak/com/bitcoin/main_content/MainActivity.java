package aj.ak.com.bitcoin.main_content;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.startapp.android.publish.adsCommon.AutoInterstitialPreferences;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.startapp.android.publish.adsCommon.VideoListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import aj.ak.com.bitcoin.MenuNav.FaqActivity;
import aj.ak.com.bitcoin.MenuNav.PatmenyActivity;
import aj.ak.com.bitcoin.R;
import aj.ak.com.bitcoin.Register.RegisterActivity;
import aj.ak.com.bitcoin.Users.Users;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnSpin,btnOne,btnTwo,btnThree,btnFour,btnFive,btnSix;;

    private TextView txtCoins;
    private ImageView imageView;
    Random r;
    int degree = 0, degree_old = 0;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    FirebaseUser u;
    long current_coin;
    ProgressBar mProgressBar;
    private static boolean  userCoins =false;
    private static boolean  reward =false;
    long a;
    private StartAppAd startAppAd = new StartAppAd(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StartAppSDK.init(this, "202580835", false);
        StartAppAd.enableAutoInterstitial();
        //   StartAppAd.showSplash(this,savedInstanceState);

        imageView = findViewById(R.id.imageView);
        txtCoins = findViewById(R.id.txtCoins);
        btnSpin = findViewById(R.id.btnSpin);
        btnOne = findViewById(R.id.btnOne);
        btnTwo = findViewById(R.id.btnTwo);
        btnThree = findViewById(R.id.btnThree);
        btnFour = findViewById(R.id.btnFour);
        btnFive = findViewById(R.id.btnFive);
        btnSix = findViewById(R.id.btnSix);

        mProgressBar = findViewById(R.id.progressBarH);
        r = new Random();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        u= firebaseAuth.getCurrentUser();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        }

        StartAppAd.setAutoInterstitialPreferences(
                new AutoInterstitialPreferences()
                        .setSecondsBetweenAds(60)
        );
        StartAppAd.disableSplash();
        StartAppAd.showAd(this);
        btnSpin.setOnClickListener(this);
        btnOne.setOnClickListener(this);
        btnTwo.setOnClickListener(this);
        btnThree.setOnClickListener(this);
        btnFour.setOnClickListener(this);
        btnFive.setOnClickListener(this);
        btnSix.setOnClickListener(this);
        databaseReference.child("USERS").child(u.getUid()).child("u").setValue(1);
        adsBlocker();

    }

    @Override
    public void onBackPressed() {
        StartAppAd.onBackPressed(this);
        super.onBackPressed();
    }
    @Override
    public void onPause() {
        super.onPause();
        ReadUserData();
    }
    @Override
    public void onStart() {
        super.onStart();
        StartAppAd.disableSplash();
        ReadUserData();
        txtCoins.setText(current_coin + " KH/s");
    }
    void ReadUserData(){
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if(dataSnapshot.)

                Users user =      dataSnapshot.child("USERS").child(u.getUid()).getValue(Users.class);
                if (user!=null) {
                    current_coin = user.coins;
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
    void WriteCoins(){
        ReadUserData();
        if(userCoins == true){

            FirebaseUser u = firebaseAuth.getCurrentUser();
            a = r.nextInt(100) + 300;
            current_coin += a;
            txtCoins.setText(current_coin + " KH/s");
            databaseReference.child("USERS").child(u.getUid()).child("coins").setValue(current_coin);
            databaseReference.child("USERS").child(u.getUid()).child("email").setValue(u.getEmail());
        }else {
            Toast.makeText(getApplicationContext(),"plz wait to fetch data",Toast.LENGTH_SHORT).show();
        }
    }
    public void OnSpin() {
        //degree
        StartAppAd.setAutoInterstitialPreferences(
                new AutoInterstitialPreferences()
                        .setSecondsBetweenAds(60)
        );
        StartAppAd.showAd(this);
        degree_old = degree % 360;
        degree = r.nextInt(3600) + 720;

        //Rotate
        RotateAnimation rotate = new RotateAnimation(degree_old, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(3600);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new DecelerateInterpolator());
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Toast.makeText(getApplicationContext(), a + " KH/s", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(rotate);
        //text.setText("btc");
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
            databaseReference.child("AdsBlocker").child(u.getUid()).setValue(users);

        }
    }
    void countPreogress(){
        ObjectAnimator animator = ObjectAnimator.ofInt(mProgressBar,"progress", 0, 100);
        animator.setDuration(10000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }
    void timer(){
        countPreogress();
        OnSpin();
        WriteCoins();

        Toast.makeText(getApplicationContext(),"Please Wait 10 Second",Toast.LENGTH_LONG).show();
        btnSpin.setEnabled(false);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnSpin.setEnabled(true);
                        //  mProgressBar.setProgress(0);
                    }
                });
            }
        },10000);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                startActivity(new Intent(this,SettingActivity.class));
                break;
            case R.id.action_settings:
                startActivity(new Intent(this,FaqActivity.class));
                break;
            case R.id.payyments:
                startActivity(new Intent(this,PatmenyActivity.class));
                break;
            case R.id.action_logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));

                break;
        }
        return true;
    }
    //video_ads
    void oneAds(){
        ReadUserData();
        if(userCoins == true) {
            startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO);
            startAppAd.setVideoListener(new VideoListener() {
                @Override
                public void onVideoCompleted() {
                    long sum;
                    FirebaseUser u = firebaseAuth.getCurrentUser();
                    sum = current_coin + 3000;
                    databaseReference.child("USERS").child(u.getUid()).child("coins").setValue(sum);
                    Toast.makeText(MainActivity.this, "win 3000 ", Toast.LENGTH_SHORT).show();
                }
            });
            startAppAd.showAd();
        }else {
            Toast.makeText(MainActivity.this, "No video available now ", Toast.LENGTH_SHORT).show();
        }
    }
    void twoAds(){
        ReadUserData();
        if(userCoins == true) {
            startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO);
            reward = true;
            startAppAd.setVideoListener(new VideoListener() {
                @Override
                public void onVideoCompleted() {
                    long sum;
                    FirebaseUser u = firebaseAuth.getCurrentUser();
                    sum = current_coin + 2000;
                    databaseReference.child("USERS").child(u.getUid()).child("coins").setValue(sum);
                    Toast.makeText(MainActivity.this, "win 2000 ", Toast.LENGTH_SHORT).show();
                }
            });
            startAppAd.showAd();
        }else {
            Toast.makeText(MainActivity.this, "No video available now ", Toast.LENGTH_SHORT).show();
        }
    }
    void threeAds(){
        ReadUserData();
        if(userCoins == true) {
            startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO);
            startAppAd.setVideoListener(new VideoListener() {
                @Override
                public void onVideoCompleted() {
                    long sum ;
                    FirebaseUser u = firebaseAuth.getCurrentUser();
                    sum = current_coin +1000;
                    databaseReference.child("USERS").child(u.getUid()).child("coins").setValue(sum);
                    Toast.makeText(MainActivity.this,"win 1000 ",Toast.LENGTH_SHORT).show();
                }
            });
            startAppAd.showAd();
        }else {
            Toast.makeText(MainActivity.this, "No video available now ", Toast.LENGTH_SHORT).show();
        }
    }
    void fourAds(){
        ReadUserData();
        if(userCoins == true) {
            startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO);
            startAppAd.setVideoListener(new VideoListener() {
                @Override
                public void onVideoCompleted() {
                    long sum ;
                    FirebaseUser u = firebaseAuth.getCurrentUser();
                    sum = current_coin +4000;
                    databaseReference.child("USERS").child(u.getUid()).child("coins").setValue(sum);
                    Toast.makeText(MainActivity.this,"win 4000 ",Toast.LENGTH_SHORT).show();
                }
            });
            startAppAd.showAd();
        }else {
            Toast.makeText(MainActivity.this, "No video available now ", Toast.LENGTH_SHORT).show();
        }
    }
    void fiveAds(){
        ReadUserData();
        if(userCoins == true) {
            startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO);
            startAppAd.setVideoListener(new VideoListener() {
                @Override
                public void onVideoCompleted() {
                    long sum ;
                    FirebaseUser u = firebaseAuth.getCurrentUser();
                    sum = current_coin +5000;
                    databaseReference.child("USERS").child(u.getUid()).child("coins").setValue(sum);
                    Toast.makeText(MainActivity.this,"win 5000 ",Toast.LENGTH_SHORT).show();
                }
            });
            startAppAd.showAd();
        }else {
            Toast.makeText(MainActivity.this, "No video available now ", Toast.LENGTH_SHORT).show();
        }
    }
    void sixAds(){
        ReadUserData();
        if(userCoins == true) {
            startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO);
            startAppAd.setVideoListener(new VideoListener() {
                @Override
                public void onVideoCompleted() {
                    long sum ;
                    FirebaseUser u = firebaseAuth.getCurrentUser();
                    sum = current_coin +6000;
                    databaseReference.child("USERS").child(u.getUid()).child("coins").setValue(sum);
                    Toast.makeText(MainActivity.this,"win 6000 ",Toast.LENGTH_SHORT).show();
                }
            });
            startAppAd.showAd();
        }else {
            Toast.makeText(MainActivity.this, "No video available now ", Toast.LENGTH_SHORT).show();
        }
    }
    //end


    @Override
    public void onClick(View view) {
        if(view == btnSpin ){
            timer();

        }
        if(view == btnOne ){

            oneAds();
        }
        if(view==btnTwo){
            twoAds();
        }
        if(view==btnThree){
            threeAds();
        }
        if(view==btnFour) {
            fourAds();
        }
        if(view==btnFive){
            fiveAds();
        }
        if(view==btnSix){
            sixAds();
        }
    }

}

