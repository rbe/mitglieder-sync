/*
 * mitglieder-server
 * mitglieder-ejb
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 * Copyright (C) 2008-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 29.12.12 11:59
 */

package eu.artofcoding.mitglieder.ejb;

import eu.artofcoding.mitglieder.api.VermittlersucheFacadeLocal;
import eu.artofcoding.mitglieder.persistence.Vermittlersuche;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

@Stateless
@TransactionAttribute(REQUIRES_NEW)
public class VermittlersucheFacade implements VermittlersucheFacadeLocal {

    @PersistenceContext
    private EntityManager em;

    public void create(Vermittlersuche vermittlersuche) {
        em.persist(vermittlersuche);
    }

    public void edit(Vermittlersuche vermittlersuche) {
        em.merge(vermittlersuche);
    }

    public void remove(Vermittlersuche vermittlersuche) {
        em.remove(em.merge(vermittlersuche));
    }

    public Vermittlersuche find(Object id) {
        return em.find(Vermittlersuche.class, id);
    }

    public List<Vermittlersuche> findAll() {
        return em.createQuery("select object(o) from Vermittlersuche as o", Vermittlersuche.class).getResultList();
    }

    public List<Vermittlersuche> findByUid(Integer uid) {
        return em.createNamedQuery("Vermittlersuche.findByUid", Vermittlersuche.class).setParameter("uid", String.format("%d", uid)).getResultList();
    }

}
