package com.parse.starter.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.R;
import com.parse.starter.adapter.DeleteAdapter;
import com.parse.starter.persistence.SaveSharedPreferences;
import com.parse.starter.util.ProgressDialogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bob on 21/11/2017.
 * Maneira para deletar o post do usuário.
 */
public class DeletePostsActivity extends AppCompatActivity {

    private String DELETE_LIST;
    private ArrayList<ParseObject> delete;
    private ArrayAdapter<ParseObject> adapter;
    private ParseQuery<ParseObject> query;
    private ListView listView;
    private TextView txtDelete;
    private ProgressDialogUtils progress;
    private View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_posts);
        parentLayout = this.findViewById(android.R.id.content);

        DELETE_LIST = SaveSharedPreferences.getUserId(this);
        progress = new ProgressDialogUtils();

        txtDelete = (TextView) findViewById(R.id.txtDeleteEmpty);
        delete = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listDelete);
        adapter = new DeleteAdapter(this, delete);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ParseObject obj = adapter.getItem(position);
                deleteItem(obj.getObjectId(), position);
            }
        });

        Toast.makeText(this, getString(R.string.item_click_delete), Toast.LENGTH_LONG)
                .show();
    }

    private void deleteItem(final String objectId, final int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.delete_post_title))
                .setMessage(getString(R.string.delete_post_message))
                .setPositiveButton(getString(R.string.positive_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progress.startProgress(getContext());
                        query = ParseQuery.getQuery("Imagem");
                        query.whereEqualTo("objectId", objectId);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    if (objects.size() > 0) {
                                        objects.get(position).deleteInBackground(new DeleteCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    progress.endProgress(getContext());
                                                    adapter.notifyDataSetChanged();
                                                    Toast.makeText(getContext(), getString(R.string.deleted_item), Toast.LENGTH_SHORT).show();
                                                    Snackbar.make(parentLayout, getString(R.string.deleted_item), Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                    adapter.clear();
                                                    getPostsToDelete();
                                                } else {
                                                    Snackbar.make(parentLayout, getString(R.string.error_to_delete), Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                    progress.endProgress(getContext());
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    Snackbar.make(parentLayout, getString(R.string.error_to_delete), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    progress.endProgress(getContext());
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(getString(R.string.negative_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPostsToDelete();
    }

    //catch the posts from user to show
    private void getPostsToDelete() {
        progress.startProgress(this);

        query = ParseQuery.getQuery("Imagem");
        query.whereEqualTo("imageuserid", SaveSharedPreferences.getUserId(getContext()));
        query.whereNotEqualTo("imagecatalog", "sim");
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                progress.endProgress(getContext());
                if (e == null) {//sucesso

                    if (objects.size() > 0) {
                        if (txtDelete.getVisibility() == View.VISIBLE) {//retira o texto da tela se houver catálogo
                            txtDelete.setVisibility(View.GONE);
                        }
                        delete.clear();
                        for (ParseObject parseObject : objects) {
                            delete.add(parseObject);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        txtDelete.setVisibility(View.VISIBLE);//mostra um texto somente para que a tela não fique em branco
                    }

                } else {//erro
                    e.printStackTrace();
                }

            }
        });
    }

    private Context getContext() {
        return this;
    }
}
