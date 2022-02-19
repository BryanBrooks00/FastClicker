package com.darwin.fastclicker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    ImageView ad_image;
    ImageView sound_image;
    TextView close_tv;
    ImageView space_image;
    ImageView baseball_image;
    ImageView cowboy_image;
    TextView space_price_tv;
    TextView baseball_price_tv;
    TextView cowboy_price_tv;
    int baseball_price = 350;
    int cowboy_price = 750;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        ad_image = view.findViewById(R.id.ad_image);
        sound_image = view.findViewById(R.id.sound_image);
        close_tv = view.findViewById(R.id.close_tv);
        space_image = view.findViewById(R.id.space_image);
        baseball_image = view.findViewById(R.id.baseball_image);
        cowboy_image = view.findViewById(R.id.cowboy_image);
        space_price_tv = view.findViewById(R.id.space_price_tv);
        baseball_price_tv = view.findViewById(R.id.baseball_price_tv);
        cowboy_price_tv = view.findViewById(R.id.cowboy_price_tv);
        onCompare();

        MainActivity mainActivity = (MainActivity)getActivity();
        space_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.changeThemeToSpace();
            }
        });

        baseball_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.changeThemeToBaseball();
            }
        });

        cowboy_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.changeThemeToCowboy();
            }
        });

        close_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 mainActivity.fragmentContainerView.setVisibility(View.INVISIBLE);
            }
        });

        sound_image.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               if (mainActivity.checkMute()) {
                                                   mainActivity.unmute();
                                                   sound_image.setImageDrawable(ContextCompat.getDrawable(mainActivity, R.drawable.sound));
                                               } else {
                                                   mainActivity.mute();
                                                   sound_image.setImageDrawable(ContextCompat.getDrawable(mainActivity, R.drawable.no_sound));
                                               }
                                           }
                                       });



        ad_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.showAd();
            }
        });
        return view;
    }

    public void onCompare() {
        int score = Integer.parseInt(Preferences.getRecord(getActivity()));
        if (score >= baseball_price) {
            baseball_image.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.withoutColor));
            baseball_price_tv.setText("");
            baseball_image.setClickable(true);
            baseball_image.setEnabled(true);
        } else {
            baseball_price_tv.setText(baseball_price + "");
            baseball_image.setClickable(false);
            baseball_image.setEnabled(false);
        }
        if (score >= cowboy_price){
            cowboy_image.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.withoutColor));
            cowboy_price_tv.setText("");
            cowboy_image.setClickable(true);
            cowboy_image.setEnabled(true);
        } else {
            cowboy_price_tv.setText(cowboy_price + "");
            cowboy_image.setClickable(false);
            cowboy_image.setEnabled(false);
        }
    }
}