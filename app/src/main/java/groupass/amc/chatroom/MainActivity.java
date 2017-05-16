package groupass.amc.chatroom;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import groupass.amc.chatroom.GeoLoc.GeoLoc;
import groupass.amc.chatroom.auth.SignupActivity;
import groupass.amc.chatroom.auth.LoginActivity;
import layout.ChangeEmail;
import layout.ChangePassword;
import layout.ResetPassword;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    public static String name;
    private NavigationView nvDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        //Asking For Permitions
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        TextView Welcome = (TextView) findViewById(R.id.welcome);
        Welcome.setText("Welcome to Frog Chat \n" + user);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        navigationView.setNavigationItemSelectedListener(this);
        request_user_name();
        auth = FirebaseAuth.getInstance();

        //Check if user is Online
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };


    }

    private void displaySelectedScreen(int itemId) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView Welcome = (TextView) findViewById(R.id.welcome);
        ImageView Logo = (ImageView) findViewById(R.id.imageView);
        Fragment fragment = null;
        Intent intent;
        Intent intent2;
        switch (itemId) {

            case R.id.Add_room:
                intent = new Intent(this, AddRoom.class);
                startActivity(intent);
                finish();
                break;
            case R.id.geo_loc:
                intent2 = new Intent(this, GeoLoc.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.change_email_button:
                fragment = new ChangeEmail();
                break;
            case R.id.change_password_button:
                fragment = new ChangePassword();
                break;
            case R.id.sending_pass_reset_button:
                fragment = new ResetPassword();
                break;
            case R.id.remove_user_button:
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MainActivity.this, SignupActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                break;
            case R.id.sign_out:
                signOut();
                name = null;
                break;

        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            Logo.setVisibility(View.INVISIBLE);
            Welcome.setVisibility(View.INVISIBLE);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }

    }

    public void request_user_name() {
        if (name == null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter name your Nickname:");
            final EditText input_field = new EditText(this);
            builder.setView(input_field);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (input_field.getText().length() <= 1) {
                        Toast.makeText(MainActivity.this, "Please enter some NickName", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        name = input_field.getText().toString();
                    }

                }
            });
            builder.show();
        } else
            return;
    }

    public String showName() {
        return name;
    }

    public String getName() {
        return name;
    }

}
