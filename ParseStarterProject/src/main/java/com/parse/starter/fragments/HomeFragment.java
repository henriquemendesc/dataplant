package com.parse.starter.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.R;
import com.parse.starter.activity.FloatingActivity;
import com.parse.starter.adapter.HomeAdapter;
import com.parse.starter.util.ProgressDialogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private ListView listView;
    private ArrayList<ParseObject> postagens;
    private ArrayAdapter<ParseObject> adapter;
    private ParseQuery<ParseObject> query;
    private TextView txtHomeFragment;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        /*
         Montar Listview e adapter
        */
        postagens = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.list_postagens_home);
        adapter = new HomeAdapter( getActivity(), postagens );
        listView.setAdapter( adapter );
        txtHomeFragment = (TextView)view.findViewById(R.id.txtHomeFragment);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseObject obj = adapter.getItem(position);
                openFloatActivity(obj);
            }
        });

        return view;
    }

    private void openFloatActivity(ParseObject obj) {
        String urlImage;
        String userAbout;

        urlImage = obj.getParseFile("imagem").getUrl();
        userAbout = getActivity().getString(R.string.usuario_postagem) + " " + obj.getString("username") + "\n" +
                getActivity().getString(R.string.postagem_descricao) + " " + obj.getString("imageabout");

        Bundle bundle = new Bundle();
        Intent intent = new Intent(getActivity(), FloatingActivity.class);
        bundle.putString("image",urlImage);
        bundle.putString("user",userAbout);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Criado por Henrique Mendes (Bob)
     * 21/11/2017
     * Para que as postagens novas apareçam, ao voltar da tela de fotos
     * deve-se alterar para o onStart do fragment/activity pois ele passa por este método
     * ao voltar com a tela para o usuário e também passa por aqui ao criar a tela de inicio
     */
    @Override
    public void onStart(){
        super.onStart();
        //recupera postagens
        getPostagens();
    }

    /**
     * Alterado por Henrique Mendes(Bob)
     * 22/11/2017
     */
    private void getPostagens(){
        final ProgressDialogUtils progress = new ProgressDialogUtils();
        progress.startProgress(getActivity());
        /*
         Recupera imagens das postagens
        */
        query = ParseQuery.getQuery("Imagem");
        query.whereNotEqualTo("imagecatalog","sim");
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                progress.endProgress(getActivity());
                if( e==null ){//sucesso

                    if( objects.size()>0 ){
                        if(txtHomeFragment.getVisibility() == View.VISIBLE){//retira o texto da tela se usuário fizer nova postagem
                            txtHomeFragment.setVisibility(View.GONE);
                        }
                        postagens.clear();
                        for (ParseObject parseObject :  objects ){
                            postagens.add( parseObject );
                        }
                        adapter.notifyDataSetChanged();
                    }else{
                        txtHomeFragment.setVisibility(View.VISIBLE);//mostra um texto somente para que a tela não fique em branco
                    }

                }else{//erro
                    e.printStackTrace();
                }

            }
        });


    }

    public void atualizaPostagens(){
        getPostagens();
    }

}
