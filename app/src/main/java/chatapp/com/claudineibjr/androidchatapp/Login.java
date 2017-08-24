package chatapp.com.claudineibjr.androidchatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class Login extends AppCompatActivity {

    //Firebase Authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Campos da tela de login
    private EditText txtLoginEmail;
    private EditText txtLoginSenha;
    private Button btnLogin;

    //Campos da tela de cadastro
    private EditText txtCadastroNome;
    private EditText txtCadastroEmail;
    private EditText txtCadastroSenha;
    private Button btnCadastrar;

    //Variável que controla se o usuário está sendo cadastrado ou não
    private boolean cadastro = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preparaAutenticacao();

        preparaAbas();
        instanciaElementosVisuais();

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mAuth.addAuthStateListener(mAuthListener);
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "onStart\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try{
            if (mAuthListener != null) {
                mAuth.removeAuthStateListener(mAuthListener);
            }
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "onStop\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void preparaAutenticacao() {

        try{

            FirebaseApp.initializeApp(this);

            mAuth = FirebaseAuth.getInstance();

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {

                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        Usuario usuarioLogado = null;

                        if (firebaseUser != null) {
                            usuarioLogado = new Usuario(firebaseUser.getUid(), firebaseUser.getEmail());

                            //Indo para a próxima tela
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.putExtra("usuarioLogado", usuarioLogado);
                            intent.putExtra("cadastro", cadastro);

                            startActivity(intent);

                        }

                        // User is signed in
                        //Toast.makeText(getApplicationContext(), "onAuthStateChanged:signed_in:" + user.getUid(), Toast.LENGTH_SHORT).show();
                    } else {
                        // User is signed out
                        //Toast.makeText(getApplicationContext(), "onAuthStateChanged:signed_out", Toast.LENGTH_SHORT).show();
                    }
                }
            };
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "preparaAutenticacao\n\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void instanciaElementosVisuais() {
        //Campos da tela de login
        txtLoginEmail = (EditText) findViewById(R.id.txtLoginEmail);
        txtLoginSenha = (EditText) findViewById(R.id.txtLoginSenha);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        //Campos da tela de cadastro
        txtCadastroNome = (EditText) findViewById(R.id.txtCadastroNome);
        txtCadastroEmail = (EditText) findViewById(R.id.txtCadastroEmail);
        txtCadastroSenha = (EditText) findViewById(R.id.txtCadastroSenha);
        btnCadastrar = (Button) findViewById(R.id.btnCadastro);

        //Listener acionado quando clicar em login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(txtLoginEmail.getText().toString(), txtLoginSenha.getText().toString());
            }
        });

        //Listener acionado quando clicar em cadastrar
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrar(txtCadastroEmail.getText().toString(), txtCadastroNome.getText().toString(), txtCadastroSenha.getText().toString());
            }
        });

    }

    private void cadastrar(final String email, String nome, final String senha) {
        ArrayList<EditText> campos = new ArrayList<>(); campos.add(txtCadastroEmail);  campos.add(txtCadastroNome); campos.add(txtCadastroSenha);
        if (camposPreenchidos(campos)){

            mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Autenticação falhou\n\n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        cadastro = true;
                        //Toast.makeText(getApplicationContext(), "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void login(String email, String senha) {
        ArrayList<EditText> campos = new ArrayList<>(); campos.add(txtLoginEmail);  campos.add(txtLoginSenha);
        if (camposPreenchidos(campos) == true){

            mAuth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful())
                                Toast.makeText(getApplicationContext(), "signInWithEmail:failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }else{
            Toast.makeText(getApplicationContext(), "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean camposPreenchidos(ArrayList<EditText> campos){

        for (int iCount = 0; iCount < campos.size(); iCount++){
            if (campos.get(iCount).getText().toString().equals("")){
                return false;
            }
        }

        return true;
    }

    private void preparaAbas() {
        TabHost abas = (TabHost) findViewById(R.id.loginTabHost);
        abas.setup();

        TabHost.TabSpec descritor = abas.newTabSpec("aba1");

        descritor.setContent(R.id.tabLogin);
        descritor.setIndicator("Login");
        abas.addTab(descritor);

        descritor = abas.newTabSpec("aba2");
        descritor.setContent(R.id.tabCadastro);
        descritor.setIndicator("Cadastro");

        abas.addTab(descritor);
    }

}
