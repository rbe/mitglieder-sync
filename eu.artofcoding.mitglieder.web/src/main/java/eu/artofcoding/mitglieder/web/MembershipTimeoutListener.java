/*
 * mitglieder-server
 * mitglieder-web
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 * Copyright (C) 2008-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 29.12.12 12:30
 */

package eu.artofcoding.mitglieder.web;

import eu.artofcoding.mitglieder.api.ManageUserFacadeLocal;
import eu.artofcoding.mitglieder.api.ProfileValuesFacadeLocal;
import eu.artofcoding.mitglieder.api.UsersFacadeLocal;

import javax.ejb.EJB;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Logger;

@WebListener
public class MembershipTimeoutListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(MembershipTimeoutListener.class.getName());

    @EJB
    private ManageUserFacadeLocal manageUserFacade;

    @EJB
    private UsersFacadeLocal usersFacade;

    @EJB
    private ProfileValuesFacadeLocal profileValuesFacade;

    public void contextInitialized(ServletContextEvent sce) {
        /*
        // Select all members with membership end date > now
        List<ProfileValues> profileValues = profileValuesFacade.findByFidAndValue(new Integer[]{56}, null);
        Users user;
        for (ProfileValues pv : profileValues) {
            if (null != pv.getUid() && null != pv.getValue()) {
                user = usersFacade.findByUid(pv.getUid());
                if (null != user) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("Starting endMembership timer for uid=" + pv.getUid() + "/email=" + user.getMail());
                    }
                    manageUserFacade.startEndMembershipTimer(user, pv.getValue());
                }
            }
        }
        */
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }

}
