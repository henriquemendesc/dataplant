package com.parse.starter.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;
import com.parse.starter.adapter.TabsAdapter;
import com.parse.starter.fragments.HomeFragment;
import com.parse.starter.persistence.SaveSharedPreferences;
import com.parse.starter.util.ProgressDialogUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by Bob on 21/11/2017.
 * Activity que recebe a foto e espera uma descrição
 * ao salvar leva para o parse
 */
public class PhotoActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 100;
    static final int REQUEST_PHOTO_CAPTURE = 101;
    private static final Object IS_CATALOG = "sim";
    private EditText edtPhoto;
    private ImageView imgPhoto;
    private Button btnSavePhoto;
    private String filePath = "";
    private Uri uri = null;
    private File file;
    private Bitmap image;
    private View parentLayout;
    private EditText edtName;
    private TextView txtName;
    private ProgressDialogUtils progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        parentLayout = findViewById(android.R.id.content);

        edtPhoto = (EditText) findViewById(R.id.edtPhoto);
        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        //se o usuário cancelar o dialog ele pode clicar na foto e escolher o que quer fazer
        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogPhoto();
            }
        });
        btnSavePhoto = (Button) findViewById(R.id.btnPhoto);
        btnSavePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.startProgress(getContext());
                savePhoto();
            }
        });
        edtName = (EditText)findViewById(R.id.edtNamePhoto);
        txtName = (TextView)findViewById(R.id.txtNamePhoto);
        progress = new ProgressDialogUtils();

        if(SaveSharedPreferences.getIsCatalog(this)){
            edtName.setVisibility(View.VISIBLE);
            txtName.setVisibility(View.VISIBLE);
        }

        //ao abrir a activity o usuário irá escolher o que irá fazer
        alertDialogPhoto();
    }

    private Context getContext() {
        return this;
    }

    private void savePhoto() {
        try {
            ParseObject parseObject = new ParseObject("Imagem");

            if (edtName.getVisibility() == View.VISIBLE && (!edtName.getText().toString().equals(""))){
                parseObject.put("imagename", edtName.getText().toString());
            }else if(edtName.getVisibility() == View.VISIBLE && edtName.getText().toString().equals("")){
                Snackbar.make(parentLayout, getString(R.string.emptyname_photo), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                progress.endProgress(getContext());
                return;
            }

            //comprimir no formato PNG
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 75, stream);
            //Cria um array de bytes da imagem
            byte[] byteArray = stream.toByteArray();

            //Criar um arquivo com formato próprio do parse
            SimpleDateFormat dateFormat = new SimpleDateFormat("ddmmaaaahhmmss");
            String nomeImagem = dateFormat.format(new Date());
            ParseFile arquivoParse = new ParseFile(nomeImagem + "imagem.png", byteArray);

            //Monta objeto para salvar no parse
            parseObject.put("username", ParseUser.getCurrentUser().getUsername());
            parseObject.put("imageuserid", ParseUser.getCurrentUser().getObjectId());
            parseObject.put("imagem", arquivoParse);
            if(!edtPhoto.getText().toString().isEmpty() || !edtPhoto.getText().toString().equals("")) {
                parseObject.put("imageabout", edtPhoto.getText().toString());
            }else{
                parseObject.put("imageabout", getString(R.string.descricao_image_adapter));
            }
            if(SaveSharedPreferences.getIsCatalog(this)){
                parseObject.put("imagecatalog", IS_CATALOG);
            }

            //salvar os dados
            parseObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    progress.endProgress(getContext());
                    if (e == null) {//sucesso
                        Snackbar.make(parentLayout, getString(R.string.uploadsuccess_photo), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        imgPhoto.setImageBitmap(null);
                        imgPhoto.setImageResource(R.drawable.galeria);
                        edtPhoto.setText("");
                        if(edtName.getVisibility() == View.VISIBLE) {
                            edtName.setText("");
                        }

                    } else {//erro
                        progress.endProgress(getContext());
                        Snackbar.make(parentLayout, getString(R.string.uploadwarning_photo), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                }
            });
        } catch (Exception e) {
            progress.endProgress(getContext());
            e.printStackTrace();
        }
    }

    private void alertDialogPhoto() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.title_dialog_photo))
                .setMessage(getString(R.string.dialog_message_photo))
                .setPositiveButton(getString(R.string.dialog_galeria_photo), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.takephoto_dialog_photo), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_PHOTO_CAPTURE);
                        }
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Testar processo de retorno dos dados
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            //recuperar local do recurso
            Uri localImagemSelecionada = data.getData();

            //recupera a imagem do local que foi selecionada
            try {
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                imgPhoto.setImageBitmap(image);
               // uri = getImageUri(this, image);
                //file = new File(getRealPathFromURI(uri));
                filePath = localImagemSelecionada.getPath();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == REQUEST_PHOTO_CAPTURE && resultCode == RESULT_OK) {
            try {
                Bundle extras = data.getExtras();
                image = (Bitmap) extras.get("data");
                imgPhoto.setImageBitmap(image);
                uri = getImageUri(this, image);
                file = new File(getRealPathFromURI(uri));
                filePath = file.getPath();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    public void onBackPressed(){
        SaveSharedPreferences.setIsCatalog(this,false);
        super.onBackPressed();
    }
}
