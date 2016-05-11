package catchla.yep.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import catchla.yep.R;
import catchla.yep.model.Topic;

/**
 * Created by mariotaku on 15/12/6.
 */
public class TopicsMenuFragment extends FloatingActionMenuFragment implements AdapterView.OnItemSelectedListener {
    private Spinner mOrderSpinner;

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<Entry> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add(new Entry(Topic.SortOrder.DEFAULT, getString(R.string.sort_order_default)));
        adapter.add(new Entry(Topic.SortOrder.DISTANCE, getString(R.string.distance)));
        adapter.add(new Entry(Topic.SortOrder.TIME, getString(R.string.time)));
        mOrderSpinner.setAdapter(adapter);
        mOrderSpinner.setOnItemSelectedListener(this);
        final String sortOrder = mPreferences.getString(KEY_TOPICS_SORT_ORDER, null);
        for (int i = 0, j = adapter.getCount(); i < j; i++) {
            if (adapter.getItem(i).sortBy.equals(sortOrder)) {
                mOrderSpinner.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onBaseViewCreated(final View view, final Bundle savedInstanceState) {
        super.onBaseViewCreated(view, savedInstanceState);
        mOrderSpinner = (Spinner) view.findViewById(R.id.order_spinner);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_topics_floating_menu, container, false);
    }

    @Override
    public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
        TopicsListFragment fragment = (TopicsListFragment) getBelongsTo();
        if (fragment == null) return;
        fragment.reloadWithSortOrder(((Entry) mOrderSpinner.getItemAtPosition(position)).sortBy);
    }

    @Override
    public void onNothingSelected(final AdapterView<?> parent) {

    }

    static class Entry {

        private final String sortBy, title;

        public Entry(final String sortBy, final String title) {
            this.sortBy = sortBy;
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
