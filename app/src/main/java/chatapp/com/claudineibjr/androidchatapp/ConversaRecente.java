package chatapp.com.claudineibjr.androidchatapp;

import java.io.Serializable;

/**
 * Created by Claudinei on 10/09/2017.
 */

public class ConversaRecente implements Serializable {

    private String emailContato;
    private Mensagem mensagem;

    public ConversaRecente(String emailContato, Mensagem mensagem) {
        this.emailContato = emailContato;
        this.mensagem = mensagem;
    }

    public ConversaRecente() {
    }

    public String getEmailContato() {
        return emailContato;
    }

    public void setEmailContato(String emailContato) {
        this.emailContato = emailContato;
    }

    public Mensagem getMensagem() {
        return mensagem;
    }

    public void setMensagem(Mensagem mensagem) {
        this.mensagem = mensagem;
    }

    @Override
    public String toString() {
        return "ConversaRecente{" +
                "emailContato='" + emailContato + '\'' +
                ", mensagem=" + mensagem.toString() +
                '}';
    }
}
