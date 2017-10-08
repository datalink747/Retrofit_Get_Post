package com.soussidev.kotlin.retrofit_get_post.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Soussi on 04/10/2017.
 */

public class User {

    @SerializedName("IdUser")
    @Expose
    private int IdUser;
    @SerializedName("NomUser")
    @Expose
    private String NomUser;
    @SerializedName("PrenomUser")
    @Expose
    private String PrenomUser;
    @SerializedName("CinUser")
    @Expose
    private int CinUser;
    @SerializedName("ImgUser")
    @Expose
    private String ImgUser;
    /*@SerializedName("TelUser")
    @Expose*/
   // private int TelUser;
    /*@SerializedName("AdressUser")
    @Expose*/
   // private String AdressUser;
    /*@SerializedName("PosetUser")
    @Expose*/
  //  private String PosetUser;



    public User() {
    }

    public User(String nomUser, String prenomUser, int cinUser, String imgUser) {
        NomUser = nomUser;
        PrenomUser = prenomUser;
        CinUser = cinUser;
        ImgUser = imgUser;
    }

    public int getIdUser() {
        return IdUser;
    }

    public void setIdUser(int idUser) {
        IdUser = idUser;
    }

    public String getNomUser() {
        return NomUser;
    }

    public void setNomUser(String nomUser) {
        NomUser = nomUser;
    }

    public String getPrenomUser() {
        return PrenomUser;
    }

    public void setPrenomUser(String prenomUser) {
        PrenomUser = prenomUser;
    }

    public int getCinUser() {
        return CinUser;
    }

    public void setCinUser(int cinUser) {
        CinUser = cinUser;
    }

    public String getImgUser() {
        return ImgUser;
    }

    public void setImgUser(String imgUser) {
        ImgUser = imgUser;
    }

    /*public int getTelUser() {
        return TelUser;
    }

    public void setTelUser(int telUser) {
        TelUser = telUser;
    }

    public String getAdressUser() {
        return AdressUser;
    }

    public void setAdressUser(String adressUser) {
        AdressUser = adressUser;
    }

    public String getPosetUser() {
        return PosetUser;
    }

    public void setPosetUser(String posetUser) {
        PosetUser = posetUser;
    }
*/


}
