package edu.muenchnermuseen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.muenchnermuseen.R;
import edu.muenchnermuseen.entities.Category;
import edu.muenchnermuseen.entities.Museum;

/**
 * Created by sfrey on 20.05.2017.
 */

public class MuseumAdapter extends BaseAdapter {

    List<Museum> museums;
    Context context;

    public MuseumAdapter(Context context, List<Museum> museums) {
        this.context = context;
        this.museums = museums;
    }

    @Override
    public int getCount() {
        return museums.size();
    }

    @Override
    public Object getItem(int position) {
        return museums.get(position);
    }

    @Override
    public long getItemId(int position) {
        return museums.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Museum museum = (Museum) getItem(position);

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.list_item_museum, null);
        }

        final ImageView thumb = (ImageView) convertView.findViewById(R.id.museum_thumb);
        final TextView title = (TextView) convertView.findViewById(R.id.museum_title);
        final TextView description = (TextView) convertView.findViewById(R.id.museum_description);

        String resName = "museum_" + museum.getId().toString();

        int resId = context.getResources().getIdentifier(resName, "mipmap", context.getPackageName());

        thumb.setImageResource(resId);
        title.setText(museum.getName());
        description.setText(museum.getDescription());

        return convertView;
    }
}
