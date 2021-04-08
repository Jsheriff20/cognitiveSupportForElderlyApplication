package messaging.app.messages.sendingMedia.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import messaging.app.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter implements
        SelectSendToFriendsFragment.onSelectedRowListener,
        SelectSendToFriendsStoryFragment.onSelectedRowListener {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;
    onRowSelectedListener mListener;

    @Override
    public void onSelectedFriendsFragmentRowListener(String UUID, String messageType) {
        mListener.onSelectedListener(UUID, messageType);
    }

    @Override
    public void onSelectedStoryFragmentRowListener(String UUID, String messageType) {
        mListener.onSelectedListener(UUID, messageType);
    }


    public interface onRowSelectedListener {
        void onSelectedListener(String UUID, String messageType);
    }


    public SectionsPagerAdapter(Context context, FragmentManager fm, onRowSelectedListener listener) {
        super(fm);
        mContext = context;
        this.mListener = listener;
    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new SelectSendToFriendsFragment(mContext, this);
                break;
            case 1:
                fragment = new SelectSendToFriendsStoryFragment(mContext, this);
                break;
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}