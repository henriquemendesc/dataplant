/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.parse.starter.R;
import com.parse.starter.adapter.TabsAdapter;
import com.parse.starter.fragments.HomeFragment;
import com.parse.starter.util.PermissionUtils;
import com.parse.starter.util.SlidingTabLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE_PERMISSION_ACCESS_COURSE_LOCATION = 1;
    private Toolbar toolbarPrincipal;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private GoogleApiClient mGoogleApiClient;
    private String[] permissoes;
    private double latitude;
    private double longitude;
    private LatLng localizacao;
    //constantes para a cidade de pacarembi
    private static double LAT = -25.460373;//-22.6078;
    private static double LNG = -49.280547;//-43.7108;
    private static String CITY = "Paracambi";//-43.7108;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Configura toolbar
        toolbarPrincipal = (Toolbar) findViewById(R.id.toolbar_principal);
        toolbarPrincipal.setLogo(R.drawable.dataplant);
        setSupportActionBar(toolbarPrincipal);

        //Configura abas
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tab_main);
        viewPager = (ViewPager) findViewById(R.id.view_pager_main);

        //configurar adapter
        TabsAdapter tabsAdapter = new TabsAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(tabsAdapter);
        slidingTabLayout.setCustomTabView(R.layout.tab_view, R.id.text_item_tab);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.cinzaEscuro));
        slidingTabLayout.setViewPager(viewPager);

        /**
         * Começa a verificar a API do google para localização
         */

        //método para conexão com a API Google
        buildGoogleApiClient();
        if (mGoogleApiClient != null) {
            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
        } else {
            buildGoogleApiClient();
        }

        //permissões
        if (!getPermissions()) {
            // Solicita as permissões
            permissoes = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
            };
            PermissionUtils.validate(this, 0, permissoes);
        }

/*        if (!GPSAtivo()){
            Toast.makeText(this,"Seu GPS não está ativo, ative-o para compartilhar fotos",Toast.LENGTH_SHORT).show();
        }*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_sair:
                //fazer algo
                deslogarUsuario();
                return true;
            case R.id.action_configuracoes:
                return true;
            case R.id.action_compartilhar:
                compartilharFoto();
                return true;
            case R.id.action_delete:
                deletePost();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deletePost() {
        Intent intent = new Intent(this, DeletePostsActivity.class);
        startActivity(intent);
    }

    /**
     * Alterado por Henrique(Bob)
     * 21/11/2017
     */
    private void compartilharFoto() {
        List<android.location.Address> addresses;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        /**
         * O compartilhamento de foto só é permitido dentro da localização especificada
         * private static String CITY = "Paracambi"
         */
        try {
            //Verifica se há permissão de localização
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSION_ACCESS_COURSE_LOCATION);
            } else {
                //se há permissão, cria a variável com as coordenadas
                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) { //verifica se é nulo, a localização
                    localizacao = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    addresses = geocoder.getFromLocation(localizacao.latitude, localizacao.longitude, 1);
                    String city = addresses.get(0).getLocality(); //pega a cidade
                    if (city.equals(CITY)) {//verifica se a cidade é a permitida, se sim, abre a tela de fotos.
                        Intent intent = new Intent(this, PhotoActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Você não pode compartilhar foto, está fora da localização permitida", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Nao foi possível busca sua Localização, verifique seu GPS", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        /*}else {
            Toast.makeText(this,"Seu GPS não está ativo, ative-o para compartilhar fotos",Toast.LENGTH_SHORT).show();
        }*/
    }

    private void deslogarUsuario() {
        ParseUser.logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    protected synchronized void buildGoogleApiClient() {
        //Toast.makeText(this,"buildGoogleApiClient",Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //verifica se a permissão já foi concedida, deveria ser um método universal, na classe util, mas, falta-me tempo para refatoração
    private boolean getPermissions() {
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public boolean GPSAtivo() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
