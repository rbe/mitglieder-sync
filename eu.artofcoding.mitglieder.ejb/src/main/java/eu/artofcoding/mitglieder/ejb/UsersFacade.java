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

import eu.artofcoding.mitglieder.api.ManageUserFacadeLocal;
import eu.artofcoding.mitglieder.api.UsersFacadeLocal;
import eu.artofcoding.mitglieder.helper.ResourceHelper;
import eu.artofcoding.mitglieder.persistence.Users;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.mail.Session;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import static eu.artofcoding.mitglieder.helper.ResourceHelper.MITGLIEDER_OK_BENUTZER_D;
import static eu.artofcoding.mitglieder.helper.ResourceHelper.RCPT;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

@Stateless
@TransactionAttribute(REQUIRES_NEW)
public class UsersFacade implements UsersFacadeLocal {

    private static final Logger logger = Logger.getLogger(UsersFacade.class.getName());

    @PersistenceContext
    private EntityManager em;

    @Resource(lookup = "mail/mitglieder")
    private Session session;

    @EJB
    private ManageUserFacadeLocal manageUserFacadeBean;

    public Calendar parseEndMembership(String endMembership) {
        Calendar endMembershipCal = null;
        try {
            if (null != endMembership && endMembership.length() > 9) {
                endMembershipCal = Calendar.getInstance();
                endMembershipCal.clear();
                if (endMembership.split("\\.").length >= 3) {
                    // 14.11.1950 00:00:00.0
                    // 0123456789
                    endMembershipCal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(endMembership.substring(0, 2)));
                    endMembershipCal.set(Calendar.MONTH, Integer.valueOf(endMembership.substring(3, 5)) - 1);
                    endMembershipCal.set(Calendar.YEAR, Integer.valueOf(endMembership.substring(6, 10)));
                } else if (endMembership.split("-").length >= 3) {
                    // 1938-04-16 00:00:00.0
                    // 0123456789
                    endMembershipCal.set(Calendar.YEAR, Integer.valueOf(endMembership.substring(0, 4)));
                    endMembershipCal.set(Calendar.MONTH, Integer.valueOf(endMembership.substring(5, 7)) - 1);
                    endMembershipCal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(endMembership.substring(8, 10)));
                }
            }
        } catch (NumberFormatException e) {
            logger.warning(String.format("Cannot process endMembership=%s: %s", endMembership, e.getMessage()));
            /*
            ResourceHelper.sendMail(session, ResourceHelper.RCPT,
            "Mitglieder: Benutzer " + users.getUid(),
            "Could not parse 'End Membership': " + endMembership + ":\n", e);
            */
            endMembershipCal = null;
        }
        return endMembershipCal;
    }

    public String calendarToGermanString(Calendar cal) {
        if (null != cal) {
            return new SimpleDateFormat("dd.MM.yyyy").format(cal.getTime());
        } else {
            return "(no date)";
        }
    }

    public void create(Users users, String endMembership) {
        // Check end membership
        Calendar endMembershipCal = parseEndMembership(endMembership);
        if (null != endMembershipCal && endMembershipCal.before(Calendar.getInstance())) {
            String date = calendarToGermanString(endMembershipCal);
            ResourceHelper.sendMail(session, RCPT,
                    String.format(MITGLIEDER_OK_BENUTZER_D, users.getUid()),
                    String.format("You were trying to create a user with end membership date in the past!%n'End Membership': %s", date),
                    null);
        } else {
            em.persist(users);
            String date = calendarToGermanString(endMembershipCal);
            ResourceHelper.sendMail(session, RCPT,
                    String.format(MITGLIEDER_OK_BENUTZER_D, users.getUid()),
                    String.format("Benutzer erfolgreich angelegt, 'End Membership'=%s", date),
                    null);
            if (null != endMembership && endMembership.length() > 0 && !endMembership.equals("null")) {
                manageUserFacadeBean.startEndMembershipTimer(users, endMembership);
            }
        }
    }

    public void edit(Users users, String endMembership) {
        em.merge(users);
        ResourceHelper.sendMail(session, RCPT,
                String.format(MITGLIEDER_OK_BENUTZER_D, users.getUid()),
                String.format("Benutzer %d erfolgreich aktualisiert", users.getUid()),
                null);
        if (null != endMembership && endMembership.length() > 0 && !endMembership.equals("null")) {
            manageUserFacadeBean.startEndMembershipTimer(users, endMembership);
        }
    }

    public void remove(Users users) {
        em.remove(em.merge(users));
    }

    public Users find(Object id) {
        return em.find(Users.class, id);
    }

    public List<Users> findAll() {
        return em.createNamedQuery("Users.findAll", Users.class).getResultList();
    }

    public Users findByUid(Integer uid) {
        List<Users> list = em.createNamedQuery("Users.findByUid", Users.class).setParameter("uid", uid).getResultList();
        return (null != list && list.size() > 0) ? list.get(0) : null;
    }

    public List<Users> findByUidName(String term) {
        TypedQuery<Users> query = em.createNamedQuery("Users.findByUidName", Users.class);
        List<Users> existingUsers = query.setParameter("sterm", term).setParameter("iterm", Integer.valueOf(term)).getResultList();
        if (existingUsers.size() == 0 && term.startsWith("0")) {
            existingUsers.addAll(query.setParameter("sterm", term.substring(1)).setParameter("iterm", Integer.valueOf(term.substring(1))).getResultList());
        }
        return existingUsers;
    }

}
