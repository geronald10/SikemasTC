package id.ac.its.sikemastc.activity.mahasiswa;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.sync.SikemasSyncUtils;

public class DetailLihatJadwal extends AppCompatActivity {

    private final String TAG = DetailLihatJadwal.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_lihat_jadwal);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tl_halaman_utama_mahasiswa);
        tabLayout.setupWithViewPager(viewPager);

        SikemasSyncUtils.startImmediatePerkuliahanMahasiswaSync(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DetailJadwalKelas(), "INFO KELAS");
        adapter.addFragment(new DetailRiwayatKehadiran(), "RIWAYAT KEHADIRAN");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle data = new Bundle();
            data.putString("id_mahasiswa", userId);
            mFragmentList.get(position).setArguments(data);
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
