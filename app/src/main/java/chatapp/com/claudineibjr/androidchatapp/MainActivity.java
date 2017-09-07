package chatapp.com.claudineibjr.androidchatapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

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

            final DadosUsuario contato = contatos.get(position);

            //Instanciando as Views
            ImageView imgContato = (ImageView) view.findViewById(R.id.imgContato);
            final TextView txtNomeContato = (TextView) view.findViewById(R.id.txtNomeContato);
            TextView txtStatusContato = (TextView) view.findViewById(R.id.txtStatusContato);

            //Definindo os valores para as Views
            /*byte[] imageBytes = Base64.decode(contato.getImagem(), Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imgContato.setImageBitmap(decodedImage);*/

            txtNomeContato.setText(contato.getEmail());

            txtStatusContato.setText(
                    contato.isConectado() ?
                            "Online" :
                            "Ausente " +
                                    (Calendar.getInstance().getTime().compareTo(contato.getUltimaVez()) == 1 ?
                                            "desde ontem às " + new SimpleDateFormat("H:m").format(contato.getUltimaVez()) :
                                            (Calendar.getInstance().getTime().compareTo(contato.getUltimaVez()) == 0 ?
                                                    "desde hoje às " + new SimpleDateFormat("H:m").format(contato.getUltimaVez()) :
                                                    "há " + Calendar.getInstance().getTime().compareTo(contato.getUltimaVez()) + " dias"
                                            )
                                    )
            );

            txtStatusContato.setTextColor(
                    contato.isConectado() ?
                            Color.parseColor("#00C853") :
                            Color.parseColor("#000000"));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Indo para a próxima tela
                    Intent intent = new Intent(MainActivity.this, Conversa.class);
                    intent.putExtra("teste", txtNomeContato.getText().toString());

                    startActivity(intent);
                }
            });

            return view;
        }
    }

    private Usuario usuario;

    private ArrayList<DadosUsuario> contatos = new ArrayList<>();

    private ListView listaContatos;

    private ContatosAdapter usuariosArrayAdapter;

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

            if (extra.getBoolean("cadastro")){

                usuario.getDadosUsuario().setConectado(true);
                usuario.getDadosUsuario().setUltimaVez(Calendar.getInstance().getTime());

                try{
                    Parametros.getUsuarioReferencia().child(usuario.getUid()).setValue(usuario);
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Não deu\n\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }else{
                Parametros.getUsuarioReferencia().orderByKey().equalTo(usuario.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            usuario = data.getValue(Usuario.class);
                            usuario.getDadosUsuario().setConectado(true);
                            usuario.getDadosUsuario().setUltimaVez(Calendar.getInstance().getTime());

                            try{
                                Parametros.getUsuarioReferencia().child(usuario.getUid()).setValue(usuario);
                            } catch (Exception e){
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            carregaContatos();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }


    }

    private void instanciaElementosVisuais() {
        listaContatos = (ListView) findViewById(R.id.lista_contatos);
    }

    private void carregaContatos() {

        usuariosArrayAdapter = new ContatosAdapter(contatos, MainActivity.this);
        listaContatos.setAdapter(usuariosArrayAdapter);

        for (Iterator<String> lstContato = usuario.getContatos().iterator(); lstContato.hasNext();){
            String strContato = lstContato.next();

            Parametros.getUsuarioReferencia().child(strContato).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (data.getKey().equals("dadosUsuario")){
                            contatos.add(data.getValue(DadosUsuario.class));
                            break;
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        usuariosArrayAdapter.notifyDataSetChanged();

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
            case R.id.menu_add_contato:{
                novoContato();
                return true;
            }

            case R.id.menu_desconectar: {
                desconecta();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void novoContato() {
        final EditText txtNovoContato = new EditText(this);
        txtNovoContato.setHint("Exemplo: claudineibjr@hotmail.com");

        new AlertDialog.Builder(this)
                .setTitle("Novo contato")
                .setMessage("Digite o e-mail do novo contato")
                .setView(txtNovoContato)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adicionaContato(txtNovoContato.getText().toString());
                    }
                }).show();
    }

    private void adicionaContato(String strNovoContato) {
        Query queryEmail = Parametros.getUsuarioReferencia()
                .orderByChild("dadosUsuario/email")
                .equalTo(strNovoContato)
                .limitToFirst(1);

        queryEmail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    usuario.addContato(data.getKey());

                    for (DataSnapshot dataChild : data.getChildren()){
                        if (dataChild.getKey().equals("dadosUsuario")){
                            Log.d(getClass().toString(), dataChild.getKey() + "\n" + dataChild.getValue().toString());
                            contatos.add(dataChild.getValue(DadosUsuario.class));
                            usuariosArrayAdapter.notifyDataSetChanged();
                            break;
                        }
                    }

                    try {
                        Parametros.getUsuarioReferencia().child(usuario.getUid()).setValue(usuario);
                    } catch (Exception e){
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {

        usuario.getDadosUsuario().setConectado(false);
        usuario.getDadosUsuario().setUltimaVez(Calendar.getInstance().getTime());
        Parametros.getUsuarioReferencia().child(usuario.getUid()).setValue(usuario);

        super.onStop();
    }
}
