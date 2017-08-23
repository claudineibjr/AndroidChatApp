package chatapp.com.claudineibjr.androidchatapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preparaAbas();

        //Verifica se existem dados vindouros da tela anterior
        Bundle extra = getIntent().getExtras();
        if (extra != null) {

            // Recebe o usuário da tela anterior
            usuario = (Usuario) extra.getSerializable("usuarioLogado");
            //usuario.setConectado(true);

            if (extra.getBoolean("cadastro")){
                //usuario.setImagem(Parametros.getBase64Image());

                try{
                    Parametros.getUsuarioReferencia().child(usuario.getUid()).setValue(usuario);
                    Toast.makeText(getApplicationContext(), "Uhul", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Não deu\n\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void preparaAbas() {
        TabHost abas = (TabHost) findViewById(R.id.mainTabHost);
        abas.setup();

        TabHost.TabSpec descritor = abas.newTabSpec("aba1");

        descritor.setContent(R.id.tabConversas);
        descritor.setIndicator("Conversas");
        abas.addTab(descritor);

        descritor = abas.newTabSpec("aba2");
        descritor.setContent(R.id.tabContatos);
        descritor.setIndicator("Contatos");

        abas.addTab(descritor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu _menu) {
        super.onCreateOptionsMenu(_menu);

        getMenuInflater().inflate(R.menu.menuprincipal, _menu);
        return (true);
    }

    public void desconecta(){
        // Desconecta o usuário
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_desconectar: {
                desconecta();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
