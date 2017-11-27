package com.parse.starter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.starter.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends ArrayAdapter<ParseObject> {

    private Context context;
    private ArrayList<ParseObject> postagens;
    private TextView txtPostagemDesc;
    private TextView txtPostagemUser;

    public HomeAdapter(Context c, ArrayList<ParseObject> objects) {
        super(c, 0, objects);
        this.context = c;
        this.postagens = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        /*
            Verifica se não existe o objeto view criado,
            pois a view utilizada é armazenado no cache do android e fica na variável
            convertView
        */
        if (view == null) {

            //Inicializa objeto para montagem do layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //monta a view a partir do xml
            view = inflater.inflate(R.layout.lista_postagem, parent, false);

        }

        //verifica se existe postagens
        if (postagens != null && postagens.size() > 0) {

            //recupera componentes da tela
            ImageView imagemPostagem = (ImageView) view.findViewById(R.id.image_lista_postagem);
            //txtPostagemDesc = (TextView)view.findViewById(R.id.txtListaPostagem);
            txtPostagemUser = (TextView) view.findViewById(R.id.txtListaNomePostagem);

            ParseObject parseObject = postagens.get(position);

            //parseObject.getParseFile("imagem")
            Picasso.with(context)
                    .load(parseObject.getParseFile("imagem").getUrl())
                    .fit()
                    .into(imagemPostagem);
            //objetos com a descrição e nome do usuário
/*            if(parseObject.getString("imageabout") == null){
                txtPostagemDesc.setText(R.string.descricao_image_adapter);
            }else {
                txtPostagemDesc.setText(context.getString(R.string.postagem_descricao)+" "+parseObject.getString("imageabout"));
            }*/
            if (parseObject.getString("username") == null) {
                txtPostagemUser.setText(R.string.usuario_postagem);
            } else {
                txtPostagemUser.setText(context.getString(R.string.usuario_postagem) + " " + parseObject.getString("username") + "\n" +
                        context.getString(R.string.postagem_descricao) + " " + parseObject.getString("imageabout"));
            }

        }

        return view;

    }
}
