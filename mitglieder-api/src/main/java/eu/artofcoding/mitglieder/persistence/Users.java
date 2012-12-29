/*
 * mitglieder-server
 * mitglieder-api
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 * Copyright (C) 2008-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 28.12.12 17:54
 */

package eu.artofcoding.mitglieder.persistence;

import javax.persistence.*;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
@NamedQueries({
        @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
        @NamedQuery(name = "Users.findByUid", query = "SELECT u FROM Users u WHERE u.uid = :uid"),
        @NamedQuery(name = "Users.findByName", query = "SELECT u FROM Users u WHERE u.name = :name"),
        @NamedQuery(name = "Users.findByUidName", query = "SELECT u FROM Users u WHERE u.name = :sterm OR u.uid = :iterm"),
        @NamedQuery(name = "Users.findByMail", query = "SELECT u FROM Users u WHERE u.mail = :mail"),
        @NamedQuery(name = "Users.findByMode", query = "SELECT u FROM Users u WHERE u.mode = :mode"),
        @NamedQuery(name = "Users.findBySort", query = "SELECT u FROM Users u WHERE u.sort = :sort"),
        @NamedQuery(name = "Users.findByThreshold", query = "SELECT u FROM Users u WHERE u.threshold = :threshold"),
        @NamedQuery(name = "Users.findByTheme", query = "SELECT u FROM Users u WHERE u.theme = :theme"),
        @NamedQuery(name = "Users.findBySignature", query = "SELECT u FROM Users u WHERE u.signature = :signature"),
        @NamedQuery(name = "Users.findByCreated", query = "SELECT u FROM Users u WHERE u.created = :created"),
        @NamedQuery(name = "Users.findByAccess", query = "SELECT u FROM Users u WHERE u.access = :access"),
        @NamedQuery(name = "Users.findByLogin", query = "SELECT u FROM Users u WHERE u.login = :login"),
        @NamedQuery(name = "Users.findByStatus", query = "SELECT u FROM Users u WHERE u.status = :status"),
        @NamedQuery(name = "Users.findByTimezone", query = "SELECT u FROM Users u WHERE u.timezone = :timezone"),
        @NamedQuery(name = "Users.findByLanguage", query = "SELECT u FROM Users u WHERE u.language = :language"),
        @NamedQuery(name = "Users.findByPicture", query = "SELECT u FROM Users u WHERE u.picture = :picture"),
        @NamedQuery(name = "Users.findByInit", query = "SELECT u FROM Users u WHERE u.init = :init")
})
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "uid", nullable = false)
    private Integer uid;

    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 60)
    private String name;

    @Basic(optional = false)
    @Column(name = "pass", nullable = false, length = 32)
    private String pass;

    @Column(name = "mail", length = 64)
    private String mail;

    @Basic(optional = false)
    @Column(name = "mode", nullable = false)
    private short mode;

    @Column(name = "sort")
    private Short sort;

    @Column(name = "threshold")
    private Short threshold;

    @Basic(optional = false)
    @Column(name = "theme", nullable = false, length = 255)
    private String theme;

    @Basic(optional = false)
    @Column(name = "signature", nullable = false, length = 255)
    private String signature;

    @Basic(optional = false)
    @Column(name = "created", nullable = false)
    private int created;

    @Basic(optional = false)
    @Column(name = "access", nullable = false)
    private int access;

    @Basic(optional = false)
    @Column(name = "login", nullable = false)
    private int login;

    @Basic(optional = false)
    @Column(name = "status", nullable = false)
    private short status;

    @Column(name = "timezone", length = 8)
    private String timezone;

    @Basic(optional = false)
    @Column(name = "language", nullable = false, length = 12)
    private String language;

    @Basic(optional = false)
    @Column(name = "picture", nullable = false, length = 255)
    private String picture;

    @Column(name = "init", length = 64)
    private String init;

    @Lob
    @Column(name = "data", length = 2147483647)
    private String data;

    public Users() {
    }

    public Users(Integer uid) {
        this.uid = uid;
    }

    public Users(Integer uid, String name, String pass, short mode, String theme, String signature, int created, int access, int login, short status, String language, String picture) {
        this.uid = uid;
        this.name = name;
        this.pass = pass;
        this.mode = mode;
        this.theme = theme;
        this.signature = signature;
        this.created = created;
        this.access = access;
        this.login = login;
        this.status = status;
        this.language = language;
        this.picture = picture;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            byte[] result = md5.digest(pass.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < result.length; i++) {
                hexString.append(String.format("%02x", 0xFF & result[i]));
            }
            this.pass = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Logger.getLogger(Users.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public short getMode() {
        return mode;
    }

    public void setMode(short mode) {
        this.mode = mode;
    }

    public Short getSort() {
        return sort;
    }

    public void setSort(Short sort) {
        this.sort = sort;
    }

    public Short getThreshold() {
        return threshold;
    }

    public void setThreshold(Short threshold) {
        this.threshold = threshold;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public int getLogin() {
        return login;
    }

    public void setLogin(int login) {
        this.login = login;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getInit() {
        return init;
    }

    public void setInit(String init) {
        this.init = init;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uid != null ? uid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Users)) {
            return false;
        }
        Users other = (Users) object;
        if ((this.uid == null && other.uid != null) || (this.uid != null && !this.uid.equals(other.uid))) {
            return false;
        }
        return true;
    }

}
