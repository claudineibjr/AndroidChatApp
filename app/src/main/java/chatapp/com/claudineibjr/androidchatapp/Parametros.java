package chatapp.com.claudineibjr.androidchatapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Claudinei on 22/08/2017.
 */

public final class Parametros {

    private static DatabaseReference databaseReference;
    private static DatabaseReference usuarioReferencia = getDatabaseReference().child("usuario");
    private static DatabaseReference conversaReferencia = getDatabaseReference().child("conversa");
    private static DatabaseReference conexaoReferencia = FirebaseDatabase.getInstance().getReference(".info/connected");

    public static DatabaseReference getDatabaseReference(){

        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }

        return databaseReference;
    }
    public static DatabaseReference getUsuarioReferencia() {
        return usuarioReferencia;
    }

    public static DatabaseReference getConversaReferencia() {
        return conversaReferencia;
    }

    public static DatabaseReference getConexaoReferencia() {
        return conexaoReferencia;
    }
}
