/*
 * mitglieder-server
 * mitglieder-ejb
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 * Copyright (C) 2008-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 28.12.12 17:37
 */

package eu.artofcoding.mitglieder.ejb;

import eu.artofcoding.mitglieder.adapter.drupal.DrupalProfileAdapter;
import eu.artofcoding.mitglieder.api.ChangeDateLocal;
import eu.artofcoding.mitglieder.api.ProfileValuesFacadeLocal;
import eu.artofcoding.mitglieder.api.UsersFacadeLocal;
import eu.artofcoding.mitglieder.persistence.ProfileValues;
import eu.artofcoding.mitglieder.persistence.Users;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

@Stateless
@TransactionAttribute(REQUIRES_NEW)
public class ChangeDateBean implements ChangeDateLocal {

    private static final Logger logger = Logger.getLogger(ChangeDateBean.class.getName());

    @EJB
    private ProfileValuesFacadeLocal profileValuesFacade;

    @EJB
    private UsersFacadeLocal usersFacade;

    public void changeDrupalDateToString(Integer uid) {
        // Select all ProfileValues with date:
        // fid=45 profile_bvkdatenbank_geburtstag
        // fid=47 profile_bvkdatenbank_pensionaer_seit
        // fid=49 profile_bvkdatenbank_selbstaendig_seit
        // fid=54 profile_bvkdatenbank_mitglied_seit
        // fid=55 profile_bvkdatenbank_bestaetigung_am
        // fid=56 profile_bvkdatenbank_mitgliedschaft_ende
        // fid=57 profile_bvkdatenbank_kuendigungsdatum
        // fid=59 profile_bvkdatenbank_kdg_bestaetigt_am
        List<ProfileValues> result = profileValuesFacade.findByFid(new Integer[]{45, 47, 49, 54, 55, 56, 57, 59}, uid);
        // Transform Drupal date into string
        Calendar now = Calendar.getInstance();
        Calendar pvCalendar;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        for (ProfileValues pv : result) {
            String value = pv.getValue();
            if (null != value) {
                if (value.startsWith("a")) {
                    pvCalendar = DrupalProfileAdapter.parseDate(value);
                    // Check if date is in future -> reset to null
                    if (null == pvCalendar || pvCalendar.after(now)) {
                        pv.setValue(null);
                    } else {
                        // Transform Drupal date into string
                        pv.setValue(sdf.format(pvCalendar.getTime()));
                    }
                } else if (value.split("-").length >= 3) {
                    Calendar cal = Calendar.getInstance();
                    // 1938-04-16 00:00:00.0
                    // 0123456789
                    cal.set(Calendar.YEAR, Integer.valueOf(value.substring(0, 4)));
                    cal.set(Calendar.MONTH, Integer.valueOf(value.substring(5, 7)) - 1);
                    cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(value.substring(8, 10)));
                    // Transform Drupal date into string
                    pv.setValue(sdf.format(cal.getTime()));
                }
                // Update ProfileValues with date string
                profileValuesFacade.edit(pv);
            }
        }
    }

    public void changeAllDrupalDateToString() {
        List<Users> users = usersFacade.findAll();
        for (Users u : users) {
            if (null != u && null != u.getUid()) {
                changeDrupalDateToString(u.getUid());
            }
        }
    }
}
