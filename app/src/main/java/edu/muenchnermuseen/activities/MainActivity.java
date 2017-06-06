package edu.muenchnermuseen.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.muenchnermuseen.R;
import edu.muenchnermuseen.adapter.CategoryAdapter;
import edu.muenchnermuseen.adapter.ScreenSlidePagerAdapter;
import edu.muenchnermuseen.db.DataBaseHelper;
import edu.muenchnermuseen.db.dao.CategoryDAO;
import edu.muenchnermuseen.db.dao.MuseumDAO;
import edu.muenchnermuseen.entities.Category;
import edu.muenchnermuseen.entities.Museum;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener,
                    SearchView.OnSuggestionListener, AdapterView.OnClickListener {

    private static int SLIDE_DELAY = 4 * 1000;

    DataBaseHelper db;

    CategoryDAO categoryDAO;
    MuseumDAO museumDAO;

    ViewPager pager;
    SearchView searchView;
    MenuItem searchMenuItem;

    Handler handler;
    Runnable autoSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
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

        pager = (ViewPager) findViewById(R.id.pager);
        ScreenSlidePagerAdapter adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), museumDAO.getMuseums());
        pager.setAdapter(adapter);

        handler = new Handler();
        autoSlider = new Runnable() {

            @Override
            public void run() {
                int total = pager.getAdapter().getCount();
                int current = pager.getCurrentItem();

                int position = 0;

                if (current < total - 1)
                {
                    position = pager.getCurrentItem() + 1;
                }

                pager.setCurrentItem(position, true);

                handler.postDelayed(this, SLIDE_DELAY);
            }
        };
        handler.postDelayed(autoSlider, SLIDE_DELAY);

        GridView categoryView = (GridView) findViewById(R.id.category_grid);
        List<Category> categories = categoryDAO.getCategories();
        Collections.sort(categories, new Comparator<Category>() {
            @Override
            public int compare(Category o1, Category o2) {
                return o1.getId() - o2.getId();
            }
        });
        CategoryAdapter categoryAdapter = new CategoryAdapter(this, categories);
        categoryView.setAdapter(categoryAdapter);

        categoryView.setOnItemClickListener(this);
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (handler != null && autoSlider != null)
        {
            handler.removeCallbacks(autoSlider);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null && autoSlider != null)
        {
            handler.removeCallbacks(autoSlider);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (handler != null && autoSlider != null)
        {
            handler.postDelayed(autoSlider, SLIDE_DELAY);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CategoryAdapter adapter = (CategoryAdapter) parent.getAdapter();
        Category category = (Category) adapter.getItem(position);

        Intent intent = new Intent(this, MuseumActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("category", category);
        intent.putExtras(b);
        startActivity(intent);
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
        getMenuInflater().inflate(R.menu.main, menu);

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
/*
        return false;
*/
//         Handle action bar item clicks here. The action bar will
//         automatically handle clicks on the Home/Up button, so long
//         as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        }

        if (categoryId > -1)
        {
            Category category = categoryDAO.getCategory(categoryId);
            intent = new Intent(this, MuseumActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("category", category);
            intent.putExtras(b);
        }

        if (intent != null)
        {
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        int total = pager.getAdapter().getCount();
        int current = pager.getCurrentItem();
        switch (id)
        {
            case R.id.slide_left:

                if (current > 0)
                {
                    handler.removeCallbacks(autoSlider);
                    handler.postDelayed(autoSlider, SLIDE_DELAY);
                    pager.setCurrentItem(pager.getCurrentItem() - 1, true);
                }

                break;

            case R.id.slide_right:

                if (current < total)
                {
                    handler.removeCallbacks(autoSlider);
                    handler.postDelayed(autoSlider, SLIDE_DELAY);
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                }

                break;
        }
    }
}
