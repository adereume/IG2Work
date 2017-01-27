package com.example.anais.ig2work.Utils;

/**
 * Created by Utilisateur on 24/11/2016.
 */

public enum StringUtils {
    URL("http://projetmobile.alwaysdata.net/data.php"),
    IDUSER("idUser"),
    FIRSTNAME("firstname"),
    LASTNAME("lastname"),
    PASSWORD("password"),
    ROLE("role"),
    ATTEMPT_CONNEXION("attempt_connexion"),

    error_champ("Ce champ semble incorrect"),
    error_pseudo_existing("Ce pseudo est déjà utilisé"),
    error_email_existing("L'email est utilisé par un autre compte");

    private String name = "";

    StringUtils(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }
}
