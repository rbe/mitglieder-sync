/*
 * mitglieder-server
 * mitglieder-web
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 * Copyright (C) 2008-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 28.12.12 18:04
 */

package eu.artofcoding.mitglieder.web;

import eu.artofcoding.mitglieder.adapter.drupal.DrupalProfileAdapter;
import eu.artofcoding.mitglieder.api.*;
import eu.artofcoding.mitglieder.persistence.*;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebService
public class Member {

    private static final Logger logger = Logger.getLogger(Member.class.getName());

    /**
     * Facade for user management.
     */
    @EJB
    private ManageUserFacadeLocal manageUserFacadeBean;

    /**
     * Facade for User entity
     */
    @EJB
    private UsersFacadeLocal usersFacade;

    /**
     * Facade for ProfileValues entity
     */
    @EJB
    private ProfileValuesFacadeLocal profileValuesFacade;

    /**
     * Facade for ProfileFields entity
     */
    @EJB
    private ProfileFieldsFacadeLocal profileFieldsFacade;

    /**
     * Facade for UsersRoles entity
     */
    @EJB
    private UsersRolesFacadeLocal usersRolesFacade;

    /**
     * Facade for Role entity
     */
    @EJB
    private RoleFacadeLocal roleFacade;

    @EJB
    private ChangeDateLocal changeDateBean;

