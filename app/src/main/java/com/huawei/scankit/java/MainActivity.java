package com.huawei.scankit.java;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.huawei.scankit.java.fragments.BitmapFragment;
import com.huawei.scankit.java.fragments.CustomizedViewFragment;
import com.huawei.scankit.java.fragments.DefaultViewFragment;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements TabLayoutMediator.TabConfigurationStrategy {

    private static String TAG = "MainActivity";

    private final static int POSITION_DEFAULT_VIEW = 0;
    private final static int POSITION_CUSTOMIZED_VIEW = 1;

    private final static int PAGE_COUNT = 3;

    private static String[] PERMISSIONS = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private final static int CAMERA_REQ_CODE = 1;

    private String[] tabTitles;

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        // TabLayout titles
        tabTitles = getResources().getStringArray(R.array.tabs_title);

        SearchViewPagerAdapter adapter = new SearchViewPagerAdapter(this);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager, this).attach();

        if (checkPermission()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, CAMERA_REQ_CODE);
        } else {
            Log.i(TAG, "Permission granted.");
        }
    }

    private Boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, PERMISSIONS[0]) != PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, PERMISSIONS[1]) != PERMISSION_GRANTED;
    }

    @Override
    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
        tab.setText(tabTitles[position]);
        viewPager.setCurrentItem(tab.getPosition(), true);
    }

    @Override
    public void onRequestPermissionsResult(
        int requestCode,
        @NonNull String[] permissions,
        @NonNull int[] grantResults
    ) {
        if (requestCode == CAMERA_REQ_CODE
                && grantResults.length == 2
                && grantResults[0] == PERMISSION_GRANTED
                && grantResults[1] == PERMISSION_GRANTED
        ) {
            Log.i(TAG, getString(R.string.permission_granted));
            Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == Config.REQUEST_CODE_SCAN_ONE
                && viewPager.getCurrentItem() == Config.POSITION_DEFAULT_VIEW
        ) {
            String fragmentTag = getString(R.string.fragment_tag, Config.POSITION_DEFAULT_VIEW);
            DefaultViewFragment defaultViewFragment = (DefaultViewFragment)
                    getSupportFragmentManager().findFragmentByTag(fragmentTag);
            if (defaultViewFragment != null) {
                defaultViewFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    static class SearchViewPagerAdapter extends FragmentStateAdapter {

        public SearchViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position)
            {
                case POSITION_DEFAULT_VIEW: return new DefaultViewFragment();
                case POSITION_CUSTOMIZED_VIEW: return new CustomizedViewFragment();
                default: return new BitmapFragment();
            }
        }

        @Override
        public int getItemCount() {
            return PAGE_COUNT;
        }
    }
}