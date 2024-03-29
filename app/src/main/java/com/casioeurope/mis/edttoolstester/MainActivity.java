package com.casioeurope.mis.edttoolstester;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ProxyInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.casioeurope.mis.edt.EDTLibrary;
import com.casioeurope.mis.edt.KeyLibrary;
import com.casioeurope.mis.edt.ScannerLibrary;
import com.casioeurope.mis.edt.SystemLibrary;
import com.casioeurope.mis.edt.constant.ScannerLibraryConstant;
import com.casioeurope.mis.edt.type.APN;
import com.casioeurope.mis.edt.type.ReadWriteFileParams;
import com.casioeurope.mis.edt.type.ScanResult;
import com.casioeurope.mis.edttoolstester.databinding.ActivityMainBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Date;

@SuppressWarnings("SpellCheckingInspection")
public class MainActivity extends Activity implements View.OnClickListener {

    private ActivityMainBinding activityMainBinding;
    private static final String TAG = "EDT Tool Tester";

    @SuppressWarnings("SpellCheckingInspection")
    public static final BroadcastReceiver scanResultReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ScanResult scanResult = new ScanResult();
            try {
                ScannerLibrary.getScanResult(scanResult);
                Log.d(TAG, String.format("ScanResult length=%d, time=%d, aimID=%d, aimModifier=%d, symbologyID=%d, symbologyName=%s, value=%s",
                        scanResult.length,
                        scanResult.time,
                        scanResult.aimID,
                        scanResult.aimModifier,
                        scanResult.symbologyID,
                        scanResult.symbologyName,
                        Arrays.toString(scanResult.value)));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = activityMainBinding.getRoot();
        setContentView(view);

        IntentFilter filter = new IntentFilter();
        filter.addAction("device.common.USERMSG");
        registerReceiver(scanResultReceiver, filter);

        try {
            Log.d(TAG, String.format("openScanner = %d", ScannerLibrary.openScanner())); // This call will automatically be deferred to a LibraryCallback
            Log.d(TAG, String.format("setOutputType = %d", ScannerLibrary.setOutputType(ScannerLibraryConstant.OUTPUT.USER))); // This call will automatically be deferred to a LibraryCallback
        } catch (RemoteException | UnsupportedOperationException e) {
            e.printStackTrace();
        }

        try {
            EDTLibrary.onLibraryReady(() -> SystemLibrary.onLibraryReady(() -> { // The following code block will be carried out once onCreate etc. have been processed and the app is up and running.
                EDTLibrary.testMessage(String.format("Serial Number = %s\r\nModel Name=%s", SystemLibrary.getCASIOSerial(), SystemLibrary.getModelName()));
            }));
        } catch (RemoteException | UnsupportedOperationException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        try {
            Log.d(TAG, String.format("closeScanner = %d", ScannerLibrary.closeScanner()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        unregisterReceiver(scanResultReceiver);
        super.onDestroy();
    }

    @SuppressLint({"SdCardPath", "ObsoleteSdkInt"})
    @SuppressWarnings({"deprecation", "RedundantSuppression"})
    @Override
    public void onClick(View v) {
        try {
            if (v == activityMainBinding.buttonReboot) {
                Log.d(TAG, "Calling Reboot from Service!");
                String result = String.format("Reboot Result = %b", EDTLibrary.reboot());
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonRebootFactory) {
                Log.d(TAG, "Calling Factory Reset from Service!");
                String result = String.format("Factory Reset Result = %b", EDTLibrary.factoryReset(true));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonRebootRecovery) {
                Log.d(TAG, "Calling Recovery Reboot from Service!");
                String result = String.format("Recovery Reboot Result = %b", EDTLibrary.recovery());
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonShutdown) {
                Log.d(TAG, "Calling Shutdown from Service!");
                String result = String.format("Shutdown Result = %b", EDTLibrary.shutdown());
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonClearPassword) {
                Log.d(TAG, "Calling Clear Password from Service!");
                String result = String.format("Clear Password Result = %b", EDTLibrary.clearPassword());
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonResetPassword) {
                Log.d(TAG, "Calling Reset Password from Service!");
                String result = String.format("Reset Password Result = %b", EDTLibrary.resetPassword("1234"));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonLockDevice) {
                Log.d(TAG, "Calling Lock Device from Service!");
                String result = String.format("Lock Device Result = %b", EDTLibrary.lockDevice());
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonAllowUnknownSources) {
                Log.d(TAG, "Calling Allow Unknown Sources(true) from Service!");
                String result = String.format("Allow Unknown Sources(true) Result = %b", EDTLibrary.allowUnknownSources(true));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonDisallowUnknownSources) {
                Log.d(TAG, "Calling Allow Unknown Sources(false) from Service!");
                String result = String.format("Allow Unknown Sources(false) Result = %b", EDTLibrary.allowUnknownSources(false));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonSetDateTime) {
                Log.d(TAG, "Calling Set Date/Time from Service!");
                String result = String.format("Date/Time Result = %b", EDTLibrary.setDateTime(new Date()));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonSetTimeZone) {
                boolean retVal;
                String newTimeZone = "Europe/London";
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                    java.util.TimeZone curTimeZone = java.util.TimeZone.getDefault();
                    if (curTimeZone.getRawOffset() == 0) {
                        newTimeZone = "Europe/Berlin";
                        //noinspection UnusedAssignment
                        retVal = EDTLibrary.setTimeZone(java.util.TimeZone.getTimeZone(newTimeZone));
                    }
                    else {
                        if (curTimeZone.getRawOffset() > 0) newTimeZone = "Atlantic/Azores";
                        //noinspection UnusedAssignment
                        retVal = EDTLibrary.setTimeZone(newTimeZone);
                    }

                } else {
                    android.icu.util.TimeZone curTimeZone = android.icu.util.TimeZone.getDefault();
                    if (curTimeZone.getRawOffset() == 0) {
                        newTimeZone = "Europe/Berlin";
                        retVal = EDTLibrary.setTimeZone(android.icu.util.TimeZone.getTimeZone(newTimeZone));
                    }
                    else {
                        if (curTimeZone.getRawOffset() > 0) newTimeZone = "Atlantic/Azores";
                        retVal = EDTLibrary.setTimeZone(newTimeZone);
                    }
                    String result = String.format("Setting TimeZone to %s Result = %b", newTimeZone, retVal);
                    EDTLibrary.testMessage(result);
                    Log.d(TAG, result);
                }
            } else if (v == activityMainBinding.buttonRemoveAllGoogleAccounts) {
                Log.d(TAG, "Calling Remove all Google Accounts from Service!");
                String result = String.format("Remove all Google Accounts Result = %b", EDTLibrary.removeAllGoogleAccounts());
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonRemoveAllAccounts) {
                Log.d(TAG, "Calling Remove all Accounts from Service!");
                String result = String.format("Remove all Accounts Result = %b", EDTLibrary.removeAllAccounts());
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonEnableWifi) {
                Log.d(TAG, "Calling Enable Wifi from Service!");
                String result = String.format("Enable Wifi Result = %b", EDTLibrary.enableWifi(true));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonDisableWifi) {
                Log.d(TAG, "Calling Disable Wifi from Service!");
                String result = String.format("Disable Wifi Result = %b", EDTLibrary.enableWifi(false));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonAddWifi) {
                Log.d(TAG, "Calling Add Wifi Account from Service!");
                android.net.wifi.WifiConfiguration conf = new android.net.wifi.WifiConfiguration();
                conf.SSID = "\"Test Network\"";
                conf.hiddenSSID = true;
                //noinspection SpellCheckingInspection
                conf.preSharedKey = "\"whatever\"";
                ProxyInfo proxyInfo = ProxyInfo.buildDirectProxy("my.proxy.server", 1234);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) { // requires Android O or later
                    conf.setHttpProxy(proxyInfo);
                } else {
                    try {
                        Method setHttpProxyMethod = conf.getClass().getDeclaredMethod("setHttpProxy", ProxyInfo.class);
                        setHttpProxyMethod.setAccessible(true);
                        setHttpProxyMethod.invoke(conf, proxyInfo);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                boolean retVal = EDTLibrary.addNetwork(conf);
                if (retVal) retVal = EDTLibrary.connectNetwork("MIS Test");
                String result = String.format("Add Wifi AccountResult = %b", retVal);
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonRemoveWifi) {
                Log.d(TAG, "Calling Remove Wifi Account from Service!");
                String result = String.format("Remove Wifi Account Result = %b", EDTLibrary.removeNetwork("MIS Test"));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonEnableBluetooth) {
                Log.d(TAG, "Calling Enable Bluetooth from Service!");
                String result = String.format("Enable Bluetooth Result = %b", EDTLibrary.enableBluetooth(true));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonDisableBluetooth) {
                Log.d(TAG, "Calling Disable Bluetooth from Service!");
                String result = String.format("Disable Bluetooth Result = %b", EDTLibrary.enableBluetooth(false));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonEnableNfc) {
                Log.d(TAG, "Calling Enable NFC from Service!");
                String result = String.format("Enable NFC Result = %b", EDTLibrary.enableNfc(true));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonDisableNfc) {
                Log.d(TAG, "Calling Disable NFC from Service!");
                String result = String.format("Disable NFC Result = %b", EDTLibrary.enableNfc(false));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonEnableGps) {
                Log.d(TAG, "Calling Enable GPS from Service!");
                String result = String.format("Enable GPS Result = %b", EDTLibrary.enableGps(true));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonDisableGps) {
                Log.d(TAG, "Calling Disable GPS from Service!");
                String result = String.format("Disable GPS Result = %b", EDTLibrary.enableGps(false));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonEnableWwan) {
                Log.d(TAG, "Calling Enable WWAN from Service!");
                String result = String.format("Enable WWAN Result = %b", EDTLibrary.enableWwan(true));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonDisableWwan) {
                Log.d(TAG, "Calling Disable WWAN from Service!");
                String result = String.format("Disable WWAN Result = %b", EDTLibrary.enableWwan(false));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonEnableDeveloperMode) {
                Log.d(TAG, "Calling Enable Developer Mode from Service!");
                String result = String.format("Enable Developer Mode Result = %b", EDTLibrary.enableDeveloperMode(true));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonDisableDeveloperMode) {
                Log.d(TAG, "Calling Disable Developer Mode from Service!");
                String result = String.format("Disable Developer Mode Result = %b", EDTLibrary.enableDeveloperMode(false));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonCopyFile) {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) { // requires Android O or later
                    EDTLibrary.testMessage("copying Files requires Android O (8) or later!");
                } else {
                    Log.d(TAG, "Calling Copy File from Service!");
                    String result = String.format("Copy File Result = %b", EDTLibrary.copyFile(Paths.get("/sdcard/Download/devinfo.html"), Paths.get("/sdcard/Download/devinfo1.html"), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES, LinkOption.NOFOLLOW_LINKS));
                    EDTLibrary.testMessage(result);
                    Log.d(TAG, result);
                }
            } else if (v == activityMainBinding.buttonMoveFile) {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) { // requires Android O or later
                    EDTLibrary.testMessage("moving Files requires Android O (8) or later!");
                } else {
                    Log.d(TAG, "Calling Move File from Service!");
                    String result = String.format("Move File Result = %b", EDTLibrary.moveFile(Paths.get("/sdcard/Download/devinfo1.html"), Paths.get("/sdcard/Download/devinfo2.html")));
                    EDTLibrary.testMessage(result);
                    Log.d(TAG, result);
                }
            } else if (v == activityMainBinding.buttonReadFile) {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) { // requires Android O or later
                    EDTLibrary.testMessage("reading Files requires Android O (8) or later!");
                } else {
                    Log.d(TAG, "Calling Read File from Service!");
                    byte[] testData = new byte[4096];
                    ReadWriteFileParams readWriteFileParams = ReadWriteFileParams.setPath(Paths.get("/sdcard/Download/devinfo.html")).setData(testData).setFileOffset(1).setDataOffset(2).setLength(100).setOptions(StandardOpenOption.READ).build();
                    String result = String.format("Read File Result = %b", EDTLibrary.readFile(readWriteFileParams));
                    EDTLibrary.testMessage(result);
                    Log.d(TAG, result);
                }
            } else if (v == activityMainBinding.getApnList) {
                Log.d(TAG, "Calling Get All APN List from Service!");
                APN[] apnArray = EDTLibrary.getAllApnList();
                @SuppressLint("DefaultLocale") String result = String.format("Get All APN List Result = %d", apnArray == null ? -1 : apnArray.length);
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.getGoogleAccounts) {
                Log.d(TAG, "Calling Get Google Accounts from Service!");
                Account[] accounts = EDTLibrary.getGoogleAccounts();
                @SuppressLint("DefaultLocale") String result = String.format("Get Google Accounts Result = %d", accounts == null ? -1 : accounts.length);
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.removeGoogleAccounts) {
                Log.d(TAG, "Calling Remove Google Accounts from Service!");
                String result = String.format("Remove Google Accounts Result = %b", EDTLibrary.removeAllAccounts());
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.installCertificate) {
                Log.d(TAG, "Calling Install Certificate from Service!");
                String result = String.format("Install casio-europe.pem Result = %b", EDTLibrary.installCertificate("TEST CA", "/sdcard/test_cert.pem"));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.mountSdCard) {
                Log.d(TAG, "Calling Mount SD Card from Service!");
                String result = String.format("Mount SD Card Result = %b", EDTLibrary.mountSDCard(true));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.unmountSdCard) {
                Log.d(TAG, "Calling Unmount SD Card from Service!");
                String result = String.format("Unmount SD Card Result = %b", EDTLibrary.mountSDCard(false));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.screenLockTimeout) {
                Log.d(TAG, "Calling Set Screen Lock Timeout from Service!");
                String result = String.format("Set Screen Lock Timeout Result = %b", EDTLibrary.setScreenLockTimeout(Integer.MAX_VALUE));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonInstallApp) {
                Log.d(TAG, "Calling Install App from Service!");
                String result = String.format("Install App Result = %b", EDTLibrary.installApk("/sdcard/test_app.apk", false));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonUninstallApp) {
                Log.d(TAG, "Calling Uninstall App from Service!");
                String result = String.format("Uninstall App Result = %b", EDTLibrary.uninstallPackage("com.test.app.package"));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonClearData) {
                Log.d(TAG, "Calling Clear Data from Service!");
                String result = String.format("Clear Data Result = %b", EDTLibrary.clearDataForPackage("com.android.chrome"));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonClearCache) {
                Log.d(TAG, "Calling Clear Cache from Service!");
                String result = String.format("Clear Cache Result = %b", EDTLibrary.clearCacheForPackage("com.android.chrome"));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonEnableApp) {
                Log.d(TAG, "Calling Enable App from Service!");
                String result = String.format("Enable App Result = %b", EDTLibrary.enableApplication("com.android.chrome", true));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonDisableApp) {
                Log.d(TAG, "Calling Disable App from Service!");
                String result = String.format("Disable App Result = %b", EDTLibrary.enableApplication("com.android.chrome", false));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonEnableDoze) {
                Log.d(TAG, "Calling Enable Doze Mode from Service!");
                String result = String.format("Enable Doze Mode Result = %b", EDTLibrary.enableBatteryOptimization("com.android.chrome", true));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonDisableDoze) {
                Log.d(TAG, "Calling Disable Doze Mode from Service!");
                String result = String.format("Disable Doze Mode Result = %b", EDTLibrary.enableBatteryOptimization("com.android.chrome", false));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonShowSerialNumber) {
                Log.d(TAG, "Calling getCASIOSerial from System Library!");
                String result = String.format("Serial Number = %s\r\nModel Name=%s", SystemLibrary.getCASIOSerial(), SystemLibrary.getModelName());
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonOpenScanner) {
                Log.d(TAG, "Calling openScanner from Scanner Library!");
                Log.d(TAG, String.format("openScanner = %d", ScannerLibrary.openScanner()));
                Log.d(TAG, String.format("openScanner = %d", ScannerLibrary.setOutputType(ScannerLibraryConstant.OUTPUT.USER)));
            } else if (v == activityMainBinding.buttonCloseScanner) {
                Log.d(TAG, "Calling closeScanner from Scanner Library!");
                @SuppressLint("DefaultLocale") String result = String.format("closeScanner = %d", ScannerLibrary.closeScanner());
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonScanBarcode) {
                Log.d(TAG, "Calling setTriggerKeyOn from Scanner Library!");
                @SuppressLint("DefaultLocale") String result = String.format("setTriggerKeyOn = %d", ScannerLibrary.setTriggerKeyOn(ScannerLibraryConstant.TRIGGERKEY.ENABLE));
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonToggleInputMode) {
                Log.d(TAG, "Calling Get Keypad Mode from Keyboard Library!");
                int keyPadMode = KeyLibrary.getKeypadMode();
                Log.d(TAG, String.format("Get Keypad Mode Result = %d", keyPadMode));
                keyPadMode++;
                if (keyPadMode > 4) keyPadMode = 1;
                Log.d(TAG, "Calling Set Keypad Mode from Keyboard Library!");
                Log.d(TAG, String.format("Set Keypad Mode Result = %b", KeyLibrary.setKeypadMode(keyPadMode)));
            } else if (v == activityMainBinding.buttonLogcatToFile) {
                Log.d(TAG, "Calling logcatToFile from EDT Library!");
                @SuppressLint("DefaultLocale") String result = String.format("logcatToFile = %b", EDTLibrary.logcatToFile(Environment.getExternalStorageDirectory() + "/logcat.txt"));
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonLogcatClear) {
                Log.d(TAG, "Calling logcatClear from EDT Library!");
                @SuppressLint("DefaultLocale") String result = String.format("logcatClear = %b", EDTLibrary.logcatClear());
                EDTLibrary.testMessage(result);
                Log.d(TAG, result);
            } else if (v == activityMainBinding.buttonTestMessage) {
                Log.d(TAG, "Calling Test Message from Service!");
                Log.d(TAG, String.format("Test Message Result = %b", EDTLibrary.testMessage("EDT Tools Test Message!")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
