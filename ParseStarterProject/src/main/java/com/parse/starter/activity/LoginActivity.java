package com.parse.starter.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.R;
import com.parse.starter.persistence.SaveSharedPreferences;
import com.parse.starter.util.ProgressDialogUtils;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText editLoginUsuario;
    private EditText editLoginSenha;
    private Button botaoLogar;
    private ParseQuery<ParseObject> query;
    private ProgressDialogUtils progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editLoginUsuario = (EditText) findViewById(R.id.edit_login_usuario);
        editLoginSenha = (EditText) findViewById(R.id.edit_login_senha);
        botaoLogar = (Button) findViewById(R.id.button_logar);
        progress = new ProgressDialogUtils();
        //ParseUser.logOut();

        //Verificar se o usuário está logado
        verificarUsuarioLogado();

        //adiciona evento de click no botão logar
        botaoLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.startProgress(getContext());
                String usuario = editLoginUsuario.getText().toString();
                String senha = editLoginSenha.getText().toString();

                verificarLogin(usuario, senha);

            }
        });

    }

    private void verificarLogin(String usuario, String senha) {
        ParseUser.logInInBackground(usuario, senha, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {//sucesso no login
                    progress.endProgress(getContext());
                    SaveSharedPreferences.setUserId(getContext(),user.getObjectId());
                    isUserAdmin(user.getObjectId(), ParseUser.getCurrentUser().getString("username"), ParseUser.getCurrentUser().getString("email"));
                    Toast.makeText(LoginActivity.this, "Login realizado com sucesso!!", Toast.LENGTH_LONG).show();
                    abrirAreaPrincipal();
                } else {//erro ao logar
                    progress.endProgress(getContext());
                    Toast.makeText(LoginActivity.this, "Erro ao fazer login, "
                            + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void abrirCadastroUsuario(View view) {
        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);
    }

    /**
     * Alterado por Henrique Mendes(Bob)
     * 22/11/2017
     * Para verificação se o usuário é admin, para que ele possa adicionar itens ao catálogo.
     */
    private void verificarUsuarioLogado() {
        if (ParseUser.getCurrentUser() != null) {
            String user_id = ParseUser.getCurrentUser().getObjectId();
            SaveSharedPreferences.setUserId(getContext(),user_id);
            isUserAdmin(user_id, ParseUser.getCurrentUser().getString("username"), ParseUser.getCurrentUser().getString("email"));
            //Enviar usuário para tela principal do App
            abrirAreaPrincipal();
        }

    }

    private void isUserAdmin(String userid, String username, String useremail) {

        SaveSharedPreferences.setUserEmail(this,useremail);
        SaveSharedPreferences.setUserName(this,username);

        //verifica se usuário é admin, para o catálogo
        ParseQuery queryUser = ParseUser.getQuery();
        queryUser.whereEqualTo("objectId", userid);

        queryUser.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {//sucesso

                    if (objects != null && objects.size() > 0) {
                        if(objects.get(0).getString("useradmin") != null && objects.get(0).getString("useradmin").equals("admin")){
                            //se o usuário tiver a TAG admin no Parse, ele grava no aplicativo para utilização posterior
                            SaveSharedPreferences.setUserAdmin(getContext(),true);
                        }else{
                            SaveSharedPreferences.setUserAdmin(getContext(),false);
                        }
                    }

                } else {//erro
                    e.printStackTrace();
                }

            }
        });
    }

    private void abrirAreaPrincipal() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private Context getContext(){
        return this;
    }

}
