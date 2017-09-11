package chatapp.com.claudineibjr.androidchatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    //Firebase Autenticação
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

        //Assim que a tela iniciar, prepara a autenticação
        preparaAutenticacao();

        //Prepara as abas contidas no TabHost
        preparaAbas();

        //Instancia os elementos visuais, permitindo que recebam e tenham valor atribuído
        instanciaElementosVisuais();

    }

    //Função acionada quando o app é iniciado
    @Override
    protected void onStart() {
        super.onStart();
        try {
            mAuth.addAuthStateListener(mAuthListener);
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "onStart\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Função acionada quando o app é fechado
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

    //Função responsável por preparar o Listener da autenticação
    private void preparaAutenticacao() {

        try{

            //Inicia o Firebase
            FirebaseApp.initializeApp(this);

            //Recebe a instância do Firebase
            mAuth = FirebaseAuth.getInstance();

            //Listener que ficará "ouvindo" alterações na autenficação
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    // Quando a autenticação mudar, recebe o usuário vindo do Firebase e verifica se é diferente de nulo
                    //  significando que está autenciado
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {

                        //Cria o usuário do Firebase
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        Usuario usuarioLogado = null;

                        //Confirme se o usuário do Firebase é diferente de nulo
                        if (firebaseUser != null) {

                            //Caso o usuário do Firebase for diferente de nulo, cria o objeto do usuário na aplicação
                            usuarioLogado = new Usuario(firebaseUser.getUid(), firebaseUser.getEmail());

                            //Indo para a próxima tela
                            Intent intent = new Intent(Login.this, MainActivity.class);

                            //Envia parâmetros extras para a próxima tela
                            intent.putExtra("usuarioLogado", usuarioLogado);
                            intent.putExtra("cadastro", cadastro);

                            startActivity(intent);

                        }
                    }
                }
            };
        } catch (Exception e){

            //Caso tenha dado erro, exibe o erro na tela
            Toast.makeText(getApplicationContext(), getClass().toString() + "\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(getClass().toString(), e.getMessage());
        }
    }

    //Função responsável por instanciar os elementos que o usuário vê
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

    //Função acionada quando for fazer o cadastro
    private void cadastrar(final String email, String nome, final String senha) {
        //Cria e popula um array com os campos que devem ser preenchidos
        ArrayList<EditText> campos = new ArrayList<>();
        campos.add(txtCadastroEmail);
        campos.add(txtCadastroNome);
        campos.add(txtCadastroSenha);

        //Verifica se todos os campos estão preenchidos
        if (camposPreenchidos(campos)){

            //Cria um listener que ficará ouvindo se a tentativa de criação do usuário foi bem sucedida
            mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        //Caso tenha dado erro na autenticação, notifica ao usuário
                        Toast.makeText(getApplicationContext(), getClass().toString() + "\n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(getClass().toString(), task.getException().getMessage());
                    }else{

                        //Caso tenha dado certo a criação, seta que é um cadastro e então o login será feito
                        //  automaticamente através do Listener da autenticação
                        cadastro = true;
                    }
                }
            });
        }else{
            //Caso não tenha preenchido todos os campos notifica ao usuário
            Toast.makeText(getApplicationContext(), "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
        }
    }

    //Função acionada quando for fazer o login
    private void login(String email, String senha) {
        //Cria e popula um array com os campos que devem ser preenchidos
        ArrayList<EditText> campos = new ArrayList<>();
        campos.add(txtLoginEmail);
        campos.add(txtLoginSenha);

        //Verifica se todos os campos estão preenchidos
        if (camposPreenchidos(campos)){

            //Cria um listener que ficará ouvindo se a tentativa de login do usuário foi bem sucedida
            mAuth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                //Caso tenha dado erro na autenticação, notifica ao usuário
                                Toast.makeText(getApplicationContext(), getClass().toString() + "\n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d(getClass().toString(), task.getException().getMessage());
                            }
                        }
                    });

        }else{
            //Caso não tenha preenchido todos os campos notifica ao usuário
            Toast.makeText(getApplicationContext(), "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
        }
    }

    //Função que verifica se todos os campos que deveriam ter sido preenchidos foram efetivamente preenchidos
    public boolean camposPreenchidos(ArrayList<EditText> campos){

        //Faz a varredura em todos os campos para ver se estão preenchidos
        for (int iCount = 0; iCount < campos.size(); iCount++){
            if (campos.get(iCount).getText().toString().equals("")){
                return false;
            }
        }

        return true;
    }

    //Função responsável por preparar as abas
    private void preparaAbas() {
        //Cria e configura o TabHost
        TabHost abas = (TabHost) findViewById(R.id.loginTabHost);
        abas.setup();
        TabHost.TabSpec descritor = abas.newTabSpec("aba1");

        //Cria a primeira aba, do login
        descritor.setContent(R.id.tabLogin);
        descritor.setIndicator("Login");
        abas.addTab(descritor);

        //Cria a segunda aba, do cadastro
        descritor = abas.newTabSpec("aba2");
        descritor.setContent(R.id.tabCadastro);
        descritor.setIndicator("Cadastro");

        //Adiciona as abas ao TabHost
        abas.addTab(descritor);
    }

}
