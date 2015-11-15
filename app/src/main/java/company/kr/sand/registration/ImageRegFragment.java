package company.kr.sand.registration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import company.kr.sand.R;

/**
 * Created by Prattler on 2015-11-04.
 */
public class ImageRegFragment extends Fragment {

    public static final int REQUEST_CODE_IMAGE=0;

    Button btn_image_reg;
    Button btn_next_image;
    RelativeLayout profile_image_back;
    Activity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.registration_image,container, false);

        activity=getActivity();
        btn_image_reg=(Button)view.findViewById(R.id.btn_image_reg_profile);
        btn_next_image=(Button)view.findViewById(R.id.btn_next_image);
        profile_image_back=(RelativeLayout)view.findViewById(R.id.img_profile_back);

        btn_image_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                ImageRegFragment.this.startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });

        return view;
    }


    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.println(requestCode);


        if (requestCode==196608 && resultCode == activity.RESULT_OK && data != null) {
            final Uri selectImageUri = data.getData();
            final String[] filePathColumn = {MediaStore.Images.Media.DATA};
            final Cursor imageCursor = activity.getContentResolver().query(selectImageUri, filePathColumn,
                    null, null, null);

            imageCursor.moveToFirst();

            final int columnIndex = imageCursor.getColumnIndex(filePathColumn[0]);
            final String imagePath = imageCursor.getString(columnIndex);

            imageCursor.close();
            final Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Drawable back_drawable = new BitmapDrawable(getResources(), bitmap);
            profile_image_back.setBackground(back_drawable);
        }
    }
}
