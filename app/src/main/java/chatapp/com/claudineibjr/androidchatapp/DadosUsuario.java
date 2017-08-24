package chatapp.com.claudineibjr.androidchatapp;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Claudinei on 23/08/2017.
 */

public class DadosUsuario implements Serializable {
    private String email;
    private String imagem;
    private boolean conectado;
    private Date ultimaVez;

    public DadosUsuario(){}

    public DadosUsuario(String email){
        this.email = email;
    }

    public DadosUsuario(String email, String imagem){
        this.email = email;
        this.imagem = imagem;
    }

    @Override
    public String toString() {
        return "DadosUsuario{" +
                "email='" + email + '\'' +
                ", imagem='" + imagem + '\'' +
                ", conectado=" + conectado +
                ", ultimaVez=" + ultimaVez +
                '}';
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

    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }

    public Date getUltimaVez() {
        return ultimaVez;
    }

    public void setUltimaVez(Date ultimaVez) {
        this.ultimaVez = ultimaVez;
    }
}