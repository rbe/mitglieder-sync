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

import eu.artofcoding.mitglieder.api.ProfileValuesFacadeLocal;
import eu.artofcoding.mitglieder.persistence.ProfileValues;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

@Stateless
@TransactionAttribute(REQUIRES_NEW)
public class ProfileValuesFacade implements ProfileValuesFacadeLocal {

    @PersistenceContext
    private EntityManager em;

    public void create(ProfileValues profileValues) {
        em.persist(profileValues);
    }

    public void edit(ProfileValues profileValues) {
        em.merge(profileValues);
    }

    public void remove(ProfileValues profileValues) {
        em.remove(em.merge(profileValues));
    }

    public ProfileValues find(Object id) {
        return em.find(ProfileValues.class, id);
    }

    public List<ProfileValues> findAll() {
        return em.createNamedQuery("ProfileValues.findAll", ProfileValues.class).getResultList();
    }

    public List<ProfileValues> findByUid(String uid) {
        return em.createNamedQuery("ProfileValues.findByUid", ProfileValues.class).setParameter("uid", Integer.valueOf(uid)).getResultList();
    }

    public List<ProfileValues> findByFid(final Integer[] fid, Integer uid) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT pkid, uid, fid, value FROM profile_values p WHERE");
        // UID given?
        if (null != uid) {
            builder.append(" uid = ").append(uid).append(" AND");
        }
        // FID
        builder.append(" p.fid IN (");
        int j = fid.length;
        for (int i = 0; i < j; i++) {
            builder.append(fid[i]);
            if (i < j - 1) {
                builder.append(", ");
            }
        }
        builder.append(")");
        return em.createNativeQuery(builder.toString(), ProfileValues.class).getResultList();
    }

    public List<ProfileValues> findByFidAndValue(final Integer[] fid, Integer uid) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT pkid, uid, fid, value FROM profile_values p WHERE");
        // UID given?
        if (null != uid) {
            builder.append(" uid = ").append(uid).append(" AND");
        }
        // FID
        builder.append(" p.fid IN (");
        int j = fid.length;
        for (int i = 0; i < j; i++) {
            builder.append(fid[i]);
            if (i < j - 1) {
                builder.append(", ");
            }
        }
        builder.append(")");
        // value
        builder.append(" AND value IS NOT NULL AND value <> ''");
        return em.createNativeQuery(builder.toString(), ProfileValues.class).getResultList();
    }

}
