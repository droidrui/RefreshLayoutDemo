package com.droidrui.refreshlayoutdemo.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.droidrui.refreshlayoutdemo.R;
import com.droidrui.refreshlayoutdemo.adapter.TabPagerAdapter;
import com.droidrui.refreshlayoutdemo.fragment.GridViewFragment;
import com.droidrui.refreshlayoutdemo.fragment.ListViewFragment;
import com.droidrui.refreshlayoutdemo.fragment.RecyclerGridFragment;
import com.droidrui.refreshlayoutdemo.fragment.RecyclerListFragment;
import com.droidrui.refreshlayoutdemo.fragment.RelativeLayoutFragment;
import com.droidrui.refreshlayoutdemo.fragment.ScrollViewFragment;
import com.droidrui.refreshlayoutdemo.fragment.TextViewFragment;
import com.droidrui.refreshlayoutdemo.fragment.ViewPagerFragment;
import com.droidrui.refreshlayoutdemo.fragment.WebViewFragment;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new ViewPagerFragment());
        fragmentList.add(new RecyclerListFragment());
        fragmentList.add(new ListViewFragment());
        fragmentList.add(new RecyclerGridFragment());
        fragmentList.add(new GridViewFragment());
        fragmentList.add(new ScrollViewFragment());
        fragmentList.add(new WebViewFragment());
        fragmentList.add(new RelativeLayoutFragment());
        fragmentList.add(new TextViewFragment());
        String[] titles = new String[]{
                "ViewPager",
                "Recycler List",
                "ListView",
                "Recycler Grid",
                "GridView",
                "ScrollView",
                "WebView",
                "RelativeLayout",
                "TextView"
        };
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        TabPagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), fragmentList, titles);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
