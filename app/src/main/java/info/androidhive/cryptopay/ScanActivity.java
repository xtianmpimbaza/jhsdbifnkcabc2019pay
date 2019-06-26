package info.androidhive.cryptopay;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.widget.Toast;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;

public class ScanActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {

    BarcodeReader barcodeReader;
    String to_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        to_activity = getIntent().getStringExtra("to_activity");
        // get the barcode reader instance
        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_scanner);
//        cameraSource.setRequestedPreviewSize(1280, 720);
    }

    @Override
    public void onScanned(Barcode barcode) {

        // playing barcode reader beep sound
        barcodeReader.playBeep();
        if (to_activity.equals("DepositActivity")) {
            Intent intent = new Intent(ScanActivity.this, DepositActivity.class);
            intent.putExtra("code", barcode.displayValue);
            finish();
            startActivity(intent);
        } else if(to_activity.equals("WithdrawActivity")) {
            Intent intent = new Intent(ScanActivity.this, WithdrawActivity.class);
            intent.putExtra("code", barcode.displayValue);
            finish();
            startActivity(intent);
        }else{
            finish();
        }
    }

    @Override
    public void onScannedMultiple(List<Barcode> list) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String s) {
        Toast.makeText(getApplicationContext(), "Error occurred while scanning " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraPermissionDenied() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showActionsDialog(final String address) {
        Log.e("Scanned", address);
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle("Select")
                .setMessage("Deposit or withdraw")
                .addButton("Deposit", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent deposit = new Intent(ScanActivity.this, DepositActivity.class);
                        deposit.putExtra("code", address);
                        startActivity(deposit);
                        dialog.dismiss();
                    }
                })
                .addButton("Withdraw", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent withdraw = new Intent(ScanActivity.this, WithdrawActivity.class);
                        withdraw.putExtra("code", address);
                        startActivity(withdraw);
                        dialog.dismiss();
                    }
                });
        builder.show();
    }
}
