package org.mik.perspectivedrawerexample;

import android.content.SharedPreferences;
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
        TileSources.setUpTileSource(mMapView, TileSources.Sources.GOOGLE_ORIGINAL, this);
        mMapView.getController().setZoom(14);
        mMapView.setMultiTouchControls(true);

        ListView listView = (ListView) findViewById(R.id.places);
        mDrawer = (PerspectiveDrawer) findViewById(R.id.drawer);

        mAdapter = new PlacesAdapter(this);

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(mPlaceClickListener);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        mCloseDrawer = pref.getBoolean("close_drawer", false);
        mDrawer.setDimmingEnabled(pref.getBoolean("dimming", true));

        if (savedInstanceState != null) {
            mMapView.getController().setCenter((org.osmdroid.api.IGeoPoint) savedInstanceState.getSerializable("map_center"));
        } else {
            mDrawer.open();
        }
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
        switch (id) {
            case R.id.action_close_drawer:
                item.setChecked(!item.isChecked());
                mCloseDrawer = item.isChecked();
                break;
            case R.id.action_dimming_enabled:
                item.setChecked(!item.isChecked());
                mDrawer.setDimmingEnabled(item.isChecked());
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("map_center", (java.io.Serializable) mMapView.getMapCenter());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean("close_drawer", mCloseDrawer);
        editor.putBoolean("dimming", mDrawer.isDimmingEnabled());
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isOpened()) {
            super.onBackPressed();
        } else {
            mDrawer.open();
        }
    }
}
