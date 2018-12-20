package id.co.reich.mockupsouthscape;

import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import id.co.reich.mockupsouthscape.fragment.GridFragment;
import id.co.reich.mockupsouthscape.session.Constants;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GridFragment.OnFragmentInteractionListener {

//    GridView androidGridView;
    FrameLayout mContentFrame;
    Fragment fragment = null;

    private AppController app() {
        return AppController.getInstance();
    }

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

        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = hView.findViewById(R.id.txtView_username);

        HashMap hm = app().getSession().getUserDetails();
        String user_name = (String) hm.get(Constants.KEY_USERNAME);
        nav_user.setText(user_name);

        ImageView img_avatar = hView.findViewById(R.id.imgView_avatar);
        String base64image = (String) hm.get(Constants.KEY_IMAGE_AVATAR);
        img_avatar.setImageBitmap(ImageHelper.decodeImage(base64image));

        mContentFrame = findViewById(R.id.nav_contentframe);
        setFragment("");
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
        String myTag = "";

        switch (id)
        {
            case R.id.nav_home :
                app().toast("Home Selected");
                fragment = new GridFragment();
                myTag = "HOME";
                setFragment(myTag);
                break;
            case R.id.nav_settings:
                app().toast("Settings Selected");
//                myTag = "SETTINGS";
                break;
            case R.id.nav_logout:
                app().toast("Logout Selected");
//                myTag = "LOGOUT";
                showLogoutDialog();
                break;
            default:
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onGridFragmentInteraction(Uri uri) {

    }

    public void showLogoutDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to Logout?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Logout and close the dialog
                app().getSession().logoutUser();
                dialog.dismiss();
                finish();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void setFragment(String fragment_TAG) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragment == null)
        {
            ft.replace(R.id.nav_contentframe, new GridFragment());
        }
        else
        {
            ft.replace(R.id.nav_contentframe, fragment, fragment_TAG);
        }

        ft.commit();
    }

}
