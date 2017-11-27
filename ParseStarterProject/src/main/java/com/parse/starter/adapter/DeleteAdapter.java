package com.parse.starter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.starter.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DeleteAdapter extends ArrayAdapter<ParseObject> {

    private Context context;
    private ArrayList<ParseObject> posts;
    private TextView postNameUser;
    private TextView postDesc;
    private ImageView imgPostDelete;

    public DeleteAdapter(Context c, ArrayList<ParseObject> objects) {
        super(c, 0, objects);
        this.context = c;
        this.posts = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {

            //Inicializa objeto para montagem do layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //monta a view a partir do xml
            view = inflater.inflate(R.layout.lista_delete, parent, false);

        }

        if (posts != null && posts.size() > 0) {

            //Recuperar elementos para exibição
            imgPostDelete = (ImageView) view.findViewById(R.id.image_lista_delete);
            postNameUser = (TextView) view.findViewById(R.id.txtListaNomeDelete);
            postDesc = (TextView) view.findViewById(R.id.txtListaDelete);
            ParseObject parseObject = posts.get(position);
            Picasso.with(context)
                    .load(parseObject.getParseFile("imagem").getUrl())
                    .fit()
                    .into(imgPostDelete);
            postNameUser.setText(context.getString(R.string.usuario_postagem)+" "+parseObject.getString("username"));
            postDesc.setText(context.getString(R.string.desc_catalogo)+" "+parseObject.getString("imageabout"));
        }else{
            Toast.makeText(getContext(),"Ocorreu um problema...",Toast.LENGTH_SHORT).show();
        }


        return view;
    }
}
