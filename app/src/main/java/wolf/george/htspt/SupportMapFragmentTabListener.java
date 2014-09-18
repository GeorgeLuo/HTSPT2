package wolf.george.htspt;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;

public class SupportMapFragmentTabListener<T extends Fragment> implements ActionBar.TabListener {
    private Fragment mFragment;
    private final MainActivity mActivity;
    private final String mTag;
    private final Class<T> mClass;
    private final int mfragmentContainerId;

    public SupportMapFragmentTabListener(MainActivity activity, String tag, Class<T> clz) {
        mActivity = activity;
        mTag = tag;
        mClass = clz;
        mfragmentContainerId = android.R.id.content;
    }

    public SupportMapFragmentTabListener(int fragmentContainerId, MainActivity activity, String tag, Class<T> clz) {
        mActivity = activity;
        mTag = tag;
        mClass = clz;
        mfragmentContainerId = fragmentContainerId;
    }

	/* The following are each of the ActionBar.TabListener callbacks */

    public void onTabSelected(Tab tab, FragmentTransaction sft) {
        // Check if the fragment is already initialized
        MainActivity.currentTab = "PostCollegeBoard";
        if (mFragment == null) {
            // If not, instantiate and add it to the activity
            mFragment = Fragment.instantiate(mActivity, mClass.getName());
            sft.add(mfragmentContainerId, mFragment, mTag);
            mActivity.setupSendMessage("mapRequest/%/Charlottesville");
           // mActivity.setupSendMessage("collegeBoardRequest/%/Charlottesville");
            mActivity.changeFeedType("main_feed");
        } else {
            MainActivity.currentTab = "PostCollegeBoard";
            // If it exists, simply attach it in order to show it
            sft.attach(mFragment);
            mActivity.sendMessage("mapRequest/%/Charlottesville");
            mActivity.changeFeedType("main_feed");
        }
    }

    public void onTabUnselected(Tab tab, FragmentTransaction sft) {
        if (mFragment != null) {
            mActivity.setLastFragment(2);
            // Detach the fragment, because another one is being attached
            sft.detach(mFragment);
        }
    }

    public void onTabReselected(Tab tab, FragmentTransaction sft) {
        // User selected the already selected tab. Usually do nothing.
    }
}