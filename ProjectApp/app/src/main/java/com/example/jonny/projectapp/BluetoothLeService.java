/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jonny.projectapp;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {

    int i = 1;
    int j = 0;
    int counter = 0;
    int epochSize = 30;
    float[] epoch = new float[epochSize];
    float activity = 0f;
    float x, y, z, X, Y, Z, xPrev, yPrev, zPrev;
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    int sleepLatency = 10; // In minutes
    int solEpochs = sleepLatency * 60 / epochSize;
    float activityThreshold = 1;
    int awakeStateEpochs = 2; // number of high activity epochs required to change state to awake
    int asleep = 0;
    int awake = 0;
    Date bedTime;
    Date wakeTime;
    boolean activityFlag = false;
    boolean sleepFlag = false;
    float solTime = 0;

    ArrayList<String[]> dataToSave = new ArrayList<String[]>();
    ArrayList<Float> actigraphyData = new ArrayList<Float>();
    //Double[] actigraphyData;
    int[] sleepState;

    private BluetoothLeService mBluetoothLeService;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public final static UUID UUID_ACCELEROMETER_SENSOR =
            UUID.fromString(SampleGattAttributes.ACCELEROMETER_SENSOR);

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

                bedTime = new Date();
                Log.i(TAG, String.valueOf(bedTime));

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");

                /***** SAVE ACTIGRAPHY TO CSV FILE WHEN DISCONNECTION OCCURS *****/

                File exportDir = new File(Environment.getExternalStorageDirectory(), "SleepActigraphy");
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }
                File file = new File(exportDir, "Actigraphy" + ".csv");
                try {
                    file.createNewFile();
                    CSVWriter writer = new CSVWriter(new FileWriter(file));
                    writer.writeAll(dataToSave);
                    writer.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }

                //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                wakeTime = new Date();
                Log.i(TAG, String.valueOf(wakeTime));

                analyseSleep();
                broadcastUpdate(intentAction);
            }
        }

        public void analyseSleep() {
            for (i = 0; i < actigraphyData.size(); i++) {
                if (actigraphyData.get(i) > activityThreshold) {
                    sleepState[i] = 1;
                }

                if (i > solEpochs && sleepFlag == false) {
                    for (j = i; j > i - solTime; j--) {
                        if (sleepState[j] == 1) {
                            activityFlag = true;
                            break;
                        }
                    }
                    if (activityFlag == true) {
                        activityFlag = false;
                    }
                    else {
                        sleepFlag = true;
                        solTime = i - solEpochs;
                    }
                }

                // Determing asleep/awake amounts
                if (sleepFlag == true) {
                    if (actigraphyData.get(i) > activityThreshold) {
                        awake = awake + 1;
                    }
                    else {
                        asleep = asleep + 1;
                    }
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        if (counter == 0) {
            final byte[] data = characteristic.getValue();

//            byte[] timeStamp = Arrays.copyOfRange(data, 0, 4);
//            final byte packetNo = data[4]; // SIGNED
            byte[] xAccel = Arrays.copyOfRange(data, 5, 7);
            byte[] yAccel = Arrays.copyOfRange(data, 7, 9);
            byte[] zAccel = Arrays.copyOfRange(data, 9, 11);
//            byte[] xGyro = Arrays.copyOfRange(data, 11, 12);
//            byte[] yGyro = Arrays.copyOfRange(data, 13, 14);
//            byte[] zGyro = Arrays.copyOfRange(data, 15, 16);
//            final byte voltage = data[17]; // Need formula to calculate voltage correctly
//            final byte charge = data[18];
//            final byte rssi = data[19];

//        2g = 16,384 counts/g    4g = 8,192 counts/g      8g = 4,096 counts/g      16g =  2,048 counts/g

            x = (xAccel[0] << 8 | (xAccel[1] & 0xff));
            y = (yAccel[0] << 8 | (yAccel[1] & 0xff));
            z = (zAccel[0] << 8 | (zAccel[1] & 0xff));

            X = x / 4096;
            Y = y / 4096;
            Z = z / 4096;

            X = X - xPrev;
            Y = Y - yPrev;
            Z = Z - zPrev;
//            String xVal = String.valueOf(X);
//            String yVal = String.valueOf(Y);
//            String zVal = String.valueOf(Z);
            activity = Math.abs(X) + Math.abs(Y) + Math.abs(Z) + activity;

            xPrev = X;
            yPrev = Y;
            zPrev = Z;

            //actigraphyData .add(new String[] {xVal,yVal,zVal});

            if (i == epochSize) {
                dataToSave .add(new String[] {String.valueOf(activity)});
                actigraphyData .add(activity);
                activity = 0f;
                i = 0;
            }

            i++;
            sendBroadcast(intent);
            counter = 99;
        }
        counter--;
    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        //close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;

        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        Log.i(TAG, "NOTIFIER SET");

        mBluetoothGatt.setCharacteristicNotification(characteristic, true);
        UUID uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(uuid);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
}
