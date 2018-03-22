package sk.alcoino.android.alcoino;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice currentDevice;
    private ListView listPaired, listDiscovered;
    private SearchAdapter searchAdapter;
    private List<android.bluetooth.BluetoothDevice> newDevices;
    private List<android.bluetooth.BluetoothDevice> newDiscoveredDevices;
    private View loadingIndicator;
    private TextView emptyView;
    private FloatingActionButton fab;
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        newDevices = new LinkedList<>();
        newDiscoveredDevices = new LinkedList<>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        listPaired = (ListView) findViewById(R.id.list_paired);
        listDiscovered = (ListView) findViewById(R.id.list_discover);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(broadcastReceiver2, filter);

        listDiscovered.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                BluetoothDevice currentDevice = searchAdapter.getItem(position);
                if (currentDevice != null) {
                    currentDevice.createBond();
                }
            }
        });

        listPaired.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String address = adapterView.getItemAtPosition(position).toString().trim();
                Intent i = new Intent(SearchActivity.this, ControlActivity.class);
                i.putExtra(EXTRA_ADDRESS, address);
                startActivity(i);
            }
        });
        emptyView = (TextView) findViewById(R.id.empty_view);

        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "BluetoothDevice does not support bluetooth", Toast.LENGTH_LONG).show();
            finish();
        } else if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        fab = (FloatingActionButton) findViewById(R.id.float_button);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingIndicator.setVisibility(View.VISIBLE);
                btnDiscover();
            }
        });
        listPairedDevices();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        listPairedDevices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(broadcastReceiver);
    }

    // display list of paired devices

    private List<BluetoothDevice> listPairedDevices() {
        Set<android.bluetooth.BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (android.bluetooth.BluetoothDevice device : pairedDevices) {
                newDevices.add(device);
            }
        }
        searchAdapter = new SearchAdapter(this, newDevices);
        listPaired.setAdapter(searchAdapter);

        return newDevices;
    }

    // search for new available devices

    public void btnDiscover() {
        fab.setEnabled(false);

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(broadcastReceiver, filter);
            checkBTPermissions();
            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver, discoverDevicesIntent);
        }
        if (!bluetoothAdapter.isDiscovering()) {
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(broadcastReceiver, filter);
            checkBTPermissions();
            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver, discoverDevicesIntent);
        }
    }

    // BroadCastReceiver of newly discovered devices

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(android.bluetooth.BluetoothDevice.ACTION_FOUND)) {
                android.bluetooth.BluetoothDevice device = intent.getParcelableExtra(android.bluetooth.BluetoothDevice.EXTRA_DEVICE);

                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    emptyView.setVisibility(View.GONE);
                    newDiscoveredDevices.add(device);
                    searchAdapter = new SearchAdapter(context, newDiscoveredDevices);
                    listDiscovered.setAdapter(searchAdapter);
                }

            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                loadingIndicator.setVisibility(View.GONE);
                fab.setEnabled(true);

                if (searchAdapter.getCount() == 0) {
                    listDiscovered.setEmptyView(emptyView);

                }
            }
        }
    };

    // BroadcastReceiver of paired devices

    private BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                android.bluetooth.BluetoothDevice device = intent.getParcelableExtra(android.bluetooth.BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    newDevices.add(device);
                    searchAdapter = new SearchAdapter(getApplicationContext(), newDevices);
                    listPaired.setAdapter(searchAdapter);
                    newDiscoveredDevices.remove(device);
                    searchAdapter = new SearchAdapter(getApplicationContext(), newDiscoveredDevices);
                    listDiscovered.setAdapter(searchAdapter);

                    if (searchAdapter.getCount() == 0) {
                        listDiscovered.setEmptyView(emptyView);
                    }
                } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d("OK", "CALLBACK RECIEVED: Not Bonded");
                } else if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d("OK", "CALLBACK RECIEVED: Trying to bond");
                }
            }
        }
    };

    // method for checking required permissions for Android version 6.0 and higher

    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }
    }
}
