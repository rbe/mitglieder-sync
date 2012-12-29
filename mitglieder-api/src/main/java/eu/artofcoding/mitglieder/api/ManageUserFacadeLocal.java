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

@Local
public interface ManageUserFacadeLocal {

    void startEndMembershipTimer(Users users, String endMembership);

    void fullRemove(Users users);

    void removeRoles(Users users);

    void removeValues(Users users);

}
