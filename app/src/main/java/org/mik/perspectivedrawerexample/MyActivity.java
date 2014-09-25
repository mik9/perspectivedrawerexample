package org.mik.perspectivedrawerexample;

import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import org.mik.perspectivedrawer.PerspectiveDrawer;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class MyActivity extends ActionBarActivity {

    private AdapterView.OnItemClickListener mPlaceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Place place = mAdapter.getItem(position);
            mMapView.getController().animateTo(new GeoPoint(place.getLat(), place.getLng()));
            if (mCloseDrawer) {
                mDrawer.close();
            }
        }
    };
    private PlacesAdapter mAdapter;
    private MapView mMapView;
    private boolean mCloseDrawer;
    private PerspectiveDrawer mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mMapView = (MapView) findViewById(R.id.map_view);
        mMapView.getController().setZoom(14);
        mMapView.setMultiTouchControls(true);

        ListView listView = (ListView) findViewById(R.id.places);
        mDrawer = (PerspectiveDrawer) findViewById(R.id.drawer);
        mDrawer.open();

        mAdapter = new PlacesAdapter(this);

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(mPlaceClickListener);

        mCloseDrawer = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("close_drawer", false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        MenuItem menuItem = menu.findItem(R.id.action_close_drawer);
        menuItem.setChecked(mCloseDrawer);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close_drawer) {
            item.setChecked(!item.isChecked());
            mCloseDrawer = item.isChecked();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("close_drawer", mCloseDrawer).commit();
    }
}
