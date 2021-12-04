package com.example.myappchat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.myappchat.Adapter.SectionsPagerAdapter;
import com.example.myappchat.MainActivity;
import com.example.myappchat.R;
import com.example.myappchat.Env.env;
import com.example.myappchat.model.User;
import com.google.android.material.tabs.TabLayout;

public class HomeActivity extends AppCompatActivity {
    private Toolbar toolbar;


    private ViewPager viewPager;
    private SectionsPagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private String token;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        AmplifyAws.intializeAmplify(this);

        i = getIntent();
//        LoginModel loginModel = (LoginModel)i.getSerializableExtra("loginModel");

        toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);


        viewPager = findViewById(R.id.viewPager_home);
        tabLayout = findViewById(R.id.tab_home);

        pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.btn_account:
                startActivity(new Intent(HomeActivity.this, UpdateProfileActivity.class));
                return true;
            case R.id.btn_search:
                startActivity(new Intent(HomeActivity.this, SearchUserActivity.class));
                return true;
            case R.id.btn_Admin:
                startActivity(new Intent(HomeActivity.this, AdminActivity.class));
                return true;
            case R.id.btn_Logout:
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                env.setToken("abcdef");
                env.setUser(new User());
                finish();
                return true;
            case R.id.btn_createConv:
                startActivity(new Intent(HomeActivity.this, CreateConvActivity.class));
                return true;
            default:
                return true;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (env.getUser().isAdmin()) {
            menu.findItem(R.id.btn_Admin).setVisible(true);
        } else {
            menu.findItem(R.id.btn_Admin).setVisible(false);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            toolbar.setTitle(env.getUser().getUsername());
        } catch (Exception e){

        }
    }

    void displayCreateConv(){

    }
}