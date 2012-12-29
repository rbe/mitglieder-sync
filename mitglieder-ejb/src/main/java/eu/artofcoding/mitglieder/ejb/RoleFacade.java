/*
 * mitglieder-server
 * mitglieder-ejb
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 * Copyright (C) 2008-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 29.12.12 10:52
 */

package eu.artofcoding.mitglieder.ejb;

import eu.artofcoding.mitglieder.api.RoleFacadeLocal;
import eu.artofcoding.mitglieder.persistence.Role;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class RoleFacade implements RoleFacadeLocal {

    @PersistenceContext
    private EntityManager em;

    public void create(Role role) {
        em.persist(role);
    }

    public void edit(Role role) {
        em.merge(role);
    }

    public void remove(Role role) {
        em.remove(em.merge(role));
    }

    public Role find(Object id) {
        return em.find(Role.class, id);
    }

    public List<Role> findAll() {
        return em.createNamedQuery("Role.findAll", Role.class).getResultList();
    }

    public List<Role> findByName(String name) {
        return em.createNamedQuery("Role.findByName", Role.class).setParameter("name", name).getResultList();
    }

}
