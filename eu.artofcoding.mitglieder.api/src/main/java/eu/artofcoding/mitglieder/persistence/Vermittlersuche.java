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

@Entity
@Table(name = "vermittlersuche")
@NamedQueries({
        @NamedQuery(name = "Vermittlersuche.findAll", query = "SELECT v FROM Vermittlersuche v"),
        @NamedQuery(name = "Vermittlersuche.findByUid", query = "SELECT v FROM Vermittlersuche v WHERE v.uid = :uid"),
        @NamedQuery(name = "Vermittlersuche.findByVorname", query = "SELECT v FROM Vermittlersuche v WHERE v.vorname = :vorname"),
        @NamedQuery(name = "Vermittlersuche.findByName", query = "SELECT v FROM Vermittlersuche v WHERE v.name = :name"),
        @NamedQuery(name = "Vermittlersuche.findByFirma1", query = "SELECT v FROM Vermittlersuche v WHERE v.firma1 = :firma1"),
        @NamedQuery(name = "Vermittlersuche.findByFirma2", query = "SELECT v FROM Vermittlersuche v WHERE v.firma2 = :firma2"),
        @NamedQuery(name = "Vermittlersuche.findByKz", query = "SELECT v FROM Vermittlersuche v WHERE v.kz = :kz"),
        @NamedQuery(name = "Vermittlersuche.findByVermittlersuche", query = "SELECT v FROM Vermittlersuche v WHERE v.vermittlersuche = :vermittlersuche"),
        @NamedQuery(name = "Vermittlersuche.findByBild", query = "SELECT v FROM Vermittlersuche v WHERE v.bild = :bild"),
        @NamedQuery(name = "Vermittlersuche.findByPlz", query = "SELECT v FROM Vermittlersuche v WHERE v.plz = :plz"),
        @NamedQuery(name = "Vermittlersuche.findByOrt", query = "SELECT v FROM Vermittlersuche v WHERE v.ort = :ort"),
        @NamedQuery(name = "Vermittlersuche.findByStrasse", query = "SELECT v FROM Vermittlersuche v WHERE v.strasse = :strasse"),
        @NamedQuery(name = "Vermittlersuche.findByTelefon", query = "SELECT v FROM Vermittlersuche v WHERE v.telefon = :telefon"),
        @NamedQuery(name = "Vermittlersuche.findByFax", query = "SELECT v FROM Vermittlersuche v WHERE v.fax = :fax"),
        @NamedQuery(name = "Vermittlersuche.findByMobil", query = "SELECT v FROM Vermittlersuche v WHERE v.mobil = :mobil"),
        @NamedQuery(name = "Vermittlersuche.findByHomepage", query = "SELECT v FROM Vermittlersuche v WHERE v.homepage = :homepage"),
        @NamedQuery(name = "Vermittlersuche.findByEmail", query = "SELECT v FROM Vermittlersuche v WHERE v.email = :email"),
        @NamedQuery(name = "Vermittlersuche.findByPkid", query = "SELECT v FROM Vermittlersuche v WHERE v.pkid = :pkid")
})
public class Vermittlersuche implements Serializable {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @Column(name = "uid", nullable = false, length = 255)
    private String uid;
    @Basic(optional = false)
    @Column(name = "vorname", nullable = false, length = 255)
    private String vorname;
    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    @Basic(optional = false)
    @Column(name = "firma1", nullable = false, length = 255)
    private String firma1;
    @Basic(optional = false)
    @Column(name = "firma2", nullable = false, length = 255)
    private String firma2;
    @Basic(optional = false)
    @Column(name = "kz", nullable = false, length = 10)
    private String kz;
    @Basic(optional = false)
    @Column(name = "vermittlersuche", nullable = false)
    private int vermittlersuche;
    @Basic(optional = false)
    @Column(name = "bild", nullable = false, length = 255)
    private String bild;
    @Basic(optional = false)
    @Lob
    @Column(name = "beschreibung", nullable = false, length = 65535)
    private String beschreibung;
    @Basic(optional = false)
    @Column(name = "plz", nullable = false, length = 20)
    private String plz;
    @Basic(optional = false)
    @Column(name = "ort", nullable = false, length = 255)
    private String ort;
    @Basic(optional = false)
    @Column(name = "strasse", nullable = false, length = 255)
    private String strasse;
    @Basic(optional = false)
    @Column(name = "telefon", nullable = false, length = 255)
    private String telefon;
    @Basic(optional = false)
    @Column(name = "fax", nullable = false, length = 255)
    private String fax;
    @Basic(optional = false)
    @Column(name = "mobil", nullable = false, length = 255)
    private String mobil;
    @Basic(optional = false)
    @Column(name = "homepage", nullable = false, length = 255)
    private String homepage;
    @Basic(optional = false)
    @Column(name = "email", nullable = false, length = 200)
    private String email;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "pkid", nullable = false)
    private Integer pkid;

    public Vermittlersuche() {
    }

    public Vermittlersuche(Integer pkid) {
        this.pkid = pkid;
    }

    public Vermittlersuche(Integer pkid, String uid, String vorname, String name, String firma1, String firma2, String kz, int vermittlersuche, String bild, String beschreibung, String plz, String ort, String strasse, String telefon, String fax, String mobil, String homepage, String email) {
        this.pkid = pkid;
        this.uid = uid;
        this.vorname = vorname;
        this.name = name;
        this.firma1 = firma1;
        this.firma2 = firma2;
        this.kz = kz;
        this.vermittlersuche = vermittlersuche;
        this.bild = bild;
        this.beschreibung = beschreibung;
        this.plz = plz;
        this.ort = ort;
        this.strasse = strasse;
        this.telefon = telefon;
        this.fax = fax;
        this.mobil = mobil;
        this.homepage = homepage;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirma1() {
        return firma1;
    }

    public void setFirma1(String firma1) {
        this.firma1 = firma1;
    }

    public String getFirma2() {
        return firma2;
    }

    public void setFirma2(String firma2) {
        this.firma2 = firma2;
    }

    public String getKz() {
        return kz;
    }

    public void setKz(String kz) {
        this.kz = kz;
    }

    public int getVermittlersuche() {
        return vermittlersuche;
    }

    public void setVermittlersuche(int vermittlersuche) {
        this.vermittlersuche = vermittlersuche;
    }

    public String getBild() {
        return bild;
    }

    public void setBild(String bild) {
        this.bild = bild;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getMobil() {
        return mobil;
    }

    public void setMobil(String mobil) {
        this.mobil = mobil;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getPkid() {
        return pkid;
    }

    public void setPkid(Integer pkid) {
        this.pkid = pkid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pkid != null ? pkid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Vermittlersuche)) {
            return false;
        }
        Vermittlersuche other = (Vermittlersuche) object;
        if ((this.pkid == null && other.pkid != null) || (this.pkid != null && !this.pkid.equals(other.pkid))) {
            return false;
        }
        return true;
    }

}
