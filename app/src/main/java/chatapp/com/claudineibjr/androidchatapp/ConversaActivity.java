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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ConversaActivity extends AppCompatActivity {

    private LinearLayout linearLayoutConversa;
    private Button btnEnviarMensagem;
    private EditText txtMensagem;

    private Usuario usuario;
    private DadosUsuario usuarioDestinatario;
    private String usuarioDestinatario_key;

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
                        usuarioDestinatario_key = data.getKey();

                        for (DataSnapshot dataChild : data.getChildren()){
                            if (dataChild.getKey().equals("dadosUsuario")){
                                usuarioDestinatario = dataChild.getValue(DadosUsuario.class);
                                Toast.makeText(getApplicationContext(), "Carregou", Toast.LENGTH_SHORT).show();
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

    private void instanciaElementosVisuais() {
        linearLayoutConversa = (LinearLayout) findViewById(R.id.linearLayoutConversa);
        txtMensagem = (EditText) findViewById(R.id.txtMensagem);

        btnEnviarMensagem = (Button) findViewById(R.id.btnEnviar);
        btnEnviarMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    adicionaMensagemTela(txtMensagem.getText().toString(), false);

                    Map<String, Object> atualizacoes = new HashMap<>();
                    atualizacoes.put("texto", txtMensagem.getText().toString());
                    atualizacoes.put("data", Calendar.getInstance(new Locale("pt", "br")).getTime());

                    Parametros.getMensagensReferencia().child(usuario.getUid()).child(usuarioDestinatario_key).push().updateChildren(atualizacoes);

                    txtMensagem.setText("");

                }catch (Exception e){
                    Log.d(getClass().toString(), e.getMessage());
                }
            }
        });
    }

    private void adicionaMensagemTela(String mensagem, boolean recebida){

        final TextView textViewMensagem = new TextView(this);

        textViewMensagem.setGravity(( recebida ? Gravity.START : Gravity.END));
        textViewMensagem.setBackgroundColor(( recebida ? Color.parseColor("#C5CAE9") : Color.parseColor("#BBDEFB")) );
        textViewMensagem.setTextColor(Color.parseColor("#000000"));
        textViewMensagem.setText(mensagem);
        textViewMensagem.setTextSize(Dimension.SP, 14);
        textViewMensagem.setPadding(10, 10, 10, 10);

        ActionBar.LayoutParams layoutParamsMensagem = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, (recebida ? Gravity.END : Gravity.START));
        layoutParamsMensagem.setMargins(( recebida ? 0 : 200), 0, ( recebida ? 200 : 0), 0);

        textViewMensagem.setLayoutParams(layoutParamsMensagem);

        final TextView textViewHorario = new TextView(this);

        textViewHorario.setGravity(( recebida ? Gravity.START : Gravity.END));
        textViewHorario.setBackgroundColor(( recebida ? Color.parseColor("#C5CAE9") : Color.parseColor("#BBDEFB")) );
        textViewHorario.setTextColor(Color.parseColor("#000000"));
        textViewHorario.setText("07/09/2017 18:22");
        textViewHorario.setTextSize(Dimension.SP, 10);
        textViewHorario.setPadding(10, 10, 10, 10);

        ActionBar.LayoutParams layoutParamsDataHora = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, (recebida? Gravity.END : Gravity.START));
        layoutParamsDataHora.setMargins(( recebida ? 0 : 200), 0, ( recebida ? 200 : 0), 15);

        textViewHorario.setLayoutParams(layoutParamsDataHora);

        linearLayoutConversa.addView(textViewMensagem);
        linearLayoutConversa.addView(textViewHorario);
    }
}
