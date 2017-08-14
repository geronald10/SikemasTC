package id.ac.its.sikemastc.activity.mahasiswa;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import id.ac.its.sikemastc.activity.BaseActivity;
import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.data.SikemasSessionManager;
import id.ac.its.sikemastc.sync.SikemasSyncUtils;

public class HalamanUtamaMahasiswa extends BaseActivity {

    private final String TAG = HalamanUtamaMahasiswa.class.getSimpleName();

    private String userId;
    private String userNama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_utama_mahasiswa);

        HashMap<String, String> userDetail = session.getUserDetails();
        userId = userDetail.get(SikemasSessionManager.KEY_USER_ID);
        userNama = userDetail.get(SikemasSessionManager.KEY_USER_NAME);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tl_halaman_utama_mahasiswa);
        tabLayout.setupWithViewPager(viewPager);

        SikemasSyncUtils.startImmediatePerkuliahanMahasiswaSync(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainPerkuliahanFragment(), "PERKULIAHAN AKTIF");
        adapter.addFragment(new ListPerkuliahanFragment(), "LIST PERKULIAHAN");
        viewPager.setAdapter(adapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle data = new Bundle();
            data.putString("id_mahasiswa", userId);
            data.putString("nama_mahasiswa", userNama);
            mFragmentList.get(position).setArguments(data);
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}