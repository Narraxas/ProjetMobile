package com.example.projet_interim;

public class CurentUser {

    private static CurentUser curentUser = null;

    public String id;
    public String username;
    public String role;

    protected CurentUser(){}

    public static synchronized CurentUser getInstance() {
        if(curentUser == null){
            curentUser = new CurentUser();
        }
        return curentUser;
    }

}
