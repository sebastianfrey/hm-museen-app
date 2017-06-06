package edu.muenchnermuseen.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import edu.muenchnermuseen.R;
import edu.muenchnermuseen.activities.DetailActivity;
import edu.muenchnermuseen.entities.Museum;

/**
 * Created by sfrey on 06.06.2017.
 */

public class ScreenSlidePageFragment extends Fragment implements AdapterView.OnClickListener {

    ImageView imageView;
    Museum museum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null)
        {
            museum = (Museum) bundle.getSerializable("museum");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page,
                container, false);

        imageView = (ImageView) view.findViewById(R.id.image_slide);

        if (museum != null)
        {
            String resName = "museum_" + museum.getId().toString();

            int resId = getResources().getIdentifier(resName, "mipmap", getActivity().getPackageName());

            imageView.setImageResource(resId);
            imageView.setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("museum", museum);
        intent.putExtras(b);
        startActivity(intent);
    }
}
