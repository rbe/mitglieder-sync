/*
 * mitglieder-server
 * mitglieder-sync-desktop
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 * Copyright (C) 2008-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 29.12.12 13:26
 */

package eu.artofcoding.mitglieder.sync.desktop;

import eu.artofcoding.mitglieder.wsclient.Member;
import eu.artofcoding.mitglieder.wsclient.MemberData;
import eu.artofcoding.mitglieder.wsclient.MemberData.Data.Entry;
import eu.artofcoding.mitglieder.wsclient.MemberService;
import org.jdesktop.application.Action;
import org.jdesktop.application.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The application's main frame.
 */
public class BVKDesktopView extends FrameView {

    /**
     * Java Logging API
     */
    private static final Logger logger;

    /**
     * JDBC connection
     */
    protected Connection connection;

    private DatabaseConnectionDialog databaseConnectionDialog;

    private WatchDatabase watchDatabase;

    private class WatchDatabase extends Task<Void, Void> {

        private final int SLEEP_MIN;

        private Map<String, MemberData> data = null;

        public WatchDatabase(Application application, int sleepMin) {
            super(application);
            SLEEP_MIN = sleepMin;
        }

        private List<String[]> queryNewAndUpdatedMembers() throws SQLException {
            List<String[]> list = new ArrayList<String[]>(10000);
            data = new Hashtable<String, MemberData>();
            // Clean up
            cleanMemberTable();
            // Query database for new or updated members
            ResultSet resultSet = null;
            Statement stmt = connection.createStatement();
            resultSet = stmt.executeQuery("SELECT * FROM bvkplatform_contact_view");
            ResultSetMetaData rsMeta = resultSet.getMetaData();
            MemberData memberData = null;
            MemberData.Data memberDataData = null;
            MemberData.Data.Entry e = null;
//            DefaultTableModel model = ((DefaultTableModel) memberTable.getModel());
            while (resultSet.next()) {
                // Create MemberData object
                memberData = new MemberData();
                memberDataData = new MemberData.Data();
                // Fill MemberData object
                memberData.setMitnr(resultSet.getString("No_"));
                for (int i = 0; i < rsMeta.getColumnCount(); i++) {
                    e = new MemberData.Data.Entry();
                    e.setKey(rsMeta.getColumnName(i + 1));
                    e.setValue(resultSet.getString(i + 1));
                    memberDataData.getEntry().add(e);
                }
                memberData.setData(memberDataData);
                // Save memberData object
                data.put(memberData.getMitnr(), memberData);
                // Add data for table
                list.add(new String[]{
                        "" + (list.size() + 1),
                        memberData.getMitnr(), // see processData: memberData = data.get...
                        "",
                        "",
                        "",
                        "NEIN"
                });
                // Set number of rows
                memberCountLabel.setText("" + list.size());
            }
            // Clean up
            resultSet.close();
            stmt.close();
            //
            return list;
        }

