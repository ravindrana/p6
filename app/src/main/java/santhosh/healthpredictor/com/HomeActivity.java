package santhosh.healthpredictor.com;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import santhosh.healthpredictor.com.application.MyApplication;
import santhosh.healthpredictor.com.data.MyFireBaseDB;


public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Toolbar toolbar = (Toolbar)findViewById(R.id.mToolbar);
        //setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.mipmap.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        NavigationView navView = (NavigationView) findViewById(R.id.navigation_view);
        if (navView != null){
            setupDrawerContent(navView);
            //show profile fragment by default
            showFragment(new ProfileFragment());
            //drawerLayout.openDrawer(GravityCompat.START);
            navView.setCheckedItem(R.id.drawer_profile);
        }

        //init firebase authlistener and db
        MyFireBaseDB.getInstance().init(this);
    }

    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);

                switch (menuItem.getItemId()) {
                    case R.id.drawer_home:
                        //viewPager.setCurrentItem(0);
                        showFragment(new HomeFragment());
                        break;
                    case R.id.drawer_profile:
                        showFragment(new ProfileFragment());
                        //viewPager.setCurrentItem(1);
                        break;
                    case R.id.drawer_settings:
                        showFragment(new HomeFragment());
                        break;
                    case R.id.drawer_signout:
                        showAlertDialog(R.id.drawer_signout);
                        break;
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });
        View header=navigationView.getHeaderView(0);

        FirebaseUser user = ((MyApplication)getApplication()).getFirebaseUser();
        ((TextView)header.findViewById(R.id.header_email)).setText(user.getEmail());
        ((TextView)header.findViewById(R.id.header_user)).setText(user.getDisplayName());
    }

    public void showFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.realcontent, fragment, null);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*//noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        switch (id){
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAlertDialog(final int id){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        switch (id){
                            case R.id.drawer_signout:
                                new PreferenceManager(HomeActivity.this).setSignedOut(true);
                                ((MyApplication)(getApplication())).getFirebaseAuth()
                                        .signOut();
                                startActivity(new Intent(HomeActivity.this, WelcomeActivity.class));
                                finish();
                                break;
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
                dialog.dismiss();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to signout?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }
}
