package com.parse.starter.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.starter.R;
import com.parse.starter.persistence.SaveSharedPreferences;
import com.parse.starter.util.ParseErros;
import com.parse.starter.util.ProgressDialogUtils;

public class CadastroActivity extends AppCompatActivity {

    private EditText textoUsuario;
    private EditText textoEmail;
    private EditText textoSenha;
    private TextView facaLogin;
    private Button botaoCadastrar;
    private ProgressDialogUtils progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        textoUsuario = (EditText) findViewById(R.id.text_usuario);
        textoEmail = (EditText) findViewById(R.id.text_email);
        textoSenha = (EditText) findViewById(R.id.text_senha);
        facaLogin = (TextView) findViewById(R.id.text_faca_login);
        botaoCadastrar = (Button) findViewById(R.id.button_cadastrar);
        progress = new ProgressDialogUtils();
        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.startProgress(getContext());
                cadastrarUsuario();
            }
        });
        facaLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirLoginUsuario();
            }
        });

    }

    private Context getContext() {
        return this;
    }

    private void cadastrarUsuario(){

        //Cria objeto usuario
        final ParseUser usuario = new ParseUser();
        usuario.setUsername( textoUsuario.getText().toString() );
        usuario.setEmail( textoEmail.getText().toString() );
        usuario.setPassword( textoSenha.getText().toString() );

        //salva dados do usuario
        usuario.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                progress.endProgress(getContext());
                if( e==null ){//sucesso ao salvar
                    Toast.makeText(CadastroActivity.this, "Cadastro feito com sucesso!", Toast.LENGTH_LONG).show();
                    SaveSharedPreferences.setUserId(getContext(),usuario.getObjectId());
                    abrirLoginUsuario();
                }else{//erro ao salvar
                    ParseErros parseErros = new ParseErros();
                    String erro = parseErros.getErro( e.getCode() );
                    Toast.makeText(CadastroActivity.this, erro , Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void abrirLoginUsuario(){
        Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
