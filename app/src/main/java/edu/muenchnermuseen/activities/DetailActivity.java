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
import android.util.Log;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import edu.muenchnermuseen.R;
import edu.muenchnermuseen.db.DataBaseHelper;
import edu.muenchnermuseen.db.dao.CategoryDAO;
import edu.muenchnermuseen.db.dao.MuseumDAO;
import edu.muenchnermuseen.entities.Category;
import edu.muenchnermuseen.entities.Museum;

public class DetailActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnClickListener,
        SearchView.OnSuggestionListener {

  DataBaseHelper db;

  MuseumDAO museumDAO;
  CategoryDAO categoryDAO;

  private Museum museum;

  SearchView searchView;
  MenuItem searchMenuItem;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    db = new DataBaseHelper(this);
    museumDAO = new MuseumDAO(db);
    categoryDAO = new CategoryDAO(db);

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setItemIconTintList(null);
    navigationView.setNavigationItemSelectedListener(this);


    Button gotoButton = (Button) findViewById(R.id.goto_btn);
    gotoButton.setOnClickListener(this);

    Bundle b = getIntent().getExtras();

    if(b != null) {
      museum = (Museum) b.getSerializable("museum");
    }

    if (museum != null)
    {
      ImageView thumb = (ImageView) findViewById(R.id.detail_thumb);
      TextView header = (TextView) findViewById(R.id.detail_header);
      TextView description = (TextView) findViewById(R.id.detail_description);

      String resName = "museum_" + museum.getId().toString();
      int resId = this.getResources().getIdentifier(resName, "mipmap", this.getPackageName());

      thumb.setImageResource(resId);
      header.setText(museum.getName());
      description.setText(museum.getDescription());
    }
  }


  @Override
  public void setIntent(Intent newIntent)
  {
    super.setIntent(newIntent);
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
    getMenuInflater().inflate(R.menu.detail, menu);

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
  public void onClick(View v) {
    Intent intent = new Intent(this, MapsActivity.class);
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
    int id = item.getItemId();
    int categoryId = -1;

    switch (id)
    {
      case R.id.nav_home:
        startActivity(new Intent(this, MainActivity.class));
        break;

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

    }

    if (categoryId > -1)
    {
      Category category = categoryDAO.getCategory(categoryId);
      Intent intent = new Intent(this, MuseumActivity.class);
      Bundle b = new Bundle();
      b.putSerializable("category", category);
      intent.putExtras(b);
      startActivity(intent);
    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }
}
