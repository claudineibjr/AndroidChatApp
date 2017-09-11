package chatapp.com.claudineibjr.androidchatapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Claudinei on 22/08/2017.
 */

public class Usuario implements Serializable {

    public static class ConversaRecente implements Serializable{
        private String contato;
        private String mensagem;

        public ConversaRecente() {
        }

        

        public String getContato() {
            return contato;
        }

        public void setContato(String contato) {
            this.contato = contato;
        }

        public String getMensagem() {
            return mensagem;
        }

        public void setMensagem(String mensagem) {
            this.mensagem = mensagem;
        }

        public ConversaRecente(String contato, String mensagem) {
            this.contato = contato;
            this.mensagem = mensagem;
        }
    }

    private String uid;
    private ArrayList<String> contatos = new ArrayList<>(); // uid dos contatos
    private HashMap<String, HashMap<String, String>> conversasRecentes = new HashMap<>();
    private DadosUsuario dadosUsuario = new DadosUsuario();

    public Usuario(){}

    public Usuario(String uid, String email) {
        this.uid = uid;
        this.dadosUsuario.setEmail(email);
    }

    public Usuario(String uid, String email, String imagem, ArrayList<String> contatos, HashMap<String, HashMap<String, String>> conversasRecentes) {
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

    public HashMap<String, HashMap<String, String>> getConversasRecentes(){
        return conversasRecentes;
    }

    public DadosUsuario getDadosUsuario() {
        return dadosUsuario;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setContatos(ArrayList<String> contatos) {
        this.contatos = contatos;
    }

    //public void addConversaRecente(ConversaRecente conversaRecente) {
        //this.conversasRecentes.add(conversaRecente);
    //}

    public void setDadosUsuario(DadosUsuario dadosUsuario) {
        this.dadosUsuario = dadosUsuario;
    }
}
