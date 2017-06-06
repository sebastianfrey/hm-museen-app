package edu.muenchnermuseen.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;

import edu.muenchnermuseen.R;
import edu.muenchnermuseen.adapter.MuseumAdapter;
import edu.muenchnermuseen.db.DataBaseHelper;
import edu.muenchnermuseen.db.dao.CategoryDAO;
import edu.muenchnermuseen.db.dao.MuseumDAO;
import edu.muenchnermuseen.entities.Category;
import edu.muenchnermuseen.entities.Museum;

public class MuseumActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener,
        SearchView.OnSuggestionListener {

    DataBaseHelper db;

    CategoryDAO categoryDAO;
    MuseumDAO museumDAO;

    SearchView searchView;
    MenuItem searchMenuItem;
    ListView museumView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DataBaseHelper(this);
        categoryDAO = new CategoryDAO(db);
        museumDAO = new MuseumDAO(db);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);


        Bundle b = getIntent().getExtras();
        Category category = null;
        if(b != null) {
           category = (Category) b.getSerializable("category");
        }

        List<Museum> museums = null;

        if (category != null)
        {
            museums = museumDAO.getMuseumsByCategory(category.getId());
        }
        else
        {
            museums = museumDAO.getMuseums();
        }

        museumView = (ListView) findViewById(R.id.museum_list);
        MuseumAdapter museumAdapter = new MuseumAdapter(this, museums);
        museumView.setAdapter(museumAdapter);
        museumView.setOnItemClickListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.museum, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnSuggestionListener(this);

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MuseumAdapter adapter = (MuseumAdapter) parent.getAdapter();
        Museum museum = (Museum) adapter.getItem(position);

        Intent intent = new Intent(this, DetailActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("museum", museum);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSuggestionSelect(int position)
    {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        CursorAdapter c = searchView.getSuggestionsAdapter();

        Cursor cur = c.getCursor();
        cur.moveToPosition(position);
        int id = cur.getInt(cur.getColumnIndex(BaseColumns._ID));

        Museum museum = museumDAO.getMuseumById(id);

        if (museum != null)
        {
            searchMenuItem.collapseActionView();
            searchView.clearFocus();
            Intent intent = new Intent(this, DetailActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("museum", museum);
            intent.putExtras(b);
            startActivity(intent);
        }

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent = null;
        int id = item.getItemId();
        int categoryId = -1;

        switch (id)
        {
            case R.id.nav_category_technology:
                categoryId = 0;
                break;

            case R.id.nav_category_history:
                categoryId = 1;
                break;

            case R.id.nav_category_nature:
                categoryId = 2;
                break;

            case R.id.nav_category_art:
                categoryId = 3;
                break;

            case R.id.nav_map:
                intent = new Intent(this, MapsActivity.class);
                break;

            case R.id.nav_home:
                intent = new Intent(this, MainActivity.class);
                break;
        }


        if (categoryId > -1)
        {
            List<Museum> museums = museumDAO.getMuseumsByCategory(categoryId);
            MuseumAdapter museumAdapter = new MuseumAdapter(this, museums);
            museumView.setAdapter(museumAdapter);
        }

        if (intent != null)
        {
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
