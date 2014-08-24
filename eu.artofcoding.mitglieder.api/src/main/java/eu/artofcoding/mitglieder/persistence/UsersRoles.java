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
@Table(name = "users_roles")
@NamedQueries({
        @NamedQuery(name = "UsersRoles.findAll", query = "SELECT u FROM UsersRoles u"),
        @NamedQuery(name = "UsersRoles.findByUid", query = "SELECT u FROM UsersRoles u WHERE u.usersRolesPK.uid = :uid"),
        @NamedQuery(name = "UsersRoles.findByRid", query = "SELECT u FROM UsersRoles u WHERE u.usersRolesPK.rid = :rid"),
        @NamedQuery(name = "UsersRoles.findByUidRid", query = "SELECT u FROM UsersRoles u WHERE u.usersRolesPK.uid = :uid AND u.usersRolesPK.rid = :rid")
        //@NamedQuery(name = "UsersRoles.findByMissingRid", query = "SELECT u FROM UsersRoles u WHERE u.usersRolesPK.rid NOT IN (SELECT ur.usersRolesPK.rid FROM UsersRoles ur WHERE ur.usersRolesPK.uid = :rid")
})
public class UsersRoles implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected UsersRolesPK usersRolesPK;

    public UsersRoles() {
    }

    public UsersRoles(UsersRolesPK usersRolesPK) {
        this.usersRolesPK = usersRolesPK;
    }

    public UsersRoles(int uid, int rid) {
        this.usersRolesPK = new UsersRolesPK(uid, rid);
    }

    public UsersRolesPK getUsersRolesPK() {
        return usersRolesPK;
    }

    public void setUsersRolesPK(UsersRolesPK usersRolesPK) {
        this.usersRolesPK = usersRolesPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (usersRolesPK != null ? usersRolesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UsersRoles)) {
            return false;
        }
        UsersRoles other = (UsersRoles) object;
        if ((this.usersRolesPK == null && other.usersRolesPK != null) || (this.usersRolesPK != null && !this.usersRolesPK.equals(other.usersRolesPK))) {
            return false;
        }
        return true;
    }

}
