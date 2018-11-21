package id.co.reich.mockupsouthscape;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    GridView androidGridView;
    FrameLayout mContentFrame;
    Fragment fragment = null;

    private AppController app() {
        return AppController.getInstance();
    }

    Integer[] imageIDs = {
            R.drawable.ic_icons8_home,
            R.drawable.ic_if_money_322468,
            R.drawable.ic_if_store_326704,
            R.drawable.ic_if_calendar_115762
    };

    String[] imageNames = {
            "Rumah",
            "Keuangan",
            "Layanan",
            "Agenda"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        mContentFrame = findViewById(R.id.nav_contentframe);

//        androidGridView = findViewById(R.id.gridview_android_example);
//        androidGridView.setAdapter(new ImageAdapterGridView(this));
//
//        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent,
//                                    View v, int position, long id) {
//                Toast.makeText(getBaseContext(), "Grid Item " + (position + 1) + " Selected", Toast.LENGTH_LONG).show();
//
//                switch(position){
//                    case 3 :
//                        Intent intent = new Intent(MainActivity.this,PlaceholderActivity.class);
//                        startActivity(intent);
//                        break;
//                    default:
//                        break;
//                }
//
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        switch (id)
        {
            case R.id.nav_home :
                app().toast("Home Selected");
                break;
            case R.id.nav_settings:
                app().toast("Settings Selected");
                break;
            case R.id.nav_logout:
                app().toast("Logout Selected");
                break;
            default:
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class ImageAdapterGridView extends BaseAdapter {
        private Context mContext;

        public ImageAdapterGridView(Context c) {
            mContext = c;
        }

        public int getCount() {
            return imageIDs.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View gridViewAndroid;

            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                gridViewAndroid = inflater.inflate(R.layout.gridview_single_item, null);
                TextView textViewAndroid = gridViewAndroid.findViewById(R.id.android_gridview_text);
                ImageView imageViewAndroid = gridViewAndroid.findViewById(R.id.android_gridview_image);
                textViewAndroid.setText(imageNames[position]);
                imageViewAndroid.setImageResource(imageIDs[position]);

            } else {
                gridViewAndroid = convertView;
            }
            return gridViewAndroid;
        }
    }
}