        private void processNewAndUpdatedMembers() {
            DefaultTableModel model = ((DefaultTableModel) memberTable.getModel());
            // Simulate
            Entry entrySimulate = new Entry();
            entrySimulate.setKey("META_SIMULATE");
            entrySimulate.setValue("" + simulateCheckBox.isSelected());
            // UserAccount
            Entry entryUserAccount = new Entry();
            entryUserAccount.setKey("META_USERACCOUNT");
            entryUserAccount.setValue("" + userAccountCheckBox.isSelected());
            // MemberData
            Entry entryMemberData = new Entry();
            entryMemberData.setKey("META_MEMBERDATA");
            entryMemberData.setValue("" + memberDataCheckBox.isSelected());
            // Roles
            Entry entryRoles = new Entry();
            entryRoles.setKey("META_ROLES");
            entryRoles.setValue("" + roleCheckBox.isSelected());
            //
            MemberData memberData = null;
            // Connect to web service
            // C:\workspace\Java\IRB\BVKDesktop>wsimport -d build\classes -s src MemberService.wsdl
            try {
                MemberService service = new MemberService();
                Member port = service.getMemberPort();
                //
                if (null != port) {
                    // PreparedStatement for DELETING processed members
                    PreparedStatement deleteStmt = connection.prepareStatement("DELETE FROM dbo.[BVK$MyBVK Kontakte] WHERE [Contact No_] = ?");
                    for (int row = 0; row < model.getRowCount(); row++) {
                        // Get data
                        memberData = data.get(model.getValueAt(row, 1));
                        // Add META data
                        memberData.getData().getEntry().add(entryMemberData);
                        memberData.getData().getEntry().add(entryUserAccount);
                        memberData.getData().getEntry().add(entryRoles);
                        memberData.getData().getEntry().add(entrySimulate);
                        // Call web service operation
                        boolean result = port.maintainMember(memberData);
                        if (result) {
                            if (!simulateCheckBox.isSelected()) {
                                // Show in memberTable
                                model.setValueAt("JA", row, 5);
                                // Delete entry from bvkplatform_contact
                                try {
                                    deleteStmt.setObject(1, memberData.getMitnr());
                                    deleteStmt.executeUpdate();
                                } catch (SQLException e) {
                                    log(e.toString());
                                }
                            } else {
                                // Show in memberTable
                                model.setValueAt("JA - Simulation", row, 5);
                            }
                            // Log
                            log((simulateCheckBox.isSelected() ? "SIMULATION - " : "") + "Mitglied Nr. " + memberData.getMitnr() + " übertragen -> " + (result ? "OK (aus dbo.[BVK$MyBVK Kontakte] gelöscht)" : "FEHLER"));
                            // Decrease counter
                            memberCountLabel.setText("" + (Integer.valueOf(memberCountLabel.getText()) - 1));
                        } else {
                            log("FEHLER - Kann Web Service für Mitglied Nr. " + memberData.getMitnr() + " nicht erreichen");
                        }
                    }
                    deleteStmt.close();
                } else {
                    log("Kann Web Service Port nicht erzeugen!");
                }
            } // TODO Don't catch "all" Exception...
            catch (Exception e) {
                logger.log(Level.SEVERE, "", e);
                log("FEHLER: " + e);
            }
        }

        @Override
        protected Void doInBackground() throws Exception {
            setMessage("Arbeite...");
            while (!isCancelled()) {
                cleanMemberTable();
                try {
                    // Query database for updated members and update table model
                    for (String[] arr : queryNewAndUpdatedMembers()) {
                        ((DefaultTableModel) memberTable.getModel()).addRow(arr);
                    }
                    // Sleep
                    log("Warte " + SLEEP_MIN + " Minute(n)");
                    try {
                        Thread.sleep(SLEEP_MIN * 60 * 1000);
                        // Process every row
                        processNewAndUpdatedMembers();
                    } catch (InterruptedException e) {
//                        log("FEHLER: " + e);
                    }
                } catch (SQLException e) {
                    log(e.toString());
                    // Cancel task
                    cancelWatchDatabase();
                }
            }
            return null;
        }

        @Override
        protected void cancelled() {
            super.cancelled();
            cleanMemberTable();
        }

        @Override
        protected void finished() {
            super.finished();
            cleanMemberTable();
            log("Verarbeitung beendet!");
        }

    }

//    /**
//     *
//     */
//    private class UpdateAllMembersTask extends Task<Object, Void> {
//
//        UpdateAllMembersTask(Application application) {
//            super(application);
//        }
//
//        @Override
//        protected Object doInBackground() {
//            //
//            updateAllMembersButton.setEnabled(false);
//            txToggleButton.setEnabled(false);
//            //
//            log("Bereite alle Mitgliedsdatensätze vor...");
//            try {
//                connection.prepareCall("dbo.bvkplatform_updateall").execute();
//            } catch (SQLException e) {
//                log("FEHLER: " + e);
//                logger.log(Level.SEVERE, null, e);
//            } finally {
//                updateAllMembersButton.setEnabled(false);
//                txToggleButton.setEnabled(true);
//            }
//            return null;
//        }
//
//        @Override
//        protected void succeeded(Object result) {
//            log("...fertig!");
//        }
//
//    }

