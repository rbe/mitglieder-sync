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
@Table(name = "profile_values")
@NamedQueries({
        @NamedQuery(name = "ProfileValues.findAll", query = "SELECT p FROM ProfileValues p"),
        @NamedQuery(name = "ProfileValues.findByUid", query = "SELECT p FROM ProfileValues p WHERE p.uid = :uid"),
        @NamedQuery(name = "ProfileValues.findByPkid", query = "SELECT p FROM ProfileValues p WHERE p.pkid = :pkid")
})
public class ProfileValues implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "fid")
    private Integer fid;

    @Column(name = "uid")
    private Integer uid;

    @Lob
    @Column(name = "value", length = 65535)
    private String value;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "pkid", nullable = false)
    private Integer pkid;

    public ProfileValues() {
    }

    public ProfileValues(Integer pkid) {
        this.pkid = pkid;
    }

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        if (!(object instanceof ProfileValues)) {
            return false;
        }
        ProfileValues other = (ProfileValues) object;
        if ((this.pkid == null && other.pkid != null) || (this.pkid != null && !this.pkid.equals(other.pkid))) {
            return false;
        }
        return true;
    }

}
