package sk.alcoino.android.alcoino;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SearchAdapter extends ArrayAdapter<android.bluetooth.BluetoothDevice> {

    public SearchAdapter(Context context, List<android.bluetooth.BluetoothDevice> devices) {
        super(context, 0, devices);
    }

    // returns the list of items

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.search_list_item, parent, false);
        }

        BluetoothDevice currentDevice = getItem(position);

        TextView deviceName = (TextView) listItemView.findViewById(R.id.device_name);
        deviceName.setText(currentDevice.getName());

        TextView deviceAddress = (TextView) listItemView.findViewById(R.id.device_address);
        deviceAddress.setText(currentDevice.getAddress());

        ImageView deviceClass = (ImageView) listItemView.findViewById(R.id.image_of_device);
        if (currentDevice.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.COMPUTER_DESKTOP) {
            deviceClass.setImageResource(R.drawable.ic_desktop_windows_black_24dp);
        }else if (currentDevice.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.COMPUTER_LAPTOP) {
            deviceClass.setImageResource(R.drawable.ic_laptop_black_24dp);
        }else if (currentDevice.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.PHONE_SMART) {
            deviceClass.setImageResource(R.drawable.ic_phone_android_black_24dp);
        }else {
            deviceClass.setImageResource(R.drawable.ic_devices_other_black_24dp);
        }
        return listItemView;
    }
}
