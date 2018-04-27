package com.example.bledemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.model.BleGattService;
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;

import org.w3c.dom.Text;

import java.util.List;
import java.util.UUID;

import static com.inuker.bluetooth.library.Code.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED;
import static java.security.CryptoPrimitive.MAC;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBtnOpen;
    private TextView mTv;
    private BluetoothClient mClient;
    private String mac;
    private UUID serviceUUID;
    private UUID characterUUID1;
    private UUID characterUUID2;
    private UUID characterWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnOpen = findViewById(R.id.btn_open);
        mTv = findViewById(R.id.tv);
        mBtnOpen.setOnClickListener(this);
        mClient = MyApplication.getBluetoothClient(this);
        serviceUUID = UUID.fromString("d94e1435-bc06-79e3-402d-b39dd0cdc27f");
        characterUUID1 = UUID.fromString("d94e1435-bc06-79e3-402d-b39dd0cdc27f");
        characterUUID2 = UUID.fromString("d94e1435-bc06-79e3-402d-b39dd0cdc27f");
        characterWrite = UUID.fromString("d94e1435-bc06-79e3-402d-b39dd0cdc27f");

        mClient.registerBluetoothBondListener(mBluetoothBondListener);
        mClient.registerConnectStatusListener(mac, mBleConnectStatusListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_open:
                scan();
                connByMacAddress(mac);
                break;
        }
    }

    private void scan() {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(3000, 3)   // 先扫BLE设备3次，每次3s
                .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s
                .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
                .build();

        mClient.search(request, new SearchResponse() {
            @Override
            public void onSearchStarted() {
                mTv.append("scan...\n");
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                mTv.append("Device Founded\n");
                mTv.append("mac address:" + device.getAddress() + "\n");
                if (device.getAddress().contains(mac)) {
                    mClient.stopSearch();
                    connByMacAddress(mac);
                    mClient.registerConnectStatusListener(mac, mBleConnectStatusListener);
                }

            }

            @Override
            public void onSearchStopped() {
                mTv.append("onSearchStopped\n");
            }

            @Override
            public void onSearchCanceled() {
                mTv.append("onSearchCanceled\n");
            }
        });
    }

    private void connByMacAddress(final String mac) {
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)   // 连接如果失败重试3次
                .setConnectTimeout(30000)   // 连接超时30s
                .setServiceDiscoverRetry(3)  // 发现服务如果失败重试3次
                .setServiceDiscoverTimeout(20000)  // 发现服务超时20s
                .build();

        mClient.connect(mac, options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile profile) {
                if (code == REQUEST_SUCCESS) {
                    openNotify1(mac);
                }
            }
        });
    }

    private final BluetoothBondListener mBluetoothBondListener = new BluetoothBondListener() {
        @Override
        public void onBondStateChanged(String mac, int bondState) {
            // bondState = Constants.BOND_NONE, BOND_BONDING, BOND_BONDED
            Toast.makeText(MainActivity.this, "bondState:" + bondState,
                    Toast.LENGTH_SHORT).show();
        }
    };

    private final BleConnectStatusListener mBleConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            if (status == STATUS_CONNECTED) {
                Toast.makeText(MainActivity.this, "STATUS_CONNECTED",
                        Toast.LENGTH_SHORT).show();
            } else if (status == STATUS_DISCONNECTED) {
                Toast.makeText(MainActivity.this, "STATUS_DISCONNECTED",
                        Toast.LENGTH_SHORT).show();
            }

        }
    };

    private void openNotify1(final String mac) {
        mClient.notify(mac, serviceUUID, characterUUID1, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
                openNotify2(mac);

                String str = new String(value);
                mTv.append("Notify1:" + str + "\n");

                write(mac, str.getBytes());

            }

            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {

                }
            }
        });


    }

    private void write(String mac, byte[] bytes) {
        mClient.write(mac, serviceUUID, characterWrite, bytes, new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    mTv.append("write:" + code + "\n");
                }
            }
        });
    }

    private void openNotify2(String mac) {
        mClient.notify(mac, serviceUUID, characterUUID2, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
                String str = new String(value);
                mTv.append("Notify2:" + str + "\n");

            }

            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {

                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClient.unregisterBluetoothBondListener(mBluetoothBondListener);
        mClient.unregisterConnectStatusListener(mac, mBleConnectStatusListener);
    }


    public void onSaveUuid() {
//        List<BleGattService> services = profile.getServices();
//        for (BleGattService service : services) {
//            if (service.getUUID().toString().contains(Utils.SEND_SERVICE)) {
//                List<BleGattCharacter> characters = service.getCharacters();
//                for (BleGattCharacter character : characters) {
//                    //save uuid
//                    SharedPreferenceUtils.saveSendService(context,
//                            service.getUUID().toString());
//                    SharedPreferenceUtils.saveSendCharacter(context,
//                            character.getUuid().toString());
//                }
//            } else if (service.getUUID().toString().contains(Utils.RECEIVE_SERVICE)) {
//                List<BleGattCharacter> characters = service.getCharacters();
//                for (BleGattCharacter character : characters) {
//                    //save uuid
//                    SharedPreferenceUtils.saveReceiveService(context,
//                            service.getUUID().toString());
//                    SharedPreferenceUtils.saveReceiveCharacter(context,
//                            character.getUuid().toString());
//
//                }
//            }

//        }
    }
}