    static {
        logger = Logger.getLogger(BVKDesktopView.class.getName());
        Level lvl = Level.INFO;
        logger.setLevel(lvl);
        for (Handler handler : Logger.getLogger("").getHandlers()) {
            handler.setLevel(lvl);
        }
    }

    public BVKDesktopView(SingleFrameApplication app) {
        super(app);
        initComponents();
        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }

        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }

        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);
        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex =
                                0;
                        busyIconTimer.start();
                    }

                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }

            }

        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = BVKDesktopApp.getApplication().getMainFrame();
            aboutBox = new BVKDesktopAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        BVKDesktopApp.getApplication().show(aboutBox);
    }

//    /**
//     *
//     */
//    @Action
//    public void readCsvAction() {
//        // Open file dialog
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.addChoosableFileFilter(new FileFilter() {
//
//            @Override
//            public boolean accept(File f) {
//                if (f.isDirectory() || f.getName().endsWith(".txt") || f.getName().endsWith(".csv")) {
//                    return true;
//                } else {
//                    return false;
//                }
//
//            }
//
//            @Override
//            public String getDescription() {
//                return "BVK CSV Datei";
//            }
//        });
//        fileChooser.setVisible(true);
//        fileChooser.showOpenDialog(getComponent());
//        File f = fileChooser.getSelectedFile();
//        fileChooser.setVisible(false);
//        // Read CSV file
//        if (f != null) {
//            CsvReader csvReader = new CsvReader(f);
//            csvReader.init();
//            // Set label
//            csvCountValueLabel.setText("" + csvReader.getCount());
//        }
//
//    }

    @Action
    public void readDatabaseAction() {
        if (databaseConnectionDialog == null) {
            JFrame mainFrame = BVKDesktopApp.getApplication().getMainFrame();
            databaseConnectionDialog = new DatabaseConnectionDialog(mainFrame, true);
            databaseConnectionDialog.setLocationRelativeTo(mainFrame);
        }
        BVKDesktopApp.getApplication().show(databaseConnectionDialog);
        //
        connection = databaseConnectionDialog.getConnection();
        if (connection != null) {
            connectDatabase();
        } else {
            disconnectDatabase();
        }
    }

    @Action
    public Task toggleTxAction() {
        if (txToggleButton.isSelected()) {
            Integer intervall = new Integer("" + txMinutesComboBox.getSelectedItem());
            log("Übertrage alle " + intervall + " Minute(n)");
            txToggleButton.setText("Übertragung läuft!");
            watchDatabase = new WatchDatabase(getApplication(), intervall);
        } else {
            if (watchDatabase != null) {
                cancelWatchDatabase();
            } else {
                log("Kann Übertragung nicht abbrechen (watchDatabase==null)!");
            }
        }
        return watchDatabase;
    }

    @Action
    public void clearLogAction() {
        logTextArea.setText("");
    }

//    /**
//     *
//     * @return
//     */
//    @Action
//    public Task updateAllMembersAction() {
//        if (JOptionPane.showConfirmDialog(mainPanel, "Sicher?", "Alle Mitglieder übertragen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//            return new UpdateAllMembersTask(getApplication());
//        }
//        return null;
//    }

    @Action
    public void integrateDatabaseAction() {
        if (integrateDatabaseCheckBoxMenuItem.isSelected()) {
            initializeDatabase();
        } else {
            removeFromDatabase();
        }
    }

    @Action
    public void analyseDatabaseAction() {
        analyseDatabase();
    }

    private void cancelWatchDatabase() {
        watchDatabase.cancel(true);
        cleanMemberTable();
        txToggleButton.setText("Übertragung beginnen");
        txToggleButton.setSelected(false);
        watchDatabase = null;
    }

    /**
     * Clean contents of memberTable
     */
    private void cleanMemberTable() {
        DefaultTableModel model = ((DefaultTableModel) memberTable.getModel());
        model.setRowCount(0);
        memberCountLabel.setText("0");
    }

    private void initializeDatabase() {
        if (null != connection) {
            StringBuilder sql = new StringBuilder();
            try {
                Statement stmt = connection.createStatement();
                InputStream resourceAsStream = BVKDesktopView.class.getResourceAsStream("/eu/artofcoding/mitglieder/sync/desktop/sql/BVKPlatform.sql");
                BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream, "UTF-8"));
                String s = null;
                while ((s = br.readLine()) != null) {
                    if (s.equalsIgnoreCase("GO")) {
                        //
                        logger.fine(sql.toString() + (char) 10 + "GO");
                        if (databaseConnectionDialog.isDebugCheckboxSelected()) {
                            log("Executing SQL:" + (char) 10 + sql.toString());
                        }
                        //
                        stmt.executeBatch();
                        stmt.close();
                        stmt = connection.createStatement();
                        sql = new StringBuilder();
                    } else if (s.length() > 0) {
                        sql.append(s + (char) 10);
                        stmt.addBatch(s + (char) 10);
                    }
                }
                //
                log("OK: In Datenbank integriert");
            } catch (SQLException e) {
                log("FEHLER: " + e);
                logger.log(Level.SEVERE, null, e);
            } catch (IOException e) {
                log("FEHLER: " + e);
                logger.log(Level.SEVERE, null, e);
            }
        }
    }

    private void removeFromDatabase() {
        if (null != connection) {
            CallableStatement call = null;
            Statement stmt = null;
            try {
                call = connection.prepareCall("{call bvkplatform_inittable(?)}");
                call.setObject(1, "2");
                call.execute();
                call.close();
                //
                stmt = connection.createStatement();
                stmt.execute("DROP PROCEDURE bvkplatform_inittable");
                stmt.close();
                //
                log("OK: Integration in Datenbank aufgehoben");
            } catch (SQLException e) {
                log("FEHLER: " + e);
                logger.log(Level.SEVERE, null, e);
            } finally {
                disconnectDatabase();
            }
        }
    }

    private void analyseDatabase() {
        if (null != connection) {
            try {
                Statement stmt = connection.createStatement();
                ResultSet result = stmt.executeQuery("SELECT ROUTINE_NAME, ROUTINE_DEFINITION FROM INFORMATION_SCHEMA.ROUTINES WHERE ROUTINE_NAME LIKE 'bvkplatform%'");
//                BufferedReader br = null;
//                String s = null;
                while (result.next()) {
//                    br = new BufferedReader(result.getClob("Definition").getCharacterStream());
//                    while ((s = br.readLine()) != null) {
//                        log(s);
//                    }
                    log("" + result.getObject("ROUTINE_NAME") + (char) 10 + result.getObject("ROUTINE_DEFINITION"));
                }
                result.close();
                stmt.close();
                //
                stmt = connection.createStatement();
                result = stmt.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES");
                while (result.next()) {
                    log("Table: " + result.getObject("TABLE_NAME"));
                }
                result.close();
                stmt.close();
                //
                stmt = connection.createStatement();
                result = stmt.executeQuery("SELECT TABLE_NAME, VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS");
                while (result.next()) {
                    log("View: " + result.getObject("TABLE_NAME") + (char) 10 + result.getObject("VIEW_DEFINITION"));
                }
                result.close();
                stmt.close();
                //
                stmt = connection.createStatement();
                result = stmt.executeQuery("SELECT COUNT(*) c FROM dbo.[BVK$MyBVK Kontakte]");
                while (result.next()) {
                    log("COUNT(*) dbo.[BVK$MyBVK Kontakte]: " + result.getObject("c"));
                }
                result.close();
                stmt.close();
            } //            catch (IOException e) {
            //                log("FEHLER:" + e);
            //                logger.log(Level.SEVERE, null, e);
            //            }
            catch (SQLException e) {
                log("FEHLER: " + e);
                logger.log(Level.SEVERE, null, e);
            }
        }
    }

    private void connectDatabase() {
        log("Verbunden als " + databaseConnectionDialog.getPref().get("username", "KEIN WERT!") + " an " + databaseConnectionDialog.getPref().get("jdbcUrl", "KEIN WERT!"));
        //
        initializeDatabase();
        if (databaseConnectionDialog.isDebugCheckboxSelected()) {
            analyseDatabase();
        }
        //
        databaseConnectedLabel.setText("J");
        txToggleButton.setEnabled(true);
        readDatabaseButton.setEnabled(false);
        integrateDatabaseCheckBoxMenuItem.setEnabled(true);
        integrateDatabaseCheckBoxMenuItem.setSelected(true);
        analyseDatabaseMenuItem.setEnabled(true);
    }

    private void disconnectDatabase() {
        try {
            connection.close();
        } catch (SQLException e) {
            // ignore logger.log(Level.SEVERE, null, e);
        }
        connection = null;
        //
        readDatabaseButton.setEnabled(true);
        databaseConnectedLabel.setText("N");
        integrateDatabaseCheckBoxMenuItem.setEnabled(false);
        integrateDatabaseCheckBoxMenuItem.setSelected(false);
        analyseDatabaseMenuItem.setEnabled(false);
    }

    /**
     * Show log in logTextArea
     */
    private void log(String text) {
        logTextArea.append(new Date() + ": " + text + "\n");
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    //GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        databasePanel = new javax.swing.JPanel();
        readDatabaseButton = new javax.swing.JButton();
        databaseConnectLabel = new javax.swing.JLabel();
        databaseConnectedLabel = new javax.swing.JLabel();
        txToggleButton = new javax.swing.JToggleButton();
        txLabel = new javax.swing.JLabel();
        txMinutesComboBox = new javax.swing.JComboBox();
        txMinutesLabel = new javax.swing.JLabel();
        splitPane = new javax.swing.JSplitPane();
        memberPanel = new javax.swing.JPanel();
        memberScrollPane = new javax.swing.JScrollPane();
        memberTable = new javax.swing.JTable();
        memberCountLabel = new javax.swing.JLabel();
        memberCountTextLabel = new javax.swing.JLabel();
        simulateCheckBox = new javax.swing.JCheckBox();
        userAccountCheckBox = new javax.swing.JCheckBox();
        memberDataCheckBox = new javax.swing.JCheckBox();
        roleCheckBox = new javax.swing.JCheckBox();
        updateAllMembersButton = new javax.swing.JButton();
        logPanel = new javax.swing.JPanel();
        logScrollPane = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        analyseDatabaseMenuItem = new javax.swing.JMenuItem();
        integrateDatabaseCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        separator = new javax.swing.JSeparator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        databasePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Daten"));
        databasePanel.setName("databasePanel"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(BVKDesktopApp.class).getContext().getActionMap(BVKDesktopView.class, this);
        readDatabaseButton.setAction(actionMap.get("readDatabaseAction")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(BVKDesktopApp.class).getContext().getResourceMap(BVKDesktopView.class);
        readDatabaseButton.setText(resourceMap.getString("readDatabaseButton.text")); // NOI18N
        readDatabaseButton.setName("readDatabaseButton"); // NOI18N

        databaseConnectLabel.setText(resourceMap.getString("databaseConnectLabel.text")); // NOI18N
        databaseConnectLabel.setName("databaseConnectLabel"); // NOI18N

        databaseConnectedLabel.setText(resourceMap.getString("databaseConnectedLabel.text")); // NOI18N
        databaseConnectedLabel.setToolTipText(resourceMap.getString("databaseConnectedLabel.toolTipText")); // NOI18N
        databaseConnectedLabel.setName("databaseConnectedLabel"); // NOI18N

        txToggleButton.setAction(actionMap.get("toggleTxAction")); // NOI18N
        txToggleButton.setText(resourceMap.getString("txToggleButton.text")); // NOI18N
        txToggleButton.setName("txToggleButton"); // NOI18N

        txLabel.setText(resourceMap.getString("txLabel.text")); // NOI18N
        txLabel.setName("txLabel"); // NOI18N

        txMinutesComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"1", "5", "30", "60", "120", "240"}));
        txMinutesComboBox.setName("txMinutesComboBox"); // NOI18N

        txMinutesLabel.setText(resourceMap.getString("txMinutesLabel.text")); // NOI18N
        txMinutesLabel.setName("txMinutesLabel"); // NOI18N

        javax.swing.GroupLayout databasePanelLayout = new javax.swing.GroupLayout(databasePanel);
        databasePanel.setLayout(databasePanelLayout);
        databasePanelLayout.setHorizontalGroup(
                databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, databasePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(readDatabaseButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(databaseConnectLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(databaseConnectedLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 212, Short.MAX_VALUE)
                                .addGroup(databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(databasePanelLayout.createSequentialGroup()
                                                .addComponent(txLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(txMinutesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(txMinutesLabel))
                                        .addComponent(txToggleButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        databasePanelLayout.setVerticalGroup(
                databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(databasePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txMinutesLabel)
                                        .addComponent(txMinutesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txLabel)
                                        .addComponent(readDatabaseButton)
                                        .addComponent(databaseConnectLabel)
                                        .addComponent(databaseConnectedLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txToggleButton)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        splitPane.setDividerLocation(250);
        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane.setName("splitPane"); // NOI18N

        memberPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Neue oder geänderte Mitglieder"));
        memberPanel.setName("memberPanel"); // NOI18N

        memberScrollPane.setName("memberScrollPane"); // NOI18N

        memberTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "Nummer", "Mitgliedsnr.", "VSName 1", "VSName 2", "VSName 3", "Web Service"
                }
        ) {
            Class[] types = new Class[]{
                    java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean[]{
                    false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        memberTable.setName("memberTable"); // NOI18N
        memberScrollPane.setViewportView(memberTable);

        memberCountLabel.setText(resourceMap.getString("memberCountLabel.text")); // NOI18N
        memberCountLabel.setName("memberCountLabel"); // NOI18N

        memberCountTextLabel.setText(resourceMap.getString("memberCountTextLabel.text")); // NOI18N
        memberCountTextLabel.setName("memberCountTextLabel"); // NOI18N

        simulateCheckBox.setText(resourceMap.getString("simulateCheckBox.text")); // NOI18N
        simulateCheckBox.setName("simulateCheckBox"); // NOI18N

        userAccountCheckBox.setSelected(true);
        userAccountCheckBox.setText(resourceMap.getString("userAccountCheckBox.text")); // NOI18N
        userAccountCheckBox.setName("userAccountCheckBox"); // NOI18N

        memberDataCheckBox.setSelected(true);
        memberDataCheckBox.setText(resourceMap.getString("memberDataCheckBox.text")); // NOI18N
        memberDataCheckBox.setName("memberDataCheckBox"); // NOI18N

        roleCheckBox.setSelected(true);
        roleCheckBox.setText(resourceMap.getString("roleCheckBox.text")); // NOI18N
        roleCheckBox.setName("roleCheckBox"); // NOI18N

        updateAllMembersButton.setAction(actionMap.get("updateAllMembers")); // NOI18N
        updateAllMembersButton.setText(resourceMap.getString("updateAllMembersButton.text")); // NOI18N
        updateAllMembersButton.setEnabled(false);
        updateAllMembersButton.setName("updateAllMembersButton"); // NOI18N

        javax.swing.GroupLayout memberPanelLayout = new javax.swing.GroupLayout(memberPanel);
        memberPanel.setLayout(memberPanelLayout);
        memberPanelLayout.setHorizontalGroup(
                memberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, memberPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(memberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(memberScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
                                        .addGroup(memberPanelLayout.createSequentialGroup()
                                                .addComponent(memberCountTextLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(memberCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(simulateCheckBox)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 125, Short.MAX_VALUE)
                                                .addComponent(userAccountCheckBox)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(memberDataCheckBox)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(roleCheckBox))
                                        .addComponent(updateAllMembersButton))
                                .addContainerGap())
        );
        memberPanelLayout.setVerticalGroup(
                memberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(memberPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(memberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(memberCountTextLabel)
                                        .addComponent(memberCountLabel)
                                        .addComponent(roleCheckBox)
                                        .addComponent(memberDataCheckBox)
                                        .addComponent(userAccountCheckBox)
                                        .addComponent(simulateCheckBox))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(memberScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(updateAllMembersButton))
        );

        splitPane.setLeftComponent(memberPanel);

        logPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Logbuch"));
        logPanel.setName("logPanel"); // NOI18N

        logScrollPane.setName("logScrollPane"); // NOI18N

        logTextArea.setColumns(20);
        logTextArea.setEditable(false);
        logTextArea.setRows(5);
        logTextArea.setName("logTextArea"); // NOI18N
        logScrollPane.setViewportView(logTextArea);

        jButton1.setAction(actionMap.get("clearLogAction")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        javax.swing.GroupLayout logPanelLayout = new javax.swing.GroupLayout(logPanel);
        logPanel.setLayout(logPanelLayout);
        logPanelLayout.setHorizontalGroup(
                logPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, logPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(logPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(logScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
                                        .addComponent(jButton1))
                                .addContainerGap())
        );
        logPanelLayout.setVerticalGroup(
                logPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, logPanelLayout.createSequentialGroup()
                                .addComponent(logScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1))
        );

        splitPane.setRightComponent(logPanel);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 613, Short.MAX_VALUE)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(splitPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE)
                                                .addComponent(databasePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE))
                                        .addContainerGap()))
        );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 565, Short.MAX_VALUE)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(databasePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                                        .addContainerGap()))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        analyseDatabaseMenuItem.setAction(actionMap.get("analyseDatabaseAction")); // NOI18N
        analyseDatabaseMenuItem.setText(resourceMap.getString("analyseDatabaseMenuItem.text")); // NOI18N
        analyseDatabaseMenuItem.setName("analyseDatabaseMenuItem"); // NOI18N
        analyseDatabaseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                analyseDatabaseMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(analyseDatabaseMenuItem);

        integrateDatabaseCheckBoxMenuItem.setAction(actionMap.get("integrateDatabaseAction")); // NOI18N
        integrateDatabaseCheckBoxMenuItem.setText(resourceMap.getString("integrateDatabaseCheckBoxMenuItem.text")); // NOI18N
        integrateDatabaseCheckBoxMenuItem.setName("integrateDatabaseCheckBoxMenuItem"); // NOI18N
        fileMenu.add(integrateDatabaseCheckBoxMenuItem);

        separator.setName("separator"); // NOI18N
        fileMenu.add(separator);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
                statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
                        .addGroup(statusPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(statusMessageLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 443, Short.MAX_VALUE)
                                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(statusAnimationLabel)
                                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
                statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(statusPanelLayout.createSequentialGroup()
                                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(statusMessageLabel)
                                        .addComponent(statusAnimationLabel)
                                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }
    // </editor-fold>
    //GEN-END:initComponents

    private void analyseDatabaseMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_analyseDatabaseMenuItemActionPerformed
        analyseDatabase();
    }

    //GEN-LAST:event_analyseDatabaseMenuItemActionPerformed
    // Variables declaration - do not modify
    //GEN-BEGIN:variables
    private javax.swing.JMenuItem analyseDatabaseMenuItem;
    private javax.swing.JLabel databaseConnectLabel;
    protected javax.swing.JLabel databaseConnectedLabel;
    private javax.swing.JPanel databasePanel;
    private javax.swing.JCheckBoxMenuItem integrateDatabaseCheckBoxMenuItem;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel logPanel;
    private javax.swing.JScrollPane logScrollPane;
    private javax.swing.JTextArea logTextArea;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel memberCountLabel;
    private javax.swing.JLabel memberCountTextLabel;
    private javax.swing.JCheckBox memberDataCheckBox;
    private javax.swing.JPanel memberPanel;
    private javax.swing.JScrollPane memberScrollPane;
    private javax.swing.JTable memberTable;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton readDatabaseButton;
    private javax.swing.JCheckBox roleCheckBox;
    private javax.swing.JSeparator separator;
    private javax.swing.JCheckBox simulateCheckBox;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JLabel txLabel;
    private javax.swing.JComboBox txMinutesComboBox;
    private javax.swing.JLabel txMinutesLabel;
    private javax.swing.JToggleButton txToggleButton;
    private javax.swing.JButton updateAllMembersButton;
    private javax.swing.JCheckBox userAccountCheckBox;
    // End of variables declaration
    //GEN-END:variables

    private final Timer messageTimer;

    private final Timer busyIconTimer;

    private final Icon idleIcon;

    private final Icon[] busyIcons = new Icon[15];

    private int busyIconIndex = 0;

    private JDialog aboutBox;

}
