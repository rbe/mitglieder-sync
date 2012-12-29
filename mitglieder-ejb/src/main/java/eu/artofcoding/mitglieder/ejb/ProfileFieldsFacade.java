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

import eu.artofcoding.mitglieder.api.ProfileFieldsFacadeLocal;
import eu.artofcoding.mitglieder.persistence.ProfileFields;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

@Stateless
@TransactionAttribute(REQUIRES_NEW)
public class ProfileFieldsFacade implements ProfileFieldsFacadeLocal {

    @PersistenceContext
    private EntityManager em;

    public ProfileFields find(Object id) {
        return em.find(ProfileFields.class, id);
    }

    public List<ProfileFields> findAll() {
        return em.createNamedQuery("ProfileFields.findAll", ProfileFields.class).getResultList();
    }

    public List<ProfileFields> findByName(String name) {
        return em.createNamedQuery("ProfileFields.findByName", ProfileFields.class).setParameter("name", name).getResultList();
    }

    public List<ProfileFields> findByTitle(String title) {
        return em.createNamedQuery("ProfileFields.findByTitle", ProfileFields.class).setParameter("title", title).getResultList();
    }

}
