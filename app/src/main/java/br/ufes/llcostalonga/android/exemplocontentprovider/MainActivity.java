/*
   Esse código foi iniciamente baseado no
   https://www.youtube.com/watch?v=eNW1d8tiXmQ&list=PLfuE3hOAeWhb7kirY4rBtTLVF73jEXwG4&index=2

   Problemas de segurança no acesso as dados do telefone ( > 6.0) obrigaram um mudança como mostrado em
   http://www.javahelps.com/2015/10/android-60-runtime-permission-model.html

   O código não representa o modo correto de acessar um Content Provider pois o faz na Thread da UI.
   O correto é usar um Loader, conforme será demonstrado posteriormente.

   Como usar esse código:
   1) Adicione 2 ou mais contatos no telefone;
   2) Rode o aplicativo. Está vendo os contatos?
   3) Com o aplicativo rodando, adicione um novo contato. O que aconteceu?
   4) Modifique o contentResolver.query() para usar o mSelectionClause e mSelectionArguments.

 */



package br.ufes.llcostalonga.android.exemplocontentprovider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity   {

   private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private TextView textViewQueryResult;
    private ContentResolver contentResolver;

    private CursorLoader mContactsLoader;

    private String[] mColumnProjection = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.CONTACT_STATUS,
            ContactsContract.Contacts.HAS_PHONE_NUMBER};

    private String mSelectionClause = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " = ?";

    private String[] mSelectionArguments = new String[]{"Leandro"};

    private String mOrderBy = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textViewQueryResult = (TextView) findViewById(R.id.textViewQueryResult);

        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            runQuery();
        }


    }

    public void runQuery(){
        ContentResolver contentResolver=getContentResolver();

        //Alternativa 1
         Cursor cursor=contentResolver.query(                 //select
                ContactsContract.Contacts.CONTENT_URI,       //from
                mColumnProjection,                           //where
                null,
                null,
                null);

       //Alternativa 2
        /*Cursor cursor=contentResolver.query(                 //select
                ContactsContract.Contacts.CONTENT_URI,       //from
                mColumnProjection,                           //where
                mSelectionClause,
                mSelectionArguments,
                null);*/

        if(cursor!=null && cursor.getCount()>0){
            StringBuilder stringBuilderQueryResult=new StringBuilder("");
            while (cursor.moveToNext()){
                stringBuilderQueryResult.append(cursor.getString(0)+" , "+cursor.getString(1)+" , "+cursor.getString(2)+"\n");
            }
            textViewQueryResult.setText(stringBuilderQueryResult.toString());
        }else{
            textViewQueryResult.setText("No Contacts in device");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                runQuery();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }



}