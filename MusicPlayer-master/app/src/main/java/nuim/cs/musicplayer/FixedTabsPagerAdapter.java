package nuim.cs.musicplayer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import org.intellij.lang.annotations.JdkConstants;

/**
 * Created by Sean on 13/01/2018.
 */

public class FixedTabsPagerAdapter extends FragmentPagerAdapter {

    public FixedTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount(){
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Fragment();
            case 1:
                return new Fragment();
            case 2:
                return new Fragment();
            default:
                return null;
        }
    }
/**
    ViewPager viewPager = (ViewPager) findViewById(R.id.go_to_player);
    PagerAdapter pagerAdapter =
            new FixedTabsPagerAdapter(getSupportFragmentManager());
    viewPager.setAdapter(pagerAdapter);
**/

}
