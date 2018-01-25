package com.aliyan.mangwalopakistan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import java.math.BigDecimal;


public class orderDetails extends AppCompatActivity {

    SingleOrder singleOrder;
    ProgressBar pb;
    ListView lv;
    TextView cost;
    Button payNow;
    CustomAdapter itemAdapter;
    boolean rec;
    static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        private static PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK).clientId(UserType.getType());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        cost = (TextView) findViewById(R.id.cost);
        payNow = (Button) findViewById(R.id.payNow);
        rec = (boolean) getIntent().getSerializableExtra("rec");
        if(rec)
            payNow.setVisibility(View.GONE);

        singleOrder = (SingleOrder) getIntent().getSerializableExtra("single order");
        cost.setText("Total cost: Rs." + singleOrder.getCost().toString());

        itemAdapter = new CustomAdapter(this, R.layout.order_detail_row, singleOrder.getItems(),singleOrder.getQuantities());
        lv = (ListView) findViewById(R.id.itemListOrders);
        lv.setClickable(true);
        pb = (ProgressBar) findViewById(R.id.progressOrders);



    }

    @Override
    public void onResume() {
        load();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this,PayPalService.class));
        super.onDestroy();
    }

    private void load()
    {
        pb.setVisibility(View.GONE);
        lv.setAdapter(itemAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void scanBarcode(View v){
        Intent intent = new Intent(this, ScanBarcodeActivity.class);
        startActivityForResult(intent,0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0){
            if(resultCode == CommonStatusCodes.SUCCESS){
                if(data != null){
                    Barcode barcode = data.getParcelableExtra("barcode");
                    if(singleOrder.id.equals(barcode.displayValue)){
                        PayPalPayment payment = new PayPalPayment(new BigDecimal(singleOrder.cost),"Rs.",singleOrder.id,PayPalPayment.PAYMENT_INTENT_SALE);
                        Intent intent = new Intent(this, PayPalActivity.class);
                        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
                        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payment);
                        intent.putExtra("single orders",singleOrder);
                        startActivityForResult(intent,69);
                     }
                    else{
                        Toast.makeText(this,"Invalid QR Code",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(this,"Invalid QR Code",Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if(requestCode == 69){
            if(resultCode == CommonStatusCodes.SUCCESS){
                Toast.makeText(getApplication(),"Transaction Successful",Toast.LENGTH_SHORT).show();
            }
            else if(resultCode == CommonStatusCodes.ERROR){
                Toast.makeText(getApplication(),"Error",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
