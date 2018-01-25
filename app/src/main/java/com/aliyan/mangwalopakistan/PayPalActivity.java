package com.aliyan.mangwalopakistan;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class PayPalActivity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String FILE = Environment.getExternalStorageDirectory().toString() + "/MangwaloPakistan/"+"Name.pdf";
    Uri downloadURL;
    SingleOrder order;
    Intent intent;
    String userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);

        userType=UserType.getType();

        Document document = new Document(PageSize.A4);
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/MangwaloPakistan");
        myDir.mkdirs();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
            document.open();
        }catch(Exception e){

        }
        addMetaData(document);
        try {
            makeInvoice(document);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        try {
            sendToServer(document);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void sendToServer(final Document document) throws FileNotFoundException {
        document.close();

        Uri file = Uri.fromFile(new File(FILE));

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mangwalo-70e56.appspot.com");
        storageReference = storageReference.child("INVOICES/Name.pdf");
        UploadTask uploadTask = storageReference.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                document.close();
                setResult(CommonStatusCodes.ERROR);
                finish();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                document.close();
                downloadURL = taskSnapshot.getDownloadUrl();
            }
        });

    }

    private void makeInvoice(Document document) throws DocumentException {

        order = (SingleOrder) getIntent().getSerializableExtra("single orders");
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD| Font.UNDERLINE, BaseColor.GRAY);
        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
        Paragraph ph = new Paragraph();

        ph.setFont(titleFont);
        ph.add("Mangwalo Pakistan\n\n\n");
        ph.setFont(catFont);
        ph.add("Payment Receipt\n\n\n\n\n");

        Paragraph invoice = new Paragraph();
        invoice.setFont(smallBold);
        invoice.add("User ID: ");
        invoice.setFont(normal);
        invoice.add(userType + "\n");
        invoice.setFont(smallBold);
        invoice.add("User Source: ");
        invoice.setFont(normal);
        invoice.add(UserType.getEmail()  + "\n\n\n\n");
        invoice.setFont(smallBold);
        invoice.add("Order id: ");
        invoice.setFont(normal);
        invoice.add(order.id + "\n\n\n");
        ph.setFont(catFont);
        invoice.add("Items purchased" + "\n\n");
        invoice.setFont(normal);
        for(int i = 0; i < order.items.size(); i++){
            invoice.add(order.items.get(i).getName() + " ( x" + order.quantities.get(i) + " )" + "          " + order.quantities.get(i)+"x"+order.items.get(i).getPrice()+"\n");
        }
        invoice.setFont(smallBold);
        invoice.add("Total Cost: ");
        invoice.setFont(normal);
        invoice.add(order.cost.toString() + "\n\n");
        invoice.setFont(smallBold);
        invoice.add("THANK YOU FOR SHOPPING WITH MANGWALO PAKISTAN");

        document.add(ph);
        document.add(invoice);
        document.newPage();
    }

    private void addMetaData(Document document) {
        document.addTitle("Invoice");
        document.addSubject("Mangwalo Pakistan");
        document.addKeywords("TAG");
        document.addAuthor("TAG");
        document.addCreator("TAG");
    }

    public void back(View view) {
        setResult(CommonStatusCodes.SUCCESS);

        intent = new Intent(this, DownloadService.class);
        intent.putExtra(DownloadService.FILENAME, "Name.pdf");
        startService(intent);
        removeOrderFromList();
        stopService(intent);
    }

    private void removeOrderFromList() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Orders");
        dbRef.child(order.id).child("paid").setValue(true);
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void ignore(View view) {
        removeOrderFromList();
    }
}