    static {
        DrupalProfileAdapter.addMapping("No_", new String[]{"profile_bvkdatenbank_mitgliedsnummer"});
        DrupalProfileAdapter.addMapping("Status", new String[]{"profile_bvkdatenbank_status"});
        DrupalProfileAdapter.addMapping("MyBVKLogin", new String[]{"profile_bvkdatenbank_login"});
        DrupalProfileAdapter.addMapping("MyBVKPasswort", new String[]{"profile_bvkdatenbank_passwort"});
        DrupalProfileAdapter.addMapping("VSName1", new String[]{"profile_bvkdatenbank_name_1"});
        DrupalProfileAdapter.addMapping("VSName2", new String[]{"profile_bvkdatenbank_name_2"});
        DrupalProfileAdapter.addMapping("VSName3", new String[]{"profile_bvkdatenbank_name_3"});
        DrupalProfileAdapter.addMapping("Address 2", new String[]{"profile_bvkdatenbank_strasse"});
        DrupalProfileAdapter.addMapping("Post Code", new String[]{"profile_bvkdatenbank_plz"});
        DrupalProfileAdapter.addMapping("City", new String[]{"profile_bvkdatenbank_ort"});
        DrupalProfileAdapter.addMapping("Country Code", new String[]{"profile_bvkdatenbank_land"});
        DrupalProfileAdapter.addMapping("Phone No_", new String[]{"profile_bvkdatenbank_telefon"});
        DrupalProfileAdapter.addMapping("Fax No_", new String[]{"profile_bvkdatenbank_telefax"});
        DrupalProfileAdapter.addMapping("Mobile Phone No_", new String[]{"profile_bvkdatenbank_mobil"});
        DrupalProfileAdapter.addMapping("Phone No_ privat", new String[]{"profile_bvkdatenbank_telefonprivat"});
        DrupalProfileAdapter.addMapping("Fax No_ privat", new String[]{"profile_bvkdatenbank_telefaxprivat"});
        DrupalProfileAdapter.addMapping("Post-office box", new String[]{"profile_bvkdatenbank_postfach"});
        DrupalProfileAdapter.addMapping("Post Code Post-office box", new String[]{"profile_bvkdatenbank_postfach_plz"});
        DrupalProfileAdapter.addMapping("City Post-office box", new String[]{"profile_bvkdatenbank_postfach_ort"});
        DrupalProfileAdapter.addMapping("E-Mail", new String[]{"profile_bvkdatenbank_email"});
        DrupalProfileAdapter.addMapping("E-Mail Newsletter", new String[]{"profile_bvkdatenbank_newsletter_email"});
        DrupalProfileAdapter.addMapping("Home Page", new String[]{"profile_bvkdatenbank_homepage"});
        DrupalProfileAdapter.addMapping("Salutation Code", new String[]{"profile_bvkdatenbank_anrede"});
        DrupalProfileAdapter.addMapping("Correspondence Salutation Code", new String[]{"profile_bvkdatenbank_briefanrede"});
        DrupalProfileAdapter.addMapping("Companies", new String[]{"profile_bvkdatenbank_unternehmen", "textarea"});
        DrupalProfileAdapter.addMapping("Birth Date", new String[]{"profile_bvkdatenbank_geburtstag", "datestring"});
        DrupalProfileAdapter.addMapping("Begin Membership", new String[]{"profile_bvkdatenbank_mitglied_seit", "datestring"});
        DrupalProfileAdapter.addMapping("End Membership", new String[]{"profile_bvkdatenbank_mitgliedschaft_ende", "datestring"});
        DrupalProfileAdapter.addMapping("FirmengruendungAm", new String[]{"profile_bvkdatenbank_selbstaendig_seit", "datestring"});
        DrupalProfileAdapter.addMapping("PensionaerSeit", new String[]{"profile_bvkdatenbank_pensionaer_seit", "datestring"});
        DrupalProfileAdapter.addMapping("Bezirksverband", new String[]{"profile_bvkdatenbank_bv"});
        DrupalProfileAdapter.addMapping("Regionalverband", new String[]{"profile_bvkdatenbank_rv"});
        DrupalProfileAdapter.addMapping("Rechtsform", new String[]{"profile_bvkdatenbank_rechtsform"});
        DrupalProfileAdapter.addMapping("Handelsregistereintrag", new String[]{"profile_bvkdatenbank_handelsregister"});
        DrupalProfileAdapter.addMapping("Vermittlerart", new String[]{"profile_bvkdatenbank_vermittlerart"});
        DrupalProfileAdapter.addMapping("Sparte", new String[]{"profile_bvkdatenbank_sparte"});
        DrupalProfileAdapter.addMapping("Beitragsgruppe", new String[]{"profile_bvkdatenbank_beitragsgruppe"});
        DrupalProfileAdapter.addMapping("MitgliedschaftBestaetigtAm", new String[]{"profile_bvkdatenbank_bestaetigung_am", "datestring"});
        DrupalProfileAdapter.addMapping("Kündigungsdatum", new String[]{"profile_bvkdatenbank_kuendigungsdatum", "datestring"});
        DrupalProfileAdapter.addMapping("KuendigungBestaetigtAm", new String[]{"profile_bvkdatenbank_kdg_bestaetigt_am", "datestring"});
        DrupalProfileAdapter.addMapping("Kuendigungsgrund", new String[]{"profile_bvkdatenbank_kuendigungsgrund"});
        DrupalProfileAdapter.addMapping("Inhaber / GF 1", new String[]{"profile_bvkdatenbank_inhaber_gf1"});
        DrupalProfileAdapter.addMapping("Inhaber / GF 2", new String[]{"profile_bvkdatenbank_inhaber_gf2"});
        DrupalProfileAdapter.addMapping("Inhaber / GF 3", new String[]{"profile_bvkdatenbank_inhaber_gf3"});
        DrupalProfileAdapter.addMapping("Ausbildung", new String[]{"profile_bvkdatenbank_ausbildung", "textarea"});
        DrupalProfileAdapter.addMapping("Pensionär", new String[]{"profile_bvkdatenbank_pensionaer"});
        DrupalProfileAdapter.addMapping("Ehrenamt", new String[]{"profile_bvkdatenbank_ehrenamt", "textarea"});
        DrupalProfileAdapter.addMapping("Bank", new String[]{"profile_bankdaten_bank"});
        DrupalProfileAdapter.addMapping("Bankleitzahl", new String[]{"profile_bankdaten_bankleitzahl"});
        DrupalProfileAdapter.addMapping("Kontoinhaber", new String[]{"profile_bankdaten_kontoinhaber"});
        DrupalProfileAdapter.addMapping("Kontonummer", new String[]{"profile_bankdaten_kontonummer"});
        DrupalProfileAdapter.addMapping("Lastschrift", new String[]{"profile_bankdaten_lastschrift"});
        DrupalProfileAdapter.addMapping("Search Name", new String[]{"profile_bvkdatenbank_suchname"});
    }

