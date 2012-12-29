/*
 * mitglieder-server
 * mitglieder-ejb
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 * Copyright (C) 2008-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 29.12.12 12:04
 */

package eu.artofcoding.mitglieder.ejb;

import eu.artofcoding.mitglieder.api.*;
import eu.artofcoding.mitglieder.helper.ResourceHelper;
import eu.artofcoding.mitglieder.persistence.ProfileValues;
import eu.artofcoding.mitglieder.persistence.Users;
import eu.artofcoding.mitglieder.persistence.UsersRoles;
import eu.artofcoding.mitglieder.persistence.Vermittlersuche;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.mail.Session;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static eu.artofcoding.mitglieder.helper.ResourceHelper.*;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

@Stateless
@TransactionAttribute(REQUIRES_NEW)
public class ManageUserFacadeBean implements ManageUserFacadeLocal {

    private static final Logger logger = Logger.getLogger(ManageUserFacadeBean.class.getName());

    @Resource
    private SessionContext sessionCtx;

    @Resource(name = "mail/mitglieder")
    private Session session;

    @EJB
    private UsersFacadeLocal usersFacade;

    @EJB
    private UsersRolesFacadeLocal usersRolesFacade;

    @EJB
    private ProfileValuesFacadeLocal profileValuesFacade;

    @EJB
    private VermittlersucheFacadeLocal vermittlersucheFacade;

    /**
     * Fully remove user
     * @param timer
     */
    @Timeout
    public void timeout(Timer timer) {
        // Get user info from serialized object
        Users info = (Users) timer.getInfo();
        // Find actual entity of user
        List<Users> users = usersFacade.findByUidName("" + info.getUid());
        Users u = users.get(0);
        // Send email notification
        String today = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        /*
        ResourceHelper.sendMail(session, ResourceHelper.RCPT,
                String.format("Mitglieder: [OK] Benutzer %d", u.getUid()),
                String.format("Benutzer wird jetzt ('End Membership'=%s) entfernt", today),
                null);
        */
        // Remove user
        fullRemove(u);
        // Cancel timer
        timer.cancel();
    }

    /**
     * Start timer for 'End Membership'
     * @param users
     * @param endMembership
     */
    public void startEndMembershipTimer(Users users, String endMembership) {
        if (null != endMembership && endMembership.length() > 0 && !endMembership.equals("null")) {
            Calendar endMembershipCal = usersFacade.parseEndMembership(endMembership);
            if (null != endMembershipCal) {
                // Check if date is in the future
                Calendar now = Calendar.getInstance();
                if (endMembershipCal.after(now)) {
                    // Start timer
                    sessionCtx.getTimerService().createTimer(endMembershipCal.getTime(), users);
                    String date = usersFacade.calendarToGermanString(endMembershipCal);
                    ResourceHelper.sendMail(session, RCPT,
                            String.format(MITGLIEDER_OK_BENUTZER_D, users.getUid()),
                            String.format("Benutzer wird am %s entfernt", date),
                            null);
                } else if (endMembershipCal.before(now)) {
                    // Fully remove user
                    fullRemove(users);
                }
            }
        }
    }

    /**
     * Fully remove a user/member.
     * @param users
     */
    public void fullRemove(Users users) {
        try {
            // Remove from 'Vermittlersuche'
            removeFromVermittlersuche(users);
            // Remove all roles for user
            removeRoles(users);
            // Remove all values for user
            removeValues(users);
            //
            List<ProfileValues> profileValues = profileValuesFacade.findByUid(String.format("%d", users.getUid()));
            for (ProfileValues pv : profileValues) {
                profileValuesFacade.remove(pv);
            }
            // Remove user
            usersFacade.remove(users);
            // Send email
            ResourceHelper.sendMail(session, RCPT,
                    String.format(MITGLIEDER_OK_BENUTZER_D_WURDE_ENTFERNT, users.getUid()),
                    String.format("Benutzer %d aus der Datenbank entfernt", users.getUid()),
                    null);
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Cannot remove user %d", users.getUid()), e);
            // Send email
            ResourceHelper.sendMail(session, RCPT,
                    String.format(MITGLIEDER_FEHLER_BENUTZER_D_WURDE_NICHT_ENTFERNT, users.getUid()),
                    String.format("Exception:%n%s", e.getMessage()),
                    null);

        }
    }

    /**
     * Remove user from 'Vermittlersuche'.
     * @param users
     */
    public void removeFromVermittlersuche(Users users) {
        List<Vermittlersuche> vermittlerSuche = vermittlersucheFacade.findByUid(users.getUid());
        for (Vermittlersuche vs : vermittlerSuche) {
            vermittlersucheFacade.remove(vs);
        }
    }

    /**
     * Remove all values for user.
     * @param users
     */
    public void removeValues(Users users) {
        List<ProfileValues> profileValues = profileValuesFacade.findByUid(String.format("%d", users.getUid()));
        for (ProfileValues pv : profileValues) {
            profileValuesFacade.remove(pv);
        }
    }

    /**
     * Remove all roles for user.
     * @param users
     */
    public void removeRoles(Users users) {
        List<UsersRoles> usersRoles = usersRolesFacade.findByUid(String.format("%d", users.getUid()));
        for (UsersRoles ur : usersRoles) {
            usersRolesFacade.remove(ur);
        }
    }

}
