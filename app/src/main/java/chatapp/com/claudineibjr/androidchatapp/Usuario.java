package chatapp.com.claudineibjr.androidchatapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Claudinei on 22/08/2017.
 */

public class Usuario implements Serializable {

    private String uid;
    private String email;
    private String imagem;
    private ArrayList<String> contatos = new ArrayList<>(); // uid dos contatos
    private ArrayList<String> conversasRecentes = new ArrayList<>(); // uid das conversas
    private boolean conectado;
    private Date ultimaVez;

    public Usuario(){}

    public Usuario(String uid, String email) {
        this.uid = uid;
        this.email = email;
    }

    public Usuario(String uid, String email, String imagem, ArrayList<String> contatos, ArrayList<String> conversasRecentes) {
        this.uid = uid;
        this.email = email;
        this.imagem = imagem;
        this.contatos = contatos;
        this.conversasRecentes = conversasRecentes;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
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

    public boolean isConectado() {  return conectado;   }

    public void setConectado(boolean conectado) {   this.conectado = conectado; }

    public Date getUltimaVez() {    return ultimaVez;   }

    public void setUltimaVez(Date ultimaVez) {  this.ultimaVez = ultimaVez; }
}
