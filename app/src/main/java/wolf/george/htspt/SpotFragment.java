package wolf.george.htspt;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SpotFragment extends ListFragment {
    static ArrayAdapter<String> adapter;
    MainActivity activity;
    private static ListView listview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        activity.getSupportActionBar().show();
        Log.d("SpotFragment", "Created");

        adapter = new ArrayAdapter<String>(
                inflater.getContext(), android.R.layout.simple_list_item_1,MainActivity.postBoard
        );
        setListAdapter(adapter);
        // super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    @Override
    public void onListItemClick(ListView list, View v, int position, long id) {

        String clickedSpot = getListView().getItemAtPosition(position).toString();
        Log.d("List Item Clicked", clickedSpot);
        activity.getSupportActionBar().setSelectedNavigationItem(1);
        activity.sendMessage("getSpotBoard/%/" + "Charlottesville" + "/%/" + clickedSpot);
    }
}
