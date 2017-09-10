package chatapp.com.claudineibjr.androidchatapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Claudinei on 22/08/2017.
 */

public class Usuario implements Serializable {

    public static class ConversaRecente implements Serializable{
        private HashMap<String, Destinatario> destinatarios;

        public ConversaRecente() {}

        public ConversaRecente(HashMap<String, Destinatario> destinatarios) {
            this.destinatarios = destinatarios;
        }

        public HashMap<String, Destinatario> getDestinatarios() {
            return destinatarios;
        }

        public void setDestinatarios(HashMap<String, Destinatario> destinatarios) {
            this.destinatarios = destinatarios;
        }
    }
    public static class Destinatario implements Serializable{
        private String mensagem;

        public Destinatario(String mensagem) {
            this.mensagem = mensagem;
        }

        public Destinatario() {
        }

        public String getMensagem() {
            return mensagem;
        }

        public void setMensagem(String mensagem) {
            this.mensagem = mensagem;
        }
    }

    private String uid;
    private ArrayList<String> contatos = new ArrayList<>(); // uid dos contatos
    private ConversaRecente conversasRecentes;
    private DadosUsuario dadosUsuario = new DadosUsuario();

    public Usuario(){}

    public Usuario(String uid, String email) {
        this.uid = uid;
        this.dadosUsuario.setEmail(email);
    }

    public Usuario(String uid, String email, String imagem, ArrayList<String> contatos, ConversaRecente conversasRecentes) {
        this.uid = uid;
        this.contatos = contatos;
        this.conversasRecentes = conversasRecentes;
        this.dadosUsuario = new DadosUsuario(email, imagem);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "uid='" + uid + '\'' +
                ", contatos=" + contatos +
                ", conversasRecentes=" + conversasRecentes +
                ", dadosUsuario=" + dadosUsuario +
                '}';
    }

    public String getUid() {
        return uid;
    }

    public ArrayList<String> getContatos(){
        return contatos;
    }

    public void addContato(String contato){
        contatos.add(contato);
    }

    public ConversaRecente getConversasRecentes(){
        return conversasRecentes;
    }

    public DadosUsuario getDadosUsuario() {
        return dadosUsuario;
    }
}