    private boolean myStartsWith(String in, String[] what) {
        boolean b = false;
        for (String s : what) {
            if (in.startsWith(s)) {
                b = true;
                break;
            }
        }
        return b;
    }

    private String mapBvkToRole(String functionCode) {
        String r = null;
        // Map function code
        if (functionCode.startsWith("AVV_")) {
            r = "AVV - Vorstand";
        } else if (functionCode.startsWith("BV_BWREF")) {
            r = "BV - Betriebswirtschaft";
        } else if (functionCode.startsWith("BV_DELEG")) {
            r = "BV - Delegierter HV";
        } else if (functionCode.startsWith("BV_JUNIOR")) {
            r = "BV - Junior";
        } else if (functionCode.startsWith("BV_MA_MFA")) {
            r = "BV - Makler + MFA";
        } else if (functionCode.startsWith("BV_MARKET")) {
            r = "BV - Marketing";
        } else if (functionCode.startsWith("BV_PRESSE")) {
            r = "BV - Pressesprecher";
        } else if (functionCode.startsWith("BV_VOR")) {
            r = "BV - Vorstand";
        } else if (myStartsWith(functionCode, new String[]{"BV_RECHPR", "BV_SCHATZ", "BV_SCHRIFT", "BV_STVVOR", "BV_EHRENVO", "BV_BEIRAT", "BV_IHK"})) {
            r = "BV - Vorstandsmitglied";
        } else if (functionCode.startsWith("BV_AUS_WEI")) {
            r = "BV - Weiterbildung";
        } else if (functionCode.startsWith("BEIRAT_")) {
            r = "BVK - Beirat";
        } else if (functionCode.startsWith("EH_RAT_")) {
            r = "BVK - Ehrenrat";
        } else if (functionCode.startsWith("GEM_RAT_")) {
            r = "BVK - Gemeinschaftsrat";
        } else if (myStartsWith(functionCode, new String[]{"GF_VERBAND", "GF_LTD", "GF_HGF"})) {
            r = "BVK - Geschäftsführung";
        } else if (functionCode.startsWith("PR_RAT_")) {
            r = "BVK - Präsidialrat";
        } else if (myStartsWith(functionCode, new String[]{"BVK_PRÄSID", "BVK_VIZE"})) {
            r = "BVK - Präsidium";
        } else if (myStartsWith(functionCode, new String[]{"GF_GS_AN", "GF_REF", "GF_VW", "GF_VW_LTG"})) {
            r = "BVK - Verwaltung";
        } else if (functionCode.startsWith("RV_")) {
            r = "RV - Mitglied";
        } else if (myStartsWith(functionCode, new String[]{"RV_VOR", "RV_STVVOR"})) {
            r = "RV - Vorstand";
        } else if (functionCode.startsWith("VV_")) {
            r = "VV - Vertretervereinigung";
        }
        //
        logger.finer("mapped role " + functionCode + " to " + r);
        return r;
    }

