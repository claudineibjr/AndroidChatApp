package chatapp.com.claudineibjr.androidchatapp;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Claudinei on 07/09/2017.
 */

public class Mensagem implements Serializable {

    private String texto;
    private Date data;
    private String remetente; //uid
    private String destinatario; //uid

    public Mensagem(String remetente, String destinatario, String texto, Date data) {
        this.texto = texto;
        this.data = data;
        this.remetente = remetente;
        this.destinatario = destinatario;
    }

    public Mensagem(){}

    public String getTexto() {
        return texto;
    }

    public Date getData() {
        return data;
    }

    public String getRemetente() {
        return remetente;
    }

    public String getDestinatario() {
        return destinatario;
    }
}
