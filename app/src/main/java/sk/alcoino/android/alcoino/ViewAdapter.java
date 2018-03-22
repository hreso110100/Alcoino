package sk.alcoino.android.alcoino;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

public class ViewAdapter extends FragmentPagerAdapter {

    private Context context;

    public ViewAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = context;
    }

    // returns selected view of tab

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ValuesFragment();
        } else {
            return new LedFragment();
        }
    }

    // returns number of tabs

    @Override
    public int getCount() {
        return 2;
    }

    // change title depending on active tab

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {

            return context.getString(R.string.value_fragment);
        } else {
            return context.getString(R.string.led_fragment);
        }
    }
}