    private void tableUsers(boolean simulate, Map<String, String> map) {
        String mitnr = map.get("No_");
        if (null == mitnr) {
            throw new IllegalArgumentException("Null argument!");
        }
        //
        if (!simulate) {
            //
            List<Users> existingUsers = usersFacade.findByUidName(mitnr);
            logger.fine("found " + existingUsers.size() + " existing users for mitnr=" + mitnr);
            if (null == existingUsers || (null != existingUsers && existingUsers.size() == 0)) {
                // Create member
                logger.fine("creating member " + mitnr);
                // Set values from map
                Users users = new Users();
                users.setUid(Integer.valueOf(map.get("MyBVKLogin")));
                users.setName(map.get("MyBVKLogin"));
                users.setPass(map.get("MyBVKPasswort"));
                users.setMail(map.get("E-Mail"));
                users.setInit(map.get("E-Mail"));
                users.setData("a:7:{s:7:\"contact\";i:0;s:5:\"block\";a:2:{s:5:\"block\";a:3:{i:14;i:1;i:8;i:1;i:7;i:1;}s:5:\"views\";a:1:{s:13:\"monats_archiv\";i:1;}}s:14:\"picture_delete\";i:0;s:14:\"picture_upload\";s:0:\"\";s:16:\"privatemsg_allow\";i:1;s:28:\"privatemsg_setmessage_notify\";i:1;s:20:\"privatemsg_mailalert\";s:1:\"0\";}");
                users.setStatus((short) 1);
                users.setCreated(1209398559);
                users.setTimezone("0");
                users.setLanguage("de");
                users.setSignature(""); // NOT NULL
                users.setTheme(""); // NOT NULL
                users.setPicture(""); // NOT NULL
                users.setSort((short) 0); // NOT NULL
                users.setThreshold((short) 0); // NOT NULL
                usersFacade.create(users, map.get("End Membership"));
            } else {
                // Update member
                logger.fine("updating member " + mitnr);
                // Set values from map
                // UPDATE users SET uid = ?, name = ?, pass = MD5(?), mail = ?, init = ?, data = ?,
                // status = 1, created = 1209398559, language = 'de', timezone = 0 WHERE uid = ?
                Users users = existingUsers.get(0);
                users.setPass(map.get("MyBVKPasswort"));
                users.setMail(map.get("E-Mail"));
                users.setInit(map.get("E-Mail"));
                usersFacade.edit(users, map.get("End Membership"));
            }
        } else {
            logger.warning("DID NOTHING - SIMULATING!");
        }
    }

