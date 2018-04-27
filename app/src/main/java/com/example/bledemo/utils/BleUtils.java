package com.example.bledemo.utils;

import android.util.Log;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by KomoriWu
 * on 2018-04-27.
 */

public class BleUtils {

    //分包
    public static void divideFrameBleSendData(byte[] data, BluetoothClient client,
                                              final String macAddress, final UUID serviceUUID,
                                              final UUID characterUUID) {
        Log.d("BLE Write Command:", new String(data));
        int tmpLen = data.length;
        int start = 0;
        int end = 0;
        final boolean[] isShowDialog = {true};
        while (tmpLen > 0) {
            byte[] sendData = new byte[21];
            if (tmpLen >= 20) {
                end += 20;
                sendData = Arrays.copyOfRange(data, start, end);
                start += 20;
                tmpLen -= 20;
            } else {
                end += tmpLen;
                sendData = Arrays.copyOfRange(data, start, end);
                tmpLen = 0;
            }

            client.write(macAddress, serviceUUID, characterUUID, sendData,
                    new BleWriteResponse() {
                        @Override
                        public void onResponse(int code) {
                            if (code == -1 && isShowDialog[0]) {
                                Log.d("tag", "code:" + code);
                                Log.d("tag", "macAddress:" + macAddress);
                                Log.d("tag", "serviceUUID:" + serviceUUID);
                                Log.d("tag", "characterUUID:" + characterUUID);
                                isShowDialog[0] = false;
                            }
                        }
                    });

        }
    }
}
