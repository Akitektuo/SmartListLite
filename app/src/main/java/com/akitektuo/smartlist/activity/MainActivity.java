package com.akitektuo.smartlist.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.adapter.ViewPagerAdapter;
import com.akitektuo.smartlist.fragment.ListFragment;
import com.akitektuo.smartlist.fragment.SettingsFragment;
import com.akitektuo.smartlist.util.Preference;

import static com.akitektuo.smartlist.util.Constant.KEY_CREATED;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, View.OnClickListener {

    private ViewPager pager;
    private TabLayout tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Preference preference = new Preference(this);
        if (!preference.getPreferenceBoolean(KEY_CREATED)) {
            preference.setDefault();
        }

        pager = (ViewPager) findViewById(R.id.container_main);
        setupViewPager();

        tab = (TabLayout) findViewById(R.id.tab_main);
        tab.setupWithViewPager(pager);
        tab.addOnTabSelectedListener(this);
        setupTabIcons();
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ListFragment());
        adapter.addFragment(new SettingsFragment());
        pager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        tab.getTabAt(0).setIcon(R.drawable.light_list_selected);
        tab.getTabAt(1).setIcon(R.drawable.light_settings);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                tab.setIcon(R.drawable.light_list_selected);
                break;
            case 1:
                tab.setIcon(R.drawable.light_settings_selected);
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                tab.setIcon(R.drawable.light_list);
                break;
            case 1:
                tab.setIcon(R.drawable.light_settings);
                break;
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onClick(View view) {

    }
}
