package company.kr.sand.registration;


import android.os.Bundle;
import company.kr.sand.R;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;




/**
 * Created by Prattler on 2015-11-04.
 */
public class NickRegFragment extends Fragment {

    public NickRegFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.registration_nick, container,false);

        return view;
    }

}
