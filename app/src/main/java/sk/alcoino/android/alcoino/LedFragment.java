package sk.alcoino.android.alcoino;

import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;

import java.io.IOException;

public class LedFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private CircleMenu circleMenu;
    private ValuesFragment valuesFragment;
    BluetoothSocket btSocket;

    public LedFragment() {
        // Required empty public constructor
    }

    public static LedFragment newInstance(String param1, String param2) {
        LedFragment fragment = new LedFragment();
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

        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_led, container, false);

        circleMenu = (CircleMenu) rootView.findViewById(R.id.circle_menu);

        circleMenu.setMainMenu(Color.parseColor("#ef6c00"), R.drawable.ic_format_paint_black_24dp, R.drawable.ic_close_black_24dp);
        circleMenu.addSubMenu(Color.parseColor("#d50000"), R.drawable.ic_mode_edit_black_24dp)
                .addSubMenu(Color.parseColor("#30A400"), R.drawable.ic_mode_edit_black_24dp)
                .addSubMenu(Color.parseColor("#258CFF"), R.drawable.ic_mode_edit_black_24dp)
                .addSubMenu(Color.parseColor("#5c007a"), R.drawable.ic_mode_edit_black_24dp)
                .addSubMenu(Color.parseColor("#ffb300"), R.drawable.ic_mode_edit_black_24dp);

        circleMenu.setOnMenuSelectedListener(new OnMenuSelectedListener() {

            @Override
            public void onMenuSelected(int index) {
                switch (index) {
                    case 0:
                        ((ControlActivity)getActivity()).sendCommand("R");
                        Toast.makeText(getActivity(), "Color changed successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        ((ControlActivity)getActivity()).sendCommand("G");
                        Toast.makeText(getActivity(), "Color changed successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        ((ControlActivity)getActivity()).sendCommand("B");
                        Toast.makeText(getActivity(), "Color changed successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        ((ControlActivity)getActivity()).sendCommand("P");
                        Toast.makeText(getActivity(), "Color changed successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        ((ControlActivity)getActivity()).sendCommand("Y");
                        Toast.makeText(getActivity(), "Color changed successfully", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        circleMenu.setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {

            @Override
            public void onMenuOpened() {

            }

            @Override
            public void onMenuClosed() {
            }
        });
        return rootView;
    }

    // disconnect button in appbar methods

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu,menuInflater);
    }

    // help method used as view reference

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_disconnect) {
            ((ControlActivity)getActivity()).disconnect();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
