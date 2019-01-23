package aj.ak.com.bitcoin.MenuNav;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import java.util.ArrayList;

import aj.ak.com.bitcoin.R;
import aj.ak.com.bitcoin.Register.RegisterActivity;
import aj.ak.com.bitcoin.Users.ListItem;
import aj.ak.com.bitcoin.main_content.MainActivity;

public class FaqActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        StartAppSDK.init(this, "202580835", false);
        ListView ls = findViewById(R.id.list_item);

        ls.setAdapter(new MyCustumAdapter(this));
        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView txtName = view.findViewById(R.id.txt_name);

               /* if(i == 0){
                    startActivity(new Intent(MainActivity.this,Main2Activity.class));
                }if (i == 1){
                    Toast.makeText(getApplicationContext(),txtName.getText(),Toast.LENGTH_LONG).show();
                }*/
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();


        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        }


    }
    // Adapter
    class MyCustumAdapter extends BaseAdapter {

        ArrayList<ListItem> items ;
        Context context;

        MyCustumAdapter(Context context){
            this.context = context;
            items = new ArrayList<ListItem>();
            items.add(new ListItem("What is KH/s","it's a cloud mining algorithm developed by our teams. "));
            items.add(new ListItem("When can i Withdraw","every 20th of the month. "));
            items.add(new ListItem("Is there any Fee in transaction","Yes, If you chose any other wallet than xapo. "));
            items.add(new ListItem("Calculate","100000000Kh = 0.02 LTC "));

        }

        public MyCustumAdapter() {
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i).Name;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View view1 = layoutInflater.inflate(R.layout.row_view,null);

            TextView txtName = view1.findViewById(R.id.txt_name);
            TextView txtDesc= view1.findViewById(R.id.txt_desc);


            ListItem temp = items.get(i);

            txtName.setText(items.get(i).Name);
            txtDesc.setText(items.get(i).Desc);

            return view1;
        }
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
}
