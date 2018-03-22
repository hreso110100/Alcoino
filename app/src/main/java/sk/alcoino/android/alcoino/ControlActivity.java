package sk.alcoino.android.alcoino;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import java.io.IOException;
import java.util.UUID;

public class ControlActivity extends AppCompatActivity {

    private String address;
    private ProgressDialog progress;
    private boolean isBtConnected = false;
    BluetoothSocket btSocket;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }
        Intent intent = getIntent();
        address = intent.getStringExtra(SearchActivity.EXTRA_ADDRESS);

        setContentView(R.layout.activity_control);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewAdapter adapter = new ViewAdapter(this, getFragmentManager());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        new ConnectBT().execute();
    }

    // async thread for safety connecting to Arduino

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ControlActivity.this, "Connecting...", "Please wait...");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = bluetoothAdapter.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Try again");
                finish();
            } else {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    // disconnecting from device

    public void disconnect() {
        if (btSocket != null) {
            try {
                btSocket.close();
                msg("Disconnected.");
            } catch (IOException e) {
                msg("Cannot disconnect.");
            }
        }
        finish();
    }

    // toast method

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    // method for sending commands to Arduino

    public void sendCommand(String command) {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(command.getBytes());
            } catch (IOException e) {
                msg("Cannot accept command");
            }
        }
    }
}
