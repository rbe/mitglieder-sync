/*
 * mitglieder-server
 * mitglieder-ejb
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 * Copyright (C) 2008-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 29.12.12 11:58
 */

package eu.artofcoding.mitglieder.ejb;

import eu.artofcoding.mitglieder.api.UsersRolesFacadeLocal;
import eu.artofcoding.mitglieder.persistence.UsersRoles;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

@Stateless
@TransactionAttribute(REQUIRES_NEW)
public class UsersRolesFacade implements UsersRolesFacadeLocal {

    @PersistenceContext
    private EntityManager em;

    public void create(UsersRoles usersRoles) {
        em.persist(usersRoles);
    }

    public void edit(UsersRoles usersRoles) {
        em.merge(usersRoles);
    }

    public void remove(UsersRoles usersRoles) {
        em.remove(em.merge(usersRoles));
    }

    public UsersRoles find(Object id) {
        return em.find(UsersRoles.class, id);
    }

    public List<UsersRoles> findAll() {
        return em.createNamedQuery("UsersRoles.findAll", UsersRoles.class).getResultList();
    }

    public List<UsersRoles> findByUid(String uid) {
        return em.createNamedQuery("UsersRoles.findByUid", UsersRoles.class).setParameter("uid", Integer.valueOf(uid)).getResultList();
    }

    public List<UsersRoles> findByUidRid(String uid, String rid) {
        return em.createNamedQuery("UsersRoles.findByUidRid", UsersRoles.class).setParameter("uid", Integer.valueOf(uid)).setParameter("rid", Integer.valueOf(rid)).getResultList();
    }

    /*
    public List<UsersRoles> findByMissingRid(String rid) {
        return em.createNamedQuery("UsersRoles.findByMissingRid", UsersRoles.class).setParameter("rid", Integer.valueOf(rid)).getResultList();
    }
    */

}
