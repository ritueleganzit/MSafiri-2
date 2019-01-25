package com.eleganz.msafiri;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LocationSearch extends AppCompatActivity {
    Toolbar mToolbar;
    ArrayAdapter mAdapter;
    ImageView search_back;
    ListView mListView;
    TextView mEmptyView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_location_search);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mListView = (ListView) findViewById(R.id.list);
        search_back = (ImageView) findViewById(R.id.search_back);
        mEmptyView = (TextView) findViewById(R.id.emptyView);

        mAdapter = new ArrayAdapter(LocationSearch.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.months_array));
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent();
                intent.putExtra("editTextValue", ""+adapterView.getItemAtPosition(i).toString());
                setResult(RESULT_OK, intent);
                finish();

            }
        });


        search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mListView.setEmptyView(mEmptyView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem mSearch = menu.findItem(R.id.action_search);

        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");
        mSearchView.setIconifiedByDefault(false);
        mSearchView.onActionViewExpanded();
        mSearchView.findViewById(R.id.search_mag_icon).setVisibility(View.GONE);

        MenuItemCompat.setOnActionExpandListener(mSearch, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Toast.makeText(LocationSearch.this, "onMenuItemActionExpand called", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();

                return true;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }
}
