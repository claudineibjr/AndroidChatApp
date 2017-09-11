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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //Classe que fará a adaptação dos contatos para um ListView personalizado
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
            final TextView txtNomeContato = (TextView) view.findViewById(R.id.txtNomeContato);
            TextView txtStatusContato = (TextView) view.findViewById(R.id.txtStatusContato);

            //Definindo os valores para as Views
            txtNomeContato.setText(contato.getEmail());

            txtStatusContato.setText(
                    contato.isConectado() ?
                            "Online" :
                            "Ausente " +
                                    (Calendar.getInstance(new Locale("pt", "BR")).getTime().compareTo(contato.getUltimaVez()) == 1 ?
                                            "desde ontem às " + new SimpleDateFormat("HH:mm").format(contato.getUltimaVez()) :
                                            (Calendar.getInstance(new Locale("pt", "br")).getTime().compareTo(contato.getUltimaVez()) == 0 ?
                                                    "desde hoje às " + new SimpleDateFormat("HH:mm").format(contato.getUltimaVez()) :
                                                    "há " + Calendar.getInstance(new Locale("pt", "BR")).getTime().compareTo(contato.getUltimaVez()) + " dias"
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
                Intent intent = new Intent(MainActivity.this, ConversaActivity.class);
                intent.putExtra("usuarioDestinatario", txtNomeContato.getText().toString());
                intent.putExtra("usuarioLogado", usuario);

                startActivity(intent);
                }
            });

            return view;
        }
    }

    //Classe que fará a adaptação das conversas recentes para um ListView personalizado
    public class ConversasAdapter extends BaseAdapter {

        private final ArrayList<ConversaRecente> conversasRecentes;
        private final Activity activity;

        public ConversasAdapter(ArrayList<ConversaRecente> conversasRecentes, Activity activity) {
            this.conversasRecentes = conversasRecentes;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return conversasRecentes.size();
        }

        @Override
        public Object getItem(int position) {
            return conversasRecentes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = activity.getLayoutInflater().inflate(R.layout.lista_conversas_personalizada, parent, false);

            final ConversaRecente conversaRecente = conversasRecentes.get(position);

            //Instanciando as Views
            final TextView txtNomeContato = (TextView) view.findViewById(R.id.conversa_txtNomeContato);
            TextView txtStatusContato = (TextView) view.findViewById(R.id.conversa_UltimaMensagem);

            //Definindo os valores para as Views
            txtNomeContato.setText(conversaRecente.getEmailContato());
            txtStatusContato.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(conversaRecente.getMensagem().getData()));
            txtStatusContato.setText(txtStatusContato.getText().toString() + (conversaRecente.getMensagem().getRemetente().equals(usuario.getUid()) ? " ► " : " ◄ "));
            txtStatusContato.setText(txtStatusContato.getText().toString() + conversaRecente.getMensagem().getTexto());

            view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                //Indo para a próxima tela
                Intent intent = new Intent(MainActivity.this, ConversaActivity.class);
                intent.putExtra("usuarioDestinatario", txtNomeContato.getText().toString());
                intent.putExtra("usuarioLogado", usuario);

                startActivity(intent);
                }
            });

            return view;
        }
    }

    private Usuario usuario;

    //Array que conterá os contatos exibidos na lista
    private ArrayList<DadosUsuario> contatos = new ArrayList<>();

    //Array que conterá as conversas recentes exibidos na lista
    private ArrayList<ConversaRecente> conversas = new ArrayList<>();

    //Lista de contatos
    private ListView listaContatos;

    //Lista de conversas recentes
    private ListView listaConversas;

    //ArrayAdapter para exibir os contatos no ListView Personalizado
    private ContatosAdapter usuariosArrayAdapter;

    //ArrayAdapter para exibir as conversas recentes no ListView Personalizado
    private ConversasAdapter conversasArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Prepara as abas contidas no TabHost
        preparaAbas();

        //Instancia os elementos visuais, permitindo que recebam e tenham valor atribuído
        instanciaElementosVisuais();

        //Verifica se existem dados vindouros da tela anterior
        Bundle extra = getIntent().getExtras();
        if (extra != null) {

            // Recebe o usuário da tela anterior
            usuario = (Usuario) extra.getSerializable("usuarioLogado");

            //Caso for um cadastro
            if (extra.getBoolean("cadastro")){

                //Seta que o usuário está online
                usuario.getDadosUsuario().setConectado(true);

                //Seta que ele foi "visto" pela última vez agora
                usuario.getDadosUsuario().setUltimaVez(Calendar.getInstance(new Locale("pt", "br")).getTime());

                try{

                    //Cria o usuário dentro do Bando de dados
                    Parametros.getUsuarioReferencia().child(usuario.getUid()).setValue(usuario);
                } catch (Exception e){

                    //Exibe o erro caso tenha dado errado a criação do usuário no banco de dados
                    Toast.makeText(getApplicationContext(), getClass().toString() + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d(getClass().toString(), e.getMessage());
                }

            }else{
                //Caso o usuário não esteja sendo criado, busca seus dados no banco de dados
                Parametros.getUsuarioReferencia().orderByKey().equalTo(usuario.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {

                            //Instancia o usuário vindouro do banco de dados
                            usuario = data.getValue(Usuario.class);

                            //Seta que o usuário está online
                            usuario.getDadosUsuario().setConectado(true);

                            //Seta que ele foi "visto" pela última vez agora
                            usuario.getDadosUsuario().setUltimaVez(Calendar.getInstance(new Locale("pt", "br")).getTime());

                            //Carrega as conversas do usuário
                            carregaConversas();

                            //Carrega os contatos do usuário
                            carregaContatos();

                            try{

                                //Insere as alterações realizadas no usuário num HashMap
                                Map<String, Object> atualizacoes = new HashMap<>();
                                atualizacoes.put("/dadosUsuario/conectado", usuario.getDadosUsuario().isConectado());
                                atualizacoes.put("/dadosUsuario/ultimaVez", usuario.getDadosUsuario().getUltimaVez());

                                //Tenta atualizar os dados do usuário no banco de dados
                                Parametros.getUsuarioReferencia().child(usuario.getUid()).updateChildren(atualizacoes);

                            } catch (Exception e){

                                //Exibe o erro caso tenha dado errado a criação do usuário no banco de dados
                                Toast.makeText(getApplicationContext(), getClass().toString() + "\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d(getClass().toString(), e.getMessage());
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }


    }

    //Função responsável por instanciar os elementos que o usuário vê
    private void instanciaElementosVisuais() {
        listaContatos = (ListView) findViewById(R.id.lista_contatos);
        listaConversas = (ListView) findViewById(R.id.lista_conversas);
    }

    //Função responsável por carregar os contatos
    private void carregaContatos() {

        //Instancia o ArrayAdapter de usuários para a lista de contatos
        usuariosArrayAdapter = new ContatosAdapter(contatos, MainActivity.this);

        //Seta o ArrayAdapter para a lista de contatos
        listaContatos.setAdapter(usuariosArrayAdapter);

        //Para cada contato encontrado (uid), vai buscar no banco de dados as informaões deste contato
        for (Iterator<String> lstContato = usuario.getContatos().iterator(); lstContato.hasNext();){
            String strContato = lstContato.next();

            //Vai buscar no banco de dados a informação dos dados do usuário
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

        //Notifica ao ArrayAdapter que o DataSet foi alterado
        usuariosArrayAdapter.notifyDataSetChanged();

    }

    //Função responsável por carregar as conversas recentes
    private void carregaConversas() {

        //Instancia o ArrayAdapter de conversas para a lista de conversas recentes
        conversasArrayAdapter = new ConversasAdapter(conversas, MainActivity.this);

        //Seta o ArrayAdapter para a lista de contatos
        listaConversas.setAdapter(conversasArrayAdapter);

        //Cria variáveis que receberão o uid do Contato e a key da conversa
        String uidContato, uidConversa;

        //Para cada objeto do HashMap das converas recentes, faz uma iteração para obter um contato
        for(Object objname: usuario.getConversasRecentes().keySet()) {

            //Seta o uid do contato
            uidContato = objname.toString();

            //Vai buscar no banco de dados o e-mail do contato
            Parametros.getUsuarioReferencia()
                    .child(uidContato)
                    .child(Parametros.getDadosUsuario())
                    .child("email")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //Para cada objeto do HashMap das converas recentes, faz uma iteração para obter uma conversa
            for(Object objname2: usuario.getConversasRecentes().get(objname).keySet()) {
                Log.d(getClass().toString() + "Conversas", "Valor: " + usuario.getConversasRecentes().get(objname).get(objname2));

                //Recebe o uid da conversa do HashMap
                uidConversa = usuario.getConversasRecentes().get(objname).get(objname2);

                //Cria a Key da conversa que será o uid dos dois contatos ordenado alfabéticamente
                String keyConversa;
                if (usuario.getUid().compareToIgnoreCase(uidContato) > 0)
                    keyConversa = uidContato + "-" + usuario.getUid();
                else
                    keyConversa = usuario.getUid() + "-" + uidContato;

                //Vai buscar no banco de dados os dados da última conversa
                Parametros.getMensagensReferencia()
                        .child(keyConversa)
                        .child(uidConversa)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                //Mensagem = usuario.getConversasRecentes().get(objname).get(objname2).toString();
            }
        }

        //Notifica ao ArrayAdapter que o DataSet foi alterado
        conversasArrayAdapter.notifyDataSetChanged();

    }

    //Função responsável por preparar as abas
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

    //Função acionada quando as opções do menu forem criadas
    @Override
    public boolean onCreateOptionsMenu(Menu _menu) {

        //Função responsável por setar o menu que conterá o botão de desconectar
        super.onCreateOptionsMenu(_menu);

        getMenuInflater().inflate(R.menu.menuprincipal, _menu);
        return (true);
    }

    // Função responsável por desconectar o usuário
    public void desconecta(){

        //Seta que o usuário está off-line
        usuario.getDadosUsuario().setConectado(false);

        //Seta que ele foi "visto" pela última vez agora
        usuario.getDadosUsuario().setUltimaVez(Calendar.getInstance(new Locale("pt", "br")).getTime());

        //Insere as alterações realizadas no usuário num HashMap
        Map<String, Object> atualizacoes = new HashMap<>();
        atualizacoes.put("/dadosUsuario/conectado", usuario.getDadosUsuario().isConectado());
        atualizacoes.put("/dadosUsuario/ultimaVez", usuario.getDadosUsuario().getUltimaVez());

        try{
            //Tenta atualizar os dados do usuário no banco de dados
            Parametros.getUsuarioReferencia().child(usuario.getUid()).updateChildren(atualizacoes);
        }catch (Exception e){

            //Exibe o erro caso tenha dado errado a atualização do usuário no banco de dados
            Toast.makeText(getApplicationContext(), getClass().toString() + "\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(getClass().toString(), e.getMessage());
        }

        //Notifica que o usuário foi desautenticado
        FirebaseAuth.getInstance().signOut();

        //Finaliza a activity
        finish();
    }

    //Função responsável por identificar o clique do usuário
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Deteca o botão clicado
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

    //Função responsável por exibir uma caixa de diálogo na tela para a criação do contato
    private void novoContato() {

        //Cria um campo que será exibido para o usuário
        final EditText txtNovoContato = new EditText(this);
        txtNovoContato.setHint("Exemplo: claudineibjr@hotmail.com");

        //Cria um alerta que irá exibir o campo para informação do e-mail do usuário
        new AlertDialog.Builder(this)
                .setTitle("Novo contato")
                .setMessage("Digite o e-mail do novo contato")
                .setView(txtNovoContato)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Ao clicar no botão ok, tenta criar este novo contato
                        adicionaContato(txtNovoContato.getText().toString());
                    }
                }).show();
    }

    //Função responsável por receber um e-mail e adicionar este e-mail na lista de contatos do usuário
    private void adicionaContato(String strNovoContato) {
        //Cria uma query para buscar os dados no banco de dados
        Query queryEmail = Parametros.getUsuarioReferencia()
                .orderByChild("dadosUsuario/email")
                .equalTo(strNovoContato)
                .limitToFirst(1);

        //Cria um LIstener unitário que irá receber os dados do usuário
        queryEmail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    //Quando receber os dados do usuário, pega a Key e adiciona-a a lista do usuário
                    usuario.addContato(data.getKey());

                    for (DataSnapshot dataChild : data.getChildren()){
                        if (dataChild.getKey().equals("dadosUsuario")){
                            //Adicionar os dados do usuário a lista de contato
                            contatos.add(dataChild.getValue(DadosUsuario.class));

                            //Notifica o ArrayAdapter que o DataSet foi alterado
                            usuariosArrayAdapter.notifyDataSetChanged();
                            break;
                        }
                    }

                    try {

                        //Tenta atualizar os dados do usuário no banco de dados
                        Parametros.getUsuarioReferencia().child(usuario.getUid()).child("contatos").setValue(usuario.getContatos());
                    } catch (Exception e){

                        //Exibe o erro caso tenha dado errado a atualização do usuário no banco de dados
                        Toast.makeText(getApplicationContext(), getClass().toString() + "\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(getClass().toString(), e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Função acionada quando o app é fechado
    @Override
    protected void onStop() {

        //Seta que o usuário está off-line
        usuario.getDadosUsuario().setConectado(false);

        //Seta que ele foi "visto" pela última vez agora
        usuario.getDadosUsuario().setUltimaVez(Calendar.getInstance(new Locale("pt", "br")).getTime());

        //Insere as alterações realizadas no usuário num HashMap
        Map<String, Object> atualizacoes = new HashMap<>();
        atualizacoes.put("/dadosUsuario/conectado", usuario.getDadosUsuario().isConectado());
        atualizacoes.put("/dadosUsuario/ultimaVez", usuario.getDadosUsuario().getUltimaVez());

        try {

            //Tenta atualizar os dados do usuário no banco de dados
            Parametros.getUsuarioReferencia().child(usuario.getUid()).updateChildren(atualizacoes);
        } catch (Exception e){
            //Exibe o erro caso tenha dado errado a atualização do usuário no banco de dados
            Toast.makeText(getApplicationContext(), getClass().toString() + "\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(getClass().toString(), e.getMessage());
        }

        super.onStop();
    }
}