package chatapp.com.claudineibjr.androidchatapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public class ContatosAdapter extends BaseAdapter {

        private final ArrayList<DadosUsuario> contatos;
        private final Activity activity;

        public ContatosAdapter(ArrayList<DadosUsuario> contatos, Activity activity) {
            this.contatos = contatos;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return contatos.size();
        }

        @Override
        public Object getItem(int position) {
            return contatos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = activity.getLayoutInflater().inflate(R.layout.lista_contatos_personalizada, parent, false);

            DadosUsuario contato = contatos.get(position);

            //Instanciando as Views
            ImageView imgContato = (ImageView) view.findViewById(R.id.imgContato);
            TextView txtNomeContato = (TextView) view.findViewById(R.id.txtNomeContato);
            TextView txtStatusContato = (TextView) view.findViewById(R.id.txtStatusContato);

            //Definindo os valores para as Views
            /*byte[] imageBytes = Base64.decode(contato.getImagem(), Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imgContato.setImageBitmap(decodedImage);*/

            txtNomeContato.setText(contato.getEmail());
            txtStatusContato.setText(contato.isConectado() ? "Online" : "Ausente desde " + new SimpleDateFormat("dd/MM/yyyy  H:m").format(contato.getUltimaVez()));
            return view;
        }
    }

    private Usuario usuario;

    private ArrayList<Usuario> contatos;

    private ListView listaContatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preparaAbas();
        instanciaElementosVisuais();

        //Verifica se existem dados vindouros da tela anterior
        Bundle extra = getIntent().getExtras();
        if (extra != null) {

            // Recebe o usuário da tela anterior
            usuario = (Usuario) extra.getSerializable("usuarioLogado");
            usuario.getDadosUsuario().setConectado(true);
            usuario.getDadosUsuario().setUltimaVez(Calendar.getInstance().getTime());

            if (extra.getBoolean("cadastro")){
                try{
                    Log.d(getClass().toString(), usuario.toString());
                    Parametros.getUsuarioReferencia().child(usuario.getUid()).setValue(usuario);
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Não deu\n\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }else{

            }
        }

        carregaContatos();
    }

    private void instanciaElementosVisuais() {
        listaContatos = (ListView) findViewById(R.id.lista_contatos);
    }

    private void carregaContatos() {

        //Parametros.getUsuarioReferencia().orderByChild("email").addValueEventListener(new ValueEventListener() {
        Parametros.getUsuarioReferencia().child(usuario.getUid()).child("contatos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Usuario> contatos_aux = new ArrayList<>();
                contatos = new ArrayList<>();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Parametros.getUsuarioReferencia().child(data.getValue().toString()).child("dadosUsuario").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Log.d(getClass().toString(), data.getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                /*for (int i = contatos_aux.size() - 1; i >= 0; i--){
                    contatos.add(contatos_aux.get(i));
                }*/

                /*ContatosAdapter usuariosArrayAdapter = new ContatosAdapter(contatos, MainActivity.this);*/

                /*listaContatos.setAdapter(usuariosArrayAdapter);*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

        usuario.getDadosUsuario().setConectado(false);
        usuario.getDadosUsuario().setUltimaVez(Calendar.getInstance().getTime());
        Parametros.getUsuarioReferencia().child(usuario.getUid()).setValue(usuario);

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

    @Override
    protected void onStop() {

        usuario.getDadosUsuario().setConectado(false);
        usuario.getDadosUsuario().setUltimaVez(Calendar.getInstance().getTime());
        Parametros.getUsuarioReferencia().child(usuario.getUid()).setValue(usuario);

        super.onStop();
    }
}
