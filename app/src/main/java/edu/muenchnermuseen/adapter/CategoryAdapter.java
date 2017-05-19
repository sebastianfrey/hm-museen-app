package edu.muenchnermuseen.adapter;

/**
 * Created by Sebastian on 18.05.2017.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.muenchnermuseen.R;
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

    final Category category = this.categories.get(position);

    // 2
    if (convertView == null) {
      final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
      convertView = layoutInflater.inflate(R.layout.category_item, null);
    }

    final ImageView imageView = (ImageView) convertView.findViewById(R.id.imageview_category_thumb);
    final TextView textView = (TextView) convertView.findViewById(R.id.textview_category_name);

    int imageId;

    switch (category.getId())
    {
      case 0:
        imageId = R.mipmap.ic_category_technology;
        break;

      case 1:
        imageId = R.mipmap.ic_category_history;
        break;

      case 2:
        imageId = R.mipmap.ic_category_nature;
        break;

      case 3:
        imageId = R.mipmap.ic_category_art;
        break;

      default:
        imageId = R.mipmap.ic_img_not_found;
    }

    imageView.setImageResource(imageId);
    textView.setText(category.getName());

    return convertView;
  }

}