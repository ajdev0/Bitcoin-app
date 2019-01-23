package aj.ak.com.bitcoin.Splash;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import aj.ak.com.bitcoin.R;
import aj.ak.com.bitcoin.Register.RegisterActivity;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME = 3000;
    private boolean InternetCheck = true;
    private ProgressBar spinner;
    ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        PostDelayedMethod();

    }

    public void PostDelayedMethod() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                boolean InternetResult = checkConnection();
                if (InternetResult) {

                    Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    spinner.setVisibility(View.GONE);
                    spinner.setVisibility(View.VISIBLE);

                    DialogAppear();
                }
            }
        }, SPLASH_TIME);

    }

    public void DialogAppear() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("Network Error");
        builder.setMessage("No Internet Connection");

        //negative message
        builder.setNegativeButton("Exit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        //positive message
        builder.setPositiveButton("Retry",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(checkConnection() == false) {
                            DialogAppear();
                        }else {
                            PostDelayedMethod();
                        }
                    }
                });
        builder.show();
    }

    //check internet status of the mobile
    public boolean isOnline() {

        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cn.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            InternetCheck =true;
            return true;
        } else {
            return false;
        }
    }

    public boolean checkConnection() {
        if (isOnline()) {
            return InternetCheck;
        } else {
            InternetCheck = false;
            return InternetCheck;
        }
    }
}
