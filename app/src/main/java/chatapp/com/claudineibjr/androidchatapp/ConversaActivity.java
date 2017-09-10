package chatapp.com.claudineibjr.androidchatapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Dimension;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ConversaActivity extends AppCompatActivity {

    private ScrollView scrollConversa;

    private LinearLayout linearLayoutConversa;

    private Button btnEnviarMensagem;

    private AutoCompleteTextView txtMensagem;
    private TextView txtNomeContato;
    private TextView txtStatusContato;

    private Usuario usuario;
    private DadosUsuario usuarioDestinatario;
    private String usuarioDestinatario_uid;

    private Button btnDBGEnviar, btnDBGReceber;

    private boolean dadosCarregados = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        instanciaElementosVisuais();

        //Verifica se existem dados vindouros da tela anterior
        Bundle extra = getIntent().getExtras();
        if (extra != null) {

            // Recebe o usuário da tela anterior
            usuario = (Usuario) extra.getSerializable("usuarioLogado");

            // Recebe o usuário que irá receber as mensagens
            String strUsuarioDestinatario = extra.getString("usuarioDestinatario");

            Query queryDestinatario = Parametros.getUsuarioReferencia()
                    .orderByChild("dadosUsuario/email")
                    .equalTo(strUsuarioDestinatario)
                    .limitToFirst(1);

            queryDestinatario.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        usuarioDestinatario_uid = data.getKey();
                        dadosCarregados = true;
                        carregaMensagens();

                        for (DataSnapshot dataChild : data.getChildren()){
                            if (dataChild.getKey().equals("dadosUsuario")){
                                usuarioDestinatario = dataChild.getValue(DadosUsuario.class);
                                txtNomeContato.setText(usuarioDestinatario.getEmail());
                                txtStatusContato.setText(usuarioDestinatario.isConectado() ?
                                        "Online" :
                                        "Ausente " +
                                                (Calendar.getInstance(new Locale("pt", "BR")).getTime().compareTo(usuarioDestinatario.getUltimaVez()) == 1 ?
                                                        "desde ontem às " + new SimpleDateFormat("HH:mm").format(usuarioDestinatario.getUltimaVez()) :
                                                        (Calendar.getInstance(new Locale("pt", "br")).getTime().compareTo(usuarioDestinatario.getUltimaVez()) == 0 ?
                                                                "desde hoje às " + new SimpleDateFormat("HH:mM").format(usuarioDestinatario.getUltimaVez()) :
                                                                "há " + Calendar.getInstance(new Locale("pt", "BR")).getTime().compareTo(usuarioDestinatario.getUltimaVez()) + " dias"
                                                        )
                                                ));
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    private void carregaMensagens() {
        String keyConversa;

        if (usuario.getUid().compareToIgnoreCase(usuarioDestinatario_uid) > 0)
            keyConversa = usuarioDestinatario_uid + "-" + usuario.getUid();
        else
            keyConversa = usuario.getUid() + "-" + usuarioDestinatario_uid;

        final ArrayList<Mensagem> mensagens = new ArrayList<>();

        Parametros.getMensagensReferencia().child(keyConversa).limitToLast(10).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(getClass().toString(), dataSnapshot.getValue().toString());
                mensagens.add(dataSnapshot.getValue(Mensagem.class));
                adicionaMensagemTela(dataSnapshot.getValue(Mensagem.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void instanciaElementosVisuais() {
        linearLayoutConversa = (LinearLayout) findViewById(R.id.linearLayoutConversa);

        txtMensagem = (AutoCompleteTextView) findViewById(R.id.txtMensagem);
        txtNomeContato = (TextView) findViewById(R.id.conversa_txtNomeContato);
        txtStatusContato = (TextView) findViewById(R.id.conversa_txtStatusContato);

        btnEnviarMensagem = (Button) findViewById(R.id.btnEnviar);
        btnEnviarMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dadosCarregados) {

                    try {
                        String keyConversa = "";

                        if (usuario.getUid().compareToIgnoreCase(usuarioDestinatario_uid) > 0)
                            keyConversa = usuarioDestinatario_uid + "-" + usuario.getUid();
                        else
                            keyConversa = usuario.getUid() + "-" + usuarioDestinatario_uid;

                        Mensagem mensagem = new Mensagem(usuario.getUid(),
                                usuarioDestinatario_uid,
                                txtMensagem.getText().toString(),
                                Calendar.getInstance(Locale.getDefault()).getTime());

                        adicionaMensagemTela(mensagem);

                        Map<String, Object> atualizacoes = new HashMap<>();
                        atualizacoes.put("remetente", mensagem.getRemetente());
                        atualizacoes.put("destinatario", mensagem.getDestinatario());
                        atualizacoes.put("texto", mensagem.getTexto());
                        atualizacoes.put("data", mensagem.getData());

                        Parametros.getMensagensReferencia()
                                .child(keyConversa)
                                .push()
                                .updateChildren(atualizacoes);

                        txtMensagem.setText("");

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Erro ao enviar a mensagem para o banco de dados\n\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Carregando as informações, tente novamente em alguns segundos...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDBGEnviar = (Button) findViewById(R.id.btnDBGEnviar);
        btnDBGEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbgMensagem(true);
            }
        });

        btnDBGReceber = (Button) findViewById(R.id.btnDBGReceber);
        btnDBGReceber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbgMensagem(false);
            }
        });

        scrollConversa = (ScrollView) findViewById(R.id.scrollConversa);
    }

    private void adicionaMensagemTela(Mensagem mensagem){

        boolean recebida = (mensagem.getDestinatario().equals(usuario.getUid()));

        final TextView textViewMensagem = new TextView(this);

        textViewMensagem.setGravity(( recebida ? Gravity.START : Gravity.END));
        textViewMensagem.setBackgroundColor(( recebida ? Color.parseColor("#C5CAE9") : Color.parseColor("#BBDEFB")) );
        textViewMensagem.setTextColor(Color.parseColor("#000000"));
        textViewMensagem.setText(mensagem.getTexto());
        textViewMensagem.setTextSize(Dimension.SP, 14);
        textViewMensagem.setPadding(10, 10, 10, 10);

        ActionBar.LayoutParams layoutParamsMensagem = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, (recebida ? Gravity.END : Gravity.START));
        layoutParamsMensagem.setMargins(( recebida ? 0 : 200), 0, ( recebida ? 200 : 0), 0);

        textViewMensagem.setLayoutParams(layoutParamsMensagem);

        final TextView textViewHorario = new TextView(this);

        textViewHorario.setGravity(( recebida ? Gravity.START : Gravity.END));
        textViewHorario.setBackgroundColor(( recebida ? Color.parseColor("#C5CAE9") : Color.parseColor("#BBDEFB")) );
        textViewHorario.setTextColor(Color.parseColor("#000000"));
        textViewHorario.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(mensagem.getData()));
        textViewHorario.setTextSize(Dimension.SP, 10);
        textViewHorario.setPadding(10, 10, 10, 10);

        ActionBar.LayoutParams layoutParamsDataHora = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, (recebida? Gravity.END : Gravity.START));
        layoutParamsDataHora.setMargins(( recebida ? 0 : 200), 0, ( recebida ? 200 : 0), 15);

        textViewHorario.setLayoutParams(layoutParamsDataHora);

        linearLayoutConversa.addView(textViewMensagem);
        linearLayoutConversa.addView(textViewHorario);

        scrollConversa.fullScroll(View.FOCUS_DOWN);
    }

    private void dbgMensagem(boolean envia){
        String keyConversa = "";

        if (usuario.getUid().compareToIgnoreCase(usuarioDestinatario_uid) > 0)
            keyConversa = usuarioDestinatario_uid + "-" + usuario.getUid();
        else
            keyConversa = usuario.getUid() + "-" + usuarioDestinatario_uid;

        Mensagem mensagem = new Mensagem((envia ? usuario.getUid() : usuarioDestinatario_uid) ,
                (envia ? usuarioDestinatario_uid : usuario.getUid()),
                txtMensagem.getText().toString(),
                Calendar.getInstance(TimeZone.getTimeZone("UTC-3")).getTime());

        Map<String, Object> atualizacoes = new HashMap<>();
        atualizacoes.put("remetente", mensagem.getRemetente());
        atualizacoes.put("destinatario", mensagem.getDestinatario());
        atualizacoes.put("texto", mensagem.getTexto());
        atualizacoes.put("data", mensagem.getData());

        Parametros.getMensagensReferencia()
                .child(keyConversa)
                .push()
                .updateChildren(atualizacoes);
    }
}
