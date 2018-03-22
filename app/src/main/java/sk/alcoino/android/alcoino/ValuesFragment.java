package sk.alcoino.android.alcoino;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class ValuesFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private RadioButton radioButtonMale, radioButtonFemale;
    private Button button;
    private String gender;
    private EditText editText;

    public ValuesFragment() {
        // Required empty public constructor
    }

    public static ValuesFragment newInstance(String param1, String param2) {
        ValuesFragment fragment = new ValuesFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_values, container, false);

        radioButtonMale = (RadioButton) rootView.findViewById(R.id.male_radio);
        radioButtonFemale = (RadioButton) rootView.findViewById(R.id.female_radio);
        button = (Button) rootView.findViewById(R.id.send);
        editText = (EditText) rootView.findViewById(R.id.weight_input);

        radioButtonMale.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                radioButtonMale.setChecked(true);
                radioButtonFemale.setChecked(false);
                gender = "M";

            }
        });

        radioButtonFemale.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                radioButtonMale.setChecked(false);
                radioButtonFemale.setChecked(true);
                gender = "F";
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if ((!editText.getText().toString().equals("") && radioButtonMale.isChecked()) || (!editText.getText().toString().equals("") && radioButtonFemale.isChecked())) {
                    ((ControlActivity) getActivity()).sendCommand(gender + editText.getText().toString() + '\n');
                    Toast.makeText(getActivity(), "Data was successfully setted.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), "Error. You must fill every field!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return rootView;
    }

    // disconnect button in appbar methods

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    // help method used as view reference

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_disconnect) {
            ((ControlActivity) getActivity()).disconnect();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
