package com.parse.starter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.starter.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Bob on 22/11/2017.
 */

public class CatalogAdapter extends ArrayAdapter<ParseObject> {

    private Context context;
    private List<ParseObject> listCatalogs;
    private TextView txtNomeCatalogo;
    private TextView txtDescCatalogo;

    public CatalogAdapter(Context context, List<ParseObject> catalogs) {
        super(context, 0, catalogs);
        this.context = context;
        this.listCatalogs = catalogs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.lista_catalogo, parent, false);

        }

        if (listCatalogs != null && listCatalogs.size() > 0) {

            ImageView imagemCatalogo = (ImageView) view.findViewById(R.id.image_lista_catalogo);
            txtNomeCatalogo = (TextView) view.findViewById(R.id.txtListaCatalogoNome);
            //txtDescCatalogo = (TextView) view.findViewById(R.id.txtListaCatalogoDesc);

            ParseObject parseObject = listCatalogs.get(position);

            Picasso.with(context)
                    .load(parseObject.getParseFile("imagem").getUrl())
                    .fit()
                    .into(imagemCatalogo);

            txtNomeCatalogo.setText(view.getContext().getString(R.string.nome)+" "+parseObject.getString("imagename") +"\n"+
                    view.getContext().getString(R.string.desc_catalogo)+" "+parseObject.getString("imageabout"));


        }
        return view;
    }
}
