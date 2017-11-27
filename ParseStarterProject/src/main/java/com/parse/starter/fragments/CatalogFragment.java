package com.parse.starter.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.R;
import com.parse.starter.activity.CadastroActivity;
import com.parse.starter.activity.MainActivity;
import com.parse.starter.activity.PhotoActivity;
import com.parse.starter.adapter.CatalogAdapter;
import com.parse.starter.adapter.HomeAdapter;
import com.parse.starter.persistence.SaveSharedPreferences;
import com.parse.starter.util.ProgressDialogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bob on 22/11/2017.
 */
public class CatalogFragment extends Fragment {

    private static final String CATALOGO_LIST = "sim";
    private ArrayList<ParseObject> catalogo;
    private ArrayAdapter<ParseObject> adapter;
    private ParseQuery<ParseObject> query;
    private FloatingActionButton floatingActionButton;
    private ListView listView;
    private TextView txtCatalogo;
    private ProgressDialogUtils progress;
    private View parentLayout;

    public CatalogFragment() {
        // Required empty public constructor
    }

    public static CatalogFragment newInstance(String param1, String param2) {
        CatalogFragment fragment = new CatalogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);
        parentLayout = getActivity().findViewById(android.R.id.content);

        progress = new ProgressDialogUtils();
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floatButtonCatalog);
        if (SaveSharedPreferences.getUserAdmin(getActivity())) {//só ficará visível para usuário administrador
            floatingActionButton.setVisibility(View.VISIBLE);
        }
        if(floatingActionButton.getVisibility() == View.VISIBLE){
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SaveSharedPreferences.setIsCatalog(getContext(),true);
                    Intent intent = new Intent(getActivity(), PhotoActivity.class);
                    startActivity(intent);
                }
            });
        }
        txtCatalogo = (TextView)view.findViewById(R.id.txtCatalogoEmpty);

        catalogo = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.listCatalog);
        adapter = new CatalogAdapter( getActivity(), catalogo );
        listView.setAdapter( adapter );

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart(){
        super.onStart();
        getCatalogo();
    }

    private void getCatalogo() {
        progress.startProgress(getActivity());

        query = ParseQuery.getQuery("Imagem");
        query.whereEqualTo("imagecatalog", CATALOGO_LIST);
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                progress.endProgress(getActivity());
                if( e==null ){//sucesso

                    if( objects.size()>0 ){
                        if(txtCatalogo.getVisibility() == View.VISIBLE){//retira o texto da tela se houver catálogo
                            txtCatalogo.setVisibility(View.GONE);
                        }
                        catalogo.clear();
                        for (ParseObject parseObject :  objects ){
                            catalogo.add( parseObject );
                        }
                        adapter.notifyDataSetChanged();
                    }else{
                        txtCatalogo.setVisibility(View.VISIBLE);//mostra um texto somente para que a tela não fique em branco
                    }

                }else{//erro
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * Criado por Henrique Mendes (bob)
     * 23/11/2017
     * menu para busca na tela de catalogos.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        final MenuItem item = menu.findItem(R.id.search);
        item.setVisible(true);
        final SearchView searchView = new SearchView(((MainActivity) getContext()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        SearchView.SearchAutoComplete txtArea = (SearchView.SearchAutoComplete)searchView.findViewById(R.id.search_src_text);
        txtArea.setTextColor(getResources().getColor(R.color.preto));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                findItemOnSearch(searchView.getQuery().toString(), searchView, item);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {

                return true;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                            findItemOnSearch(searchView.getQuery().toString(), searchView, item);
                                          }
                                      }
        );
    }

    private void findItemOnSearch(String nome, final SearchView searchView, final MenuItem item) {
        progress.startProgress(getActivity());

        query = ParseQuery.getQuery("Imagem");
        query.whereEqualTo("imagecatalog", CATALOGO_LIST);
        query.whereMatches("imagename",nome);
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                progress.endProgress(getActivity());
                hideKeyboard(searchView);
                if( e==null ){//sucesso
                    item.collapseActionView();
                    if( objects.size()>0 ){
                        if(txtCatalogo.getVisibility() == View.VISIBLE){//retira o texto da tela se houver catálogo
                            txtCatalogo.setVisibility(View.GONE);
                        }
                        catalogo.clear();
                        for (ParseObject parseObject :  objects ){
                            catalogo.add( parseObject );
                        }
                        adapter.notifyDataSetChanged();
                    }else{
                        Snackbar.make(parentLayout, getString(R.string.empty_search), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                }else{//erro
                    e.printStackTrace();
                }

            }
        });
    }

    private void hideKeyboard(SearchView searchView) {
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }
}
