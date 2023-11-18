package com.claudio.aulaiv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity implements ContactAdapter.IOnItemClickListener {

    private ArrayList<Contact> contactArrayList;
    private ContactAdapter contactAdapter;

    private RecyclerView recyclerView;

    private EditText etName, etPhone;

    private Button btAddContact;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);


        recyclerView = findViewById(R.id.recyclerView);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        btAddContact = findViewById(R.id.btAddContact);


        contactArrayList = new ArrayList<>();
        contactAdapter = new ContactAdapter(contactArrayList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactAdapter);

        if(!checkPermission()) {
            requestPermissions();
        } else {
            getContacts();
            updateRecyclerView();
        }

        btAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContact(etName.getText().toString().trim(), etPhone.getText().toString().trim());
                updateRecyclerView();
            }
        });
    }

    private void requestPermissions() {
        if(checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    private void addContact(String name, String phone) {
        ContentResolver contentResolver = getContentResolver();
        ArrayList<ContentProviderOperation> contentProviderResults = new ArrayList<>();

        contentProviderResults.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        contentProviderResults.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());

        contentProviderResults.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, contentProviderResults);
            Toast.makeText(this, "Contato adicionado com sucesso", Toast.LENGTH_LONG).show();
        } catch (RemoteException | OperationApplicationException e) {
            Toast.makeText(this, "Erro ao adicionar contato", Toast.LENGTH_LONG).show();
        }
    }


    private ArrayList<Contact> getContacts() {
        ArrayList<Contact> contacts = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};

        @NonNull
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIndex);
                String phoneNumber = cursor.getString(numberIndex);
                contacts.add(new Contact(name, phoneNumber));
            }

            cursor.close();
        }

        return contacts;
    }

    private void updateRecyclerView() {
        contactArrayList.clear();
        contactArrayList.addAll(getContacts());
        contactAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateRecyclerView();
            }
        }
    }

    @Override
    public void onItemClick(int position) {
//        Carregar a tela de edicao
    }

    @Override
    public void onDeleteClick(int position) {
//        chamar o metodo para delete no banco de dados
    }
}