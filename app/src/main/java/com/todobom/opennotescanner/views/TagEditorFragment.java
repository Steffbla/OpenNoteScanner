package com.todobom.opennotescanner.views;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.todobom.opennotescanner.R;
import java.io.IOException;

/**
 * Created by allgood on 29/05/16.
 */
public class TagEditorFragment extends DialogFragment {

    private Runnable mRunOnDetach;
    private String filePath;

    private String[] stdTags = {"rocket", "gift", "tv", "bell", "game", "star", "magnet"};

    private ImageView[] stdTagsButtons = new ImageView[7];

    private boolean[] stdTagsState = new boolean[7];

    public TagEditorFragment() {
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View tagEditorView = inflater.inflate(R.layout.tageditor_view, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        stdTagsButtons[0] = tagEditorView.findViewById(R.id.buttonRocket);
        stdTagsButtons[1] = tagEditorView.findViewById(R.id.buttonGift);
        stdTagsButtons[2] = tagEditorView.findViewById(R.id.buttonTv);
        stdTagsButtons[3] = tagEditorView.findViewById(R.id.buttonBell);
        stdTagsButtons[4] = tagEditorView.findViewById(R.id.buttonGame);
        stdTagsButtons[5] = tagEditorView.findViewById(R.id.buttonStar);
        stdTagsButtons[6] = tagEditorView.findViewById(R.id.buttonMagnet);

        for ( int i=0 ; i<7 ; i++ ) {

            stdTagsButtons[i].setBackgroundTintList(ColorStateList.valueOf( stdTagsState[i] ? 0xFF00E676 : 0xFFa0a0a0 ));

            stdTagsButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getTagIndex(v);
                    stdTagsState[index] = !stdTagsState[index];
                    v.setBackgroundTintList(ColorStateList.valueOf( stdTagsState[index] ? 0xFF00E676 : 0xFFa0a0a0 ));
                }
            });
        }

        Button tagDoneButton = tagEditorView.findViewById(R.id.tag_done);
        tagDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTags();
                dismiss();
            }
        });

        return tagEditorView;
    }

    private int getTagIndex( View v ) {
        for ( int i=0 ; i<7 ; i++ ) {
            if (stdTagsButtons[i] == v) {
                return i;
            }
        }
        return -1;
    }

    private void loadTags() {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String userComment = exif.getAttribute("UserComment");
        for (int i=0; i<7 ; i++) {
            stdTagsState[i] = userComment.contains("<" + stdTags[i] + ">");
        }
    }

    private void saveTags() {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder userComment = new StringBuilder(exif.getAttribute("UserComment"));
        for (int i=0; i<7 ; i++) {
            if (stdTagsState[i] && !userComment.toString().contains("<" + stdTags[i] + ">")) {
                userComment.append("<").append(stdTags[i]).append(">");
            } else if (!stdTagsState[i] && userComment.toString().contains("<" + stdTags[i] + ">")) {
                userComment.toString().replaceAll("<" + stdTags[i] + ">", "");
            }
        }
        exif.setAttribute("UserComment", userComment.toString());
        try {
            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mRunOnDetach != null) {
            mRunOnDetach.run();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    public void setRunOnDetach( Runnable runOnDetach ) {
        mRunOnDetach = runOnDetach;
    }


    public void setFilePath(String filePath) {
        this.filePath = filePath;
        loadTags();
    }
}
