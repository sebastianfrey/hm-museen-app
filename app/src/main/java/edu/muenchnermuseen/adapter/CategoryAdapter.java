package edu.muenchnermuseen.adapter;

/**
 * Created by Sebastian on 18.05.2017.
 */
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import edu.muenchnermuseen.db.entities.Category;

public class CategoryAdapter extends BaseAdapter {

  private final Context mContext;
  private final List<Category> categories;

  // 1
  public CategoryAdapter(Context context, List<Category> categories) {
    this.mContext = context;
    this.categories = categories;
  }

  // 2
  @Override
  public int getCount() {
    return categories.size();
  }

  // 3
  @Override
  public long getItemId(int position) {
    return 0;
  }

  // 4
  @Override
  public Object getItem(int position) {
    return null;
  }

  // 5
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    TextView dummyTextView = new TextView(mContext);
    dummyTextView.setText(String.valueOf(position));
    return dummyTextView;
  }

}