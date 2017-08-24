package chatapp.com.claudineibjr.androidchatapp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Claudinei on 22/08/2017.
 */

public class Usuario implements Serializable {

    private String uid;
    private ArrayList<String> contatos = new ArrayList<>(); // uid dos contatos
    private ArrayList<String> conversasRecentes = new ArrayList<>(); // uid das conversas
    private DadosUsuario dadosUsuario = new DadosUsuario();

    public Usuario(){}

    public Usuario(String uid, String email) {
        this.uid = uid;
        this.dadosUsuario.setEmail(email);
    }

    public Usuario(String uid, String email, String imagem, ArrayList<String> contatos, ArrayList<String> conversasRecentes) {
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

    public ArrayList<String> getConversasRecentes(){
        return conversasRecentes;
    }

    public DadosUsuario getDadosUsuario() {
        return dadosUsuario;
    }
}
