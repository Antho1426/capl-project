package ch.epfl.mobots.capl_framework.ui;
/*
 * Author: Martin Woolley
 * Twitter: @bluetooth_mdw
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
import java.util.ArrayList;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import ch.epfl.mobots.capl_framework.Constants;
import ch.epfl.mobots.capl_framework.MicroBit;
import ch.epfl.mobots.capl_framework.R;
//import com.bluetooth.mwoolley.microbitbledemo.R;
import ch.epfl.mobots.capl_framework.Settings;
import ch.epfl.mobots.capl_framework.Utility;
import ch.epfl.mobots.capl_framework.bluetooth.BleScanner;
import ch.epfl.mobots.capl_framework.bluetooth.BleScannerFactory;
import ch.epfl.mobots.capl_framework.bluetooth.ScanResultsConsumer;

public class DeviceListActivity extends AppCompatActivity implements ScanResultsConsumer {




    private static final int START_ACTIVITY = 1;
    private boolean DebugModeOn;


    private boolean ble_scanning = false;
    private Handler handler = new Handler();
    private ListAdapter ble_device_list_adapter;
    private BleScanner ble_scanner;
    private static final long SCAN_TIMEOUT = 30000;
    private static final int REQUEST_LOCATION = 0;
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION};
    private boolean permissions_granted=false;
    private static final String DEVICE_NAME_START = "BBC micro";
    private int device_count=0;
    private Toast toast;

    static class ViewHolder {
        public TextView text;
        public TextView bdaddr;
    }

    private Button scanButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_list);



        // Setting the chalkboard se font to the main button
        scanButton = (Button) findViewById(R.id.scanButton);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/chalkboardseregular.ttf");
        scanButton.setTypeface(type);



        setButtonText();
        getSupportActionBar().setTitle(R.string.screen_title_main);
        showMsg(Utility.htmlColorGreen("Ready"));

        Settings.getInstance().restore(this);

        ble_device_list_adapter = new ListAdapter();

        ListView listView = (ListView) this.findViewById(R.id.deviceList);
        listView.setAdapter(ble_device_list_adapter);

        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));

        ble_scanner = BleScannerFactory.getBleScanner(this.getApplicationContext());
        ble_scanner.setDevice_name_start(DEVICE_NAME_START);
        ble_scanner.setSelect_bonded_devices_only(true);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (ble_scanning) {
                    setScanState(false);
                    ble_scanner.stopScanning();
                }

                BluetoothDevice device = ble_device_list_adapter.getDevice(position);
                if (device.getBondState() == BluetoothDevice.BOND_NONE && Settings.getInstance().isFilter_unpaired_devices()) {
                    device.createBond();
                    showMsg(Utility.htmlColorRed("Selected micro:bit must be paired - pairing now"));
                    return;
                }
                try {
                    DeviceListActivity.this.unregisterReceiver(broadcastReceiver);
                } catch (Exception e) {
                    // ignore!
                }
                if (toast != null) {
                    toast.cancel();
                }
                MicroBit microbit = MicroBit.getInstance();
                microbit.setBluetooth_device(device);
//                Intent intent = new Intent(DeviceListActivity.this, MenuActivity.class);
//                intent.putExtra(MenuActivity.EXTRA_NAME, device.getName());
//                intent.putExtra(MenuActivity.EXTRA_ID, device.getAddress());
                Intent intent = new Intent(DeviceListActivity.this, FreeGameActivity.class);
                intent.putExtra(FreeGameActivity.EXTRA_NAME, device.getName());
                intent.putExtra(FreeGameActivity.EXTRA_ID, device.getAddress());
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            Log.d(Constants.TAG,e.getClass().getCanonicalName()+":"+e.getMessage());
        }
        Settings.getInstance().save(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_main_settings) {
            Intent intent = new Intent(DeviceListActivity.this, MainSettingsActivity.class);
            startActivityForResult(intent, MainSettingsActivity.START_MAIN_SETTINGS);
            return true;
        }
        if (id == R.id.menu_main_help) {
            Intent intent = new Intent(DeviceListActivity.this, HelpActivity.class);
            intent.putExtra(Constants.URI, Constants.MAIN_HELP);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(Constants.TAG, "onActivityResult");
        if (requestCode == MainSettingsActivity.START_MAIN_SETTINGS) {
            if (resultCode == RESULT_OK) {
                Log.d(Constants.TAG, "onActivityResult RESULT_OK");
                setButtonText();
                showMsg(Utility.htmlColorGreen("Ready"));
            } else {
                Log.d(Constants.TAG, "onActivityResult NOT RESULT_OK");
            }
        }
    }

    public void onScan(View view) {

        if (!ble_scanner.isScanning()) {
            device_count=0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissions_granted = false;
                    requestLocationPermission();
                } else {
                    Log.i(Constants.TAG, "Location permission has already been granted. Starting scanning.");
                    permissions_granted = true;
                }
            } else {
                // the ACCESS_COARSE_LOCATION permission did not exist before M so....
                permissions_granted = true;
            }
            startScanning();
        } else {
            showMsg(Utility.htmlColorGreen("Stopping scanning"));
            ble_scanner.stopScanning();
        }
    }

    private void startScanning() {
        if (permissions_granted) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ble_device_list_adapter.clear();
                    ble_device_list_adapter.notifyDataSetChanged();
                }
            });
            simpleToast(getScanningMessage(),2000);
            ble_scanner.startScanning(this, SCAN_TIMEOUT);
        } else {
            showMsg(Utility.htmlColorRed("Permission to perform Bluetooth scanning was not yet granted"));
        }
    }

    private void requestLocationPermission() {
        Log.i(Constants.TAG, "Location permission has NOT yet been granted. Requesting permission.");
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
            Log.i(Constants.TAG, "Displaying location permission rationale to provide additional context.");
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Permission Required");
            builder.setMessage("Please grant Location access so this application can perform Bluetooth scanning");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    Log.d(Constants.TAG, "Requesting permissions after explanation");
                    ActivityCompat.requestPermissions(DeviceListActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
                }
            });
            builder.show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            Log.i(Constants.TAG, "Received response for location permission request.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission has been granted
                Log.i(Constants.TAG, "Location permission has now been granted. Scanning.....");
                permissions_granted = true;
                if (ble_scanner.isScanning()) {
                    startScanning();
                }
            }else{
                Log.i(Constants.TAG, "Location permission was NOT granted.");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void generalAlert(String title, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

    private void simpleToast(String message, int duration) {
        toast = Toast.makeText(this, message, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void setScanState(boolean value) {
        ble_scanning = value;
        ((Button) this.findViewById(R.id.scanButton)).setText(value ? Constants.STOP_SCANNING : "Find paired BBC micro:bits");
    }

    @Override
    public void candidateBleDevice(final BluetoothDevice device, byte[] scan_record, int rssi) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ble_device_list_adapter.addDevice(device);
                ble_device_list_adapter.notifyDataSetChanged();
                device_count++;
            }
        });
    }

    @Override
    public void scanningStarted() {
        setScanState(true);
        showMsg(Utility.htmlColorGreen(getScanningMessage()));
    }

    @Override
    public void scanningStopped() {
        setScanState(false);
        if (device_count > 0) {
            showMsg(Utility.htmlColorGreen("Ready"));
        } else {
            showMsg(Utility.htmlColorRed(getNoneFoundMessage()));
        }
    }
    // adaptor
    private class ListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> ble_devices;

        public ListAdapter() {
            super();
            ble_devices = new ArrayList<BluetoothDevice>();

        }

        public void addDevice(BluetoothDevice device) {
            if (!ble_devices.contains(device)) {
                ble_devices.add(device);
            }
        }

        public boolean contains(BluetoothDevice device) {
            return ble_devices.contains(device);
        }

        public BluetoothDevice getDevice(int position) {
            return ble_devices.get(position);
        }

        public void clear() {
            ble_devices.clear();
        }

        @Override
        public int getCount() {
            return ble_devices.size();
        }

        @Override
        public Object getItem(int i) {
            return ble_devices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = DeviceListActivity.this.getLayoutInflater().inflate(
                        R.layout.list_row, null);
                viewHolder = new ViewHolder();
                viewHolder.text = (TextView) view.findViewById(R.id.textView);
                viewHolder.bdaddr = (TextView) view.findViewById(R.id.bdaddr);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            BluetoothDevice device = ble_devices.get(i);
            String deviceName = device.getName();
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                deviceName = deviceName + " (BONDED)";
            }
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.text.setText(deviceName);
            else
                viewHolder.text.setText("unknown device");

            viewHolder.bdaddr.setText(device.getAddress());

            return view;
        }
    }


    final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                            showMsg(Utility.htmlColorRed("Device was not paired successfully"));
                        } else if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                            showMsg(Utility.htmlColorGreen("Pairing is in progress"));
                        } else {
                            showMsg(Utility.htmlColorGreen("Device was paired successfully - select it now"));
                        }
                }
            }
        };

    private void showMsg(final String msg) {
        Log.d(Constants.TAG, msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) DeviceListActivity.this.findViewById(R.id.message)).setText(Html.fromHtml(msg));
            }
        });
    }

    private String getScanningMessage() {
        if (Settings.getInstance().isFilter_unpaired_devices()) {
            return "Scanning for paired micro:bits";
        } else {
            return "Scanning for all micro:bits";

        }
    }

    private void setButtonText() {
        String text="";
        if (Settings.getInstance().isFilter_unpaired_devices()) {
            text = Constants.FIND_PAIRED;
        } else {
            text = Constants.FIND_ANY;
        }
        final String button_text = text;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) DeviceListActivity.this.findViewById(R.id.scanButton)).setText(button_text);
            }
        });

    }

    private String getNoneFoundMessage() {
        if (Settings.getInstance().isFilter_unpaired_devices()) {
            return Constants.NO_PAIRED_FOUND;
        } else {
            return Constants.NONE_FOUND;
        }
    }











    // XML callback leading from MainActivity to FreeGameActivity (shortcut)
    public void shortcutToFreeGame(View view) {
        Intent intentToActivity = new Intent(this, FreeGameActivity.class);
        DebugModeOn = true;
        intentToActivity.putExtra("MyBoolean", DebugModeOn);
        startActivityForResult(intentToActivity, START_ACTIVITY);
    }





}
