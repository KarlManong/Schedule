package io.github.abc549825.schedule;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import io.github.abc549825.schedule.beans.ConfigDetail;
import io.github.abc549825.schedule.tools.DBOpenHelper;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends ListFragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private RuntimeExceptionDao<ConfigDetail, Integer> mDao;


        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            DBOpenHelper mHelper = OpenHelperManager.getHelper(this.getActivity(), DBOpenHelper.class);
            mDao = mHelper.getConfigDetailDao();

            setListAdapter(new ArrayAdapter<ConfigDetail>(this.getActivity(), android.R.layout.simple_list_item_checked, mDao.queryForAll()));

            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            setEmptyText("空");
            ListView listView = getListView();
            registerForContextMenu(listView);
            setHasOptionsMenu(true);

            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    menu.add(0, Menu.FIRST, Menu.FIRST, "删除");
                    menu.add(0, 2, 2, "启用");
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @SuppressWarnings("unchecked")
                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    ArrayAdapter<ConfigDetail> adapter = ((ArrayAdapter<ConfigDetail>) getListAdapter());
                    switch (item.getItemId()) {
                        case Menu.FIRST:
                            SparseBooleanArray ids = getListView().getCheckedItemPositions();
                            for (int i = 0; i < ids.size(); i++) {
                                if (ids.valueAt(i)) {
                                    int position = ids.keyAt(i);
                                    ConfigDetail detail = adapter.getItem(position);
                                    mDao.delete(detail);
                                }
                            }
                            PlaceholderFragment.this.refreshDataSet();
                            mode.finish();
                            return true;
                        case 2:
                            mode.finish();
                            return false;
                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_new:
                    mDao.create(new ConfigDetail());
                    refreshDataSet();
                    return true;
            }

            return super.onOptionsItemSelected(item);
        }

        @SuppressWarnings("unchecked")
        private void refreshDataSet() {
            ArrayAdapter<ConfigDetail> adapter = (ArrayAdapter<ConfigDetail>) getListAdapter();
            adapter.clear();
            adapter.addAll(mDao.queryForAll());
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, Menu.FIRST, Menu.FIRST, "删除");
        }

        @Override
        public boolean onContextItemSelected(MenuItem item) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            final ConfigDetail detail = (ConfigDetail) getListAdapter().getItem(info.position);
            if (item.getItemId() == Menu.FIRST) {
                new AlertDialog.Builder(this.getActivity()).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDao.delete(detail);
                        PlaceholderFragment.this.refreshDataSet();
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();
                return true;
            }

            return super.onContextItemSelected(item);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            unregisterForContextMenu(getListView());
            if (mDao != null) {
                mDao = null;
                OpenHelperManager.releaseHelper();
            }
        }

    }

}
