/*
 * mitglieder-server
 * mitglieder-api
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 * Copyright (C) 2008-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 28.12.12 17:38
 */

package eu.artofcoding.mitglieder.api;

import eu.artofcoding.mitglieder.persistence.ProfileValues;

import java.util.List;
import javax.ejb.Local;

@Local
public interface ProfileValuesFacadeLocal {

    void create(ProfileValues profileValues);

    void edit(ProfileValues profileValues);

    void remove(ProfileValues profileValues);

    ProfileValues find(Object id);

    List<ProfileValues> findAll();

    List<ProfileValues> findByUid(final String uid);

    List<ProfileValues> findByFid(final Integer[] fid, final Integer uid);

    List<ProfileValues> findByFidAndValue(final Integer[] fid, final Integer uid);

}
