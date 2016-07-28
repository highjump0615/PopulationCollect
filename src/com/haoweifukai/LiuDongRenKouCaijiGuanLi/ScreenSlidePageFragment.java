
package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.CommonUtils;

public class ScreenSlidePageFragment extends Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_IMAGE_URI = "image_uri";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_IMAGE_URI}.
     */
    private String mImageUri;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ScreenSlidePageFragment create(String imageUri) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URI, imageUri);
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUri = getArguments().getString(ARG_IMAGE_URI);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_screen_slide_page, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.image_enhance);
        //imageView.setImageURI(Uri.parse(mImageUri));
        //imageView.setImageBitmap(CommonUtils.adjustBitmap(Uri.parse(mImageUri), 1));
        imageView.setImageBitmap(CommonUtils.adjustBitmap(Uri.parse(mImageUri)));

        return rootView;
    }

    /**
     * Returns the image uri of page represented by this fragment object.
     */
    public String getImageUri() {
        return mImageUri;
    }

}
