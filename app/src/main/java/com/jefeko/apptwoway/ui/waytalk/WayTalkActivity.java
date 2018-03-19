package com.jefeko.apptwoway.ui.waytalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.ui.common.BaseActivity;
import com.jefeko.apptwoway.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WayTalkActivity extends BaseActivity {
    @BindView(R.id.wayTalk_tab) TabLayout wayTalk_tab;
    @BindView(R.id.waytalk_viewpager)
    ViewPager mViewPager;

    ViewPagerAdapter mPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_way_talk);
        ButterKnife.bind(this);
        mContext = this;

        setToolbar();
        wayTalk_tab.setupWithViewPager(mViewPager);
        setUpViewPager();

        LogUtils.d("page = "+getIntent().getStringExtra("page"));

        if(getIntent().getStringExtra("page") != null){
            mViewPager.setCurrentItem(1);
        }
    }

    private void setUpViewPager() {
        mPagerAdapter = new WayTalkActivity.ViewPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new WayTalkGroupFragment(), getString(R.string.waytalk_grup));
        mPagerAdapter.addFragment(new WayTalkHistoryFragment(), getString(R.string.waytalk_history));
        mViewPager.setAdapter(mPagerAdapter);
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent != null) {
            doPushResult(intent);
        }
    }

    public void doPushResult(Intent intent) {

        setUpViewPager();

        mViewPager.setCurrentItem(1);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        //push
//        Intent intent = getIntent();
//        if(intent != null) {
//            doPushResult(intent);
//        }
    }

}