    private void tableProfileValues(boolean simulate, Map<String, String> map) {
        String mitnr = map.get("No_");
        if (null == mitnr) {
            throw new IllegalArgumentException("Null argument!");
        }
        // Delete users' entries in ProfileValues
        List<Users> existingUsers = usersFacade.findByUidName(mitnr);
        if (existingUsers.size() > 0) {
            Users users = existingUsers.get(0);
            manageUserFacadeBean.removeValues(users);
        }
        //
        ProfileValues nProfileValues = null;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        // For every field/value
        for (String key : map.keySet()) {
            // Get value for key
            String value = map.get(key);
            // Get fid from profile_fields (name)
            List<ProfileFields> fidByName = profileFieldsFacade.findByName(DrupalProfileAdapter.getDrupalName(key));
            // Check if we found a 'fid'
            if (null != fidByName && fidByName.size() > 0) {
                // Drupal type
                String drupalType = DrupalProfileAdapter.getDrupalType(key);
                // FID
                Integer fid = fidByName.get(0).getFid();
                // ... and value is not empty
                if (value != null && value.length() > 0) {
                    // Trim
                    value = value.trim();
                    // Check type and possible conversions
                    if (drupalType.startsWith("date")) {
                        cal.clear();
                        if (value.split("\\.").length >= 3) {
                            // 14.11.1950 00:00:00.0
                            // 0123456789
                            cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(value.substring(0, 2)));
                            cal.set(Calendar.MONTH, Integer.valueOf(value.substring(3, 5)) - 1);
                            cal.set(Calendar.YEAR, Integer.valueOf(value.substring(6, 10)));
                        } else if (value.split("-").length >= 3) {
                            // 1938-04-16 00:00:00.0
                            // 0123456789
                            cal.set(Calendar.YEAR, Integer.valueOf(value.substring(0, 4)));
                            cal.set(Calendar.MONTH, Integer.valueOf(value.substring(5, 7)) - 1);
                            cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(value.substring(8, 10)));
                        }
                        if (drupalType.equals("datestring")) {
                            value = sdf.format(cal.getTime());
                        } else {
                            value = DrupalProfileAdapter.createDate(cal);
                        }
                    }
                }
                // Debug
                logger.fine("adding profile_values for member " + mitnr + ": DB: " + key + " -> Drupal: type=" + drupalType + ", name=" + fid + ", value=" + value);
                //
                if (!simulate) {
                    nProfileValues = new ProfileValues();
                    nProfileValues.setFid(fid);
                    nProfileValues.setUid(Integer.valueOf(mitnr));
                    nProfileValues.setValue(value);
                    profileValuesFacade.create(nProfileValues);
                }
            }
        }

    }

    private void applyRole(String mitnr, String role) {
        // Check argument
        if (null == mitnr || null == role) {
            throw new IllegalArgumentException("Null argument!");
        }
        // Get 'rid' for role'name'
        List<Role> roleByName = roleFacade.findByName(role);
        if (null != roleByName && roleByName.size() > 0) {
            int rid = roleByName.get(0).getRid();
            List<UsersRoles> ridAndUid = usersRolesFacade.findByUidRid(mitnr, "" + rid);
            if (null == ridAndUid || ridAndUid.size() == 0) {
                usersRolesFacade.create(new UsersRoles(Integer.valueOf(mitnr), rid));
                logger.fine("added role for mitnr=" + mitnr + " -> " + role + " rid=" + rid);
            }
        } else {
            logger.warning("role '" + role + "' for mitnr=" + mitnr + " NOT found");
        }
    }

    private void tableUsersRoles(boolean simulate, Map<String, String> map) {
        String mitnr = map.get("No_");
        // Check argument
        if (null == mitnr || null == map) {
            throw new IllegalArgumentException("Null argument!");
        }
        //
        if (!simulate) {
            // Remove all roles
            List<Users> list = usersFacade.findByUidName(mitnr);
            if (list.size() > 0) {
                Users users = list.get(0);
                manageUserFacadeBean.removeRoles(users);
            }
            // Rolle "Mitglied" immer setzen
            applyRole(mitnr, "1Mitglied");
            // Bezirksverband
            applyRole(mitnr, "BV - " + map.get("Bezirksverband"));
            // Regionalverband
            applyRole(mitnr, "RV - " + map.get("Regionalverband"));
            // Function Codes
            String functionCode = map.get("Function Codes");
            if (functionCode != null) {
                String[] roles = functionCode.split(",");
                for (String r : roles) {
                    String role = mapBvkToRole(r);
                    if (role != null) {
                        applyRole(mitnr, role);
                    }
                }
            }
            // Ehrenämter (nur bei Kommissionen)
            String ehrenamt = map.get("Ehrenamt");
            if (ehrenamt != null) {
                logger.finer("ehrenamt for " + mitnr + " = " + ehrenamt);
                String[] pairs = ehrenamt.split(",");
                for (int i = 0; i < pairs.length; i += 2) {
                    if (pairs[i].indexOf("Kommission") >= 0) {
                        applyRole(mitnr, pairs[i] + " - " + pairs[i + 1]);
                    }
                }
            }
        }
    }

    /**
     * Web service operation: insert member or update its data
     */
    @WebMethod(operationName = "maintainMember")
    public boolean maintainMember(@WebParam(name = "memberData") MemberData memberData) {
        // Check argument
        if (null == memberData) {
            throw new IllegalArgumentException("Null argument!");
        }
        // Translate web service parameter/type MemberData into Map
        Map<String, String> map = new HashMap<>();
        map.put("No_", memberData.getMitnr());
        List<MemberData.Data.Entry> entries = memberData.getData().getEntry();
        for (MemberData.Data.Entry entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        // Debug
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("received new request: " + map);
        }
        //
        boolean success = false;
        try {
            // Simulate
            boolean simulate = false;
            if (map.get("META_SIMULATE").equals("true")) {
                simulate = true;
                if (logger.isLoggable(Level.INFO)) {
                    logger.info("SIMULATION!");
                }
            }
            // Table users
            if (map.get("META_USERACCOUNT").equals("true")) {
                tableUsers(simulate, map);
            }
            // Table profileValues
            if (map.get("META_MEMBERDATA").equals("true")) {
                tableProfileValues(simulate, map);
            }
            // Table user_roles
            if (map.get("META_ROLES").equals("true")) {
                tableUsersRoles(simulate, map);
            }
            // No exception was thrown
            success = true;
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Illegal argument exception", e);
        }
        return success;
    }

    @WebMethod(operationName = "changeDrupalDateToString")
    public boolean changeDrupalDateToString(@WebParam(name = "uid") final Integer uid) {
        changeDateBean.changeDrupalDateToString(uid);
        return true;
    }

    @WebMethod(operationName = "changeAllDrupalDateToString")
    public boolean changeAllDrupalDateToString() {
        changeDateBean.changeAllDrupalDateToString();
        return true;
    }

}
