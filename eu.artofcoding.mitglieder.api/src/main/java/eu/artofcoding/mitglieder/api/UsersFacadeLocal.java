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

import eu.artofcoding.mitglieder.persistence.Users;

import javax.ejb.Local;
import java.util.Calendar;
import java.util.List;

@Local
public interface UsersFacadeLocal {

    void create(Users users, String endMembership);

    void edit(Users users, String endMembership);

    void remove(Users users);

    Users find(Object id);

    List<Users> findAll();

    Users findByUid(Integer term);

    List<Users> findByUidName(String term);

    Calendar parseEndMembership(String endMembership);

    String calendarToGermanString(Calendar cal);

}
