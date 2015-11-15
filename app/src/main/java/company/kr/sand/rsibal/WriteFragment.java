package company.kr.sand.rsibal;

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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import company.kr.sand.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class WriteFragment extends Fragment {

    public static final int HIGH = 2;
    public static final int MIDDLE = 1;
    public static final int LOW = 0;

    public static final int REQUEST_CODE_IMAGE = 0;

    View view;
    Button btn_image_reg;
    //Button btn_next_image;
    RelativeLayout food_image_back;
    Activity activity;
    RadioGroup rg_taste, rg_quantity, rg_performance;
    int cur_taste, cur_quantity, cur_performance;

    public WriteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cur_taste = LOW;
        cur_quantity = LOW;
        cur_performance = LOW;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.write_fragment, container, false);
        setView();
        setViewAction();

        return view;
    }

    private void setView() {

        btn_image_reg = (Button) view.findViewById(R.id.btn_image_reg_food);
        food_image_back = (RelativeLayout) view.findViewById(R.id.img_food_back);
        rg_taste = (RadioGroup) view.findViewById(R.id.rg_taste);
        rg_quantity = (RadioGroup) view.findViewById(R.id.rg_quantity);
        rg_performance = (RadioGroup) view.findViewById(R.id.rg_performance);


    }

    private void setViewAction() {

        rg_taste.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i) {
                    case R.id.btn_taste_low:
                        cur_taste = LOW;
                        break;
                    case R.id.btn_taste_middle:
                        cur_taste = MIDDLE;
                        break;
                    case R.id.btn_taste_high:
                        cur_taste = HIGH;
                        break;

                }
            }
        });

        rg_quantity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i) {
                    case R.id.btn_quantity_low:
                        cur_quantity = LOW;
                        break;
                    case R.id.btn_quantity_middle:
                        cur_quantity = MIDDLE;
                        break;
                    case R.id.btn_quantity_high:
                        cur_quantity = HIGH;
                        break;

                }
            }
        });

        rg_performance.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i) {
                    case R.id.btn_peformance_low:
                        cur_performance = LOW;
                        break;
                    case R.id.btn_performance_middle:
                        cur_performance = MIDDLE;
                        break;
                    case R.id.btn_performance_high:
                        cur_performance = HIGH;
                        break;

                }
            }
        });


        btn_image_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        activity = getActivity();

    }


    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMAGE && resultCode == activity.RESULT_OK && data != null) {
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
            food_image_back.setBackground(back_drawable);
        }
    }
}
