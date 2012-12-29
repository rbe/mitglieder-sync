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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UsersRolesPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "uid", nullable = false)
    private int uid;

    @Basic(optional = false)
    @Column(name = "rid", nullable = false)
    private int rid;

    public UsersRolesPK() {
    }

    public UsersRolesPK(int uid, int rid) {
        this.uid = uid;
        this.rid = rid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) uid;
        hash += (int) rid;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UsersRolesPK)) {
            return false;
        }
        UsersRolesPK other = (UsersRolesPK) object;
        if (this.uid != other.uid) {
            return false;
        }
        if (this.rid != other.rid) {
            return false;
        }
        return true;
    }

}
