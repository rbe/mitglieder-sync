/*
 * mitglieder-server
 * mitglieder-sync-desktop
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 * Copyright (C) 2008-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 29.12.12 13:19
 */

package eu.artofcoding.mitglieder.sync.desktop;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author otto
 */
public final class CsvReader {

    private boolean DEBUG = true;

    /**
     * Java Logging API
     */
    private Logger logger;

    /**
     * A file for reading CSV
     */
    private File csvFile;

    /**
     * Header (first line) in CSV file
     */
    private String[] header;

    /**
     * Values from CSV file
     */
    private List<String[]> values;

    /**
     * Cache key/value-pairs (see getLineAsMap)
     */
    private Map<Integer, Map<String, String>> valueCache;

    public CsvReader(File csvFile) {
        this.csvFile = csvFile;
        logger = Logger.getLogger(getClass().getName());
//        logger.setLevel(Level.ALL);
        values = new LinkedList<String[]>();
        valueCache = new Hashtable<Integer, Map<String, String>>();
    }

    /**
     * Read CSV data: first line is header
     */
    private void readCsv() {
        BufferedReader br = null;
        String s = null;
        String[] val = null;
        boolean firstLine = true;
        StringBuilder sb = null;
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((s = br.readLine()) != null) {
                // Read first line with header
                if (firstLine) {
                    firstLine = false;
                    header = s.split("\\;");
                    logger.finest("header=" + s + " -> " + header.length);
                } // Read line with values
                else {
                    val = s.split("\\;");
                    if (header.length != val.length) {
                        // Debug
                        if (DEBUG) {
                            sb = new StringBuilder();
                            for (String v : val) {
                                sb.append(v + ";");
                            }
                            logger.fine("header.length=" + header.length + " values[i].length=" + val.length + " -> values=" + sb.toString());
                        }
                    }
                    if (header.length == val.length) {
                        values.add(val);
                    }
                    logger.finest("values=" + s);
                }
            }
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Initialize: read CSV file
     */
    public synchronized void init() {
        readCsv();
    }

    /**
     * Number of lines read from CSV file
     */
    public int getCount() {
        return values.size();
    }

    /**
     * Return line from CSV file
     */
    public String[] getLine(int lineNumber) {
        return values.get(lineNumber);
    }

    /**
     * Return certain line from CSV file as map
     */
    public Map<String, String> getLineAsMap(int lineNumber) {
        Map<String, String> map = null;
        // Lookup line in cache
//        synchronized (valueCache) {
        map = valueCache.get(lineNumber);
        // Debug
        if (map != null) {
            logger.fine("Got line " + lineNumber + " from cache!");
        }
//        }
        // Not found in cache
        if (map == null) {
            map = new Hashtable<String, String>();
            String[] v = values.get(lineNumber);
            int i = 0;
            for (String h : header) {
                map.put(h, v[i++]);
            }
        }
        // Debug
        logger.fine("lineAsMap=" + map);
        // Save map for caching
//        synchronized (valueCache) {
//        valueCache.notifyAll();
        valueCache.put(lineNumber, map);
//        }
        return map;
    }

    public Map<String, String>[] getAllLines() {
        List<Hashtable<String, String>> list = new ArrayList<Hashtable<String, String>>();
        for (int i = 0; i < getCount(); i++) {
            list.add((Hashtable<String, String>) getLineAsMap(i));
        }
        return list.toArray(new Hashtable[0]);
    }

    public String getValue(int lineNumber, int column) {
        return values.get(lineNumber)[column];
    }

//    /**
//     * 
//     * @param args
//     */
//    public static void main(String[] args) {
//        CsvReader c = new CsvReader(new File("c:/mitglieder.csv"));
//        c.init();
//        //
//        c.getLineAsMap(0);
//        c.getLineAsMap(1);
//        c.getLineAsMap(1);
//        c.getLineAsMap(2);
//        //
//        Map<String, String>[] all = c.getAllLines();
//        for (Map<String, String> a : all) {
//            System.out.println("" + a.get("MIT-NR"));
//        }
//    }

}
