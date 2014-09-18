package wolf.george.htspt;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class LocalFragment extends ListFragment {
    static ArrayAdapter<String> adapter;
    MainActivity activity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();

        adapter = new ArrayAdapter<String>(
                inflater.getContext(), android.R.layout.simple_list_item_1,MainActivity.collegeBoard
        );
        setListAdapter(adapter);
        // super.onCreateView(inflater, container, savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
