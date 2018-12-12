package com.pruebascannerbeacons;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.IllegalViewOperationException;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class BeaconModule extends ReactContextBaseJavaModule implements BeaconConsumer, RangeNotifier {

    private BeaconManager mBeaconManager;
    private Map<String, String> blueUp;
    private Context appContext;
    private Map<String , Double> prom;

    public BeaconModule(ReactApplicationContext reactContext) {
        super(reactContext);

        blueUp = new HashMap<>();
        prom = new HashMap<>();
        blueUp.put("0xacfd065e1a514932ac01", "BlueUp1");
        blueUp.put("0xacfd065e1a514932ac02", "BlueUp2");
        appContext = reactContext;


        mBeaconManager = BeaconManager.getInstanceForApplication(reactContext);

        //Le decimos al beaconManager que protocolo vamos a utilizar.
        mBeaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));

        mBeaconManager.setForegroundBetweenScanPeriod(6000L);
        // mBeaconManager.setForegroundScanPeriod(1000L);
        mBeaconManager.bind(this);

    }

    @Override
    public String getName() {
        return "BeaconModule";
    }


    @Override
    public void onBeaconServiceConnect() {


        ArrayList<Identifier> identifiers = new ArrayList<>();
        //identifiers.add(null);

        Region region = new Region("AllBeaconsRegion", identifiers);
        Log.e("TAG", "Estoy Aquí tambien");
        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        mBeaconManager.addRangeNotifier(this);
    }

    @Override
    public Context getApplicationContext() {
        return appContext;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {

    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return false;
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        Log.e("TAG", "Longitud = " + beacons.size());

        if (beacons.size() > 0) {
            Log.e("ERROR", beacons.toString());
            for(int i = 0; i < beacons.size(); i++) {
                Beacon beacon = beacons.iterator().next();
                if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {
                    if (blueUp.containsKey(beacon.getId1().toString())) {
                        prom.put(blueUp.get(beacon.getId1().toString()), beacon.getDistance());
                        Log.d("BEACON","El beacon " + blueUp.get(beacon.getId1().toString()) + " se encuentra a una distancia de "
                                + beacon.getDistance() + " metros");
                    }
                    Log.e("TELEMETRY", "ExtraData size = " + beacon.getExtraDataFields().size());
                    if (beacon.getExtraDataFields().size() > 0) {
                        long telemetryVersion = beacon.getExtraDataFields().get(0);
                        long batteryMilliVolts = beacon.getExtraDataFields().get(1);
                        long pduCount = beacon.getExtraDataFields().get(3);
                        long uptime = beacon.getExtraDataFields().get(4);

                        Log.e("TELEMETRY", "The above beacon is sending telemetry version "+telemetryVersion+
                                ", has been up for : "+uptime+" seconds"+
                                ", has a battery level of "+batteryMilliVolts+" mV"+
                                ", and has transmitted "+pduCount+" advertisements.");

                    }
                }
            }
        }
    }

    @ReactMethod
    private void showToastMessage(String message) {
        Toast toast = Toast.makeText(getReactApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @ReactMethod
    public void getBeaconsArray(Promise promise){
        try{
            WritableMap map = Arguments.createMap();
            WritableArray x = Arguments.createArray();
            WritableArray y = Arguments.createArray();
            Set<String> aux = prom.keySet();
            for(int i = 0; i < prom.size(); i++){
                String beacon = aux.iterator().next();
                x.pushString(beacon);
                y.pushDouble(prom.get(beacon));
            }
            map.putArray("beacons", x);
            map.putArray("distance", y);

            promise.resolve(map);
        }catch (IllegalViewOperationException e){
            promise.reject("Promise error", e);
        }
    }
}
    