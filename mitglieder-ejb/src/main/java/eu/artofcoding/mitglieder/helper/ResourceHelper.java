/*
 * mitglieder-server
 * mitglieder-ejb
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 * Copyright (C) 2008-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 29.12.12 11:55
 */

package eu.artofcoding.mitglieder.helper;

import eu.artofcoding.mitglieder.ejb.UsersFacade;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourceHelper {

    private static final Logger logger = Logger.getLogger(UsersFacade.class.getName());

    public static String RCPT;

    public static final String MITGLIEDER_OK_BENUTZER_D = "Mitglieder: [OK] Benutzer %d";

    public static final String MITGLIEDER_FEHLER_BENUTZER_D = "Mitglieder: [FEHLER] Benutzer %d";

    public static final String MITGLIEDER_FEHLER_BENUTZER_D_WURDE_NICHT_ENTFERNT = "Mitglieder: [FEHLER] Benutzer %d wurde nicht entfernt";

    public static final String MITGLIEDER_OK_BENUTZER_D_WURDE_ENTFERNT = "Mitglieder: [OK] Benutzer %d wurde entfernt";

    static {
        String systemRcpt = System.getProperty("mitglieder.mail.rcpt");
        if (null != systemRcpt) {
            RCPT = systemRcpt;
        } else {
            RCPT = "bvkwebservice@medienhof.de";
        }
    }

    public static void sendMail(Session session, String recipient, String subject, String text, Exception exception) {
        try {
            Message message = new MimeMessage(session);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient, false));
            message.setSubject(subject);
            message.setText(text);
            // Exception
            if (null != exception) {
                StringBuilder t = new StringBuilder();
                for (StackTraceElement elt : exception.getStackTrace()) {
                    t.append("at ").append(elt.getClassName()).append(".").append(elt.getMethodName());
                    t.append("(").append(elt.getFileName()).append(":").append(elt.getLineNumber()).append("): ");
                }
                message.setText(String.format("%s%n%nException:%n%s", text, t.toString()));
            }
            message.setHeader("X-Mailer", ResourceHelper.class.getName());
            message.setSentDate(new Date());
            // Send message
            Transport.send(message);
        } catch (MessagingException e) {
            logger.log(Level.SEVERE, String.format("Could not send email: %s", e.getMessage()));
        }
    }

}
