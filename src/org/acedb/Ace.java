/*
 *                    BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors.  These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 *      http://www.biojava.org/
 *
 */

package org.acedb;

import java.util.*;
import java.net.*;

public class Ace {
    /**
     * You can't make one of these for love nor money.
     */

    private Ace() {
    }

    private static Set drivers;
    private static Map databases;

    static {
	drivers = new HashSet();
	databases = new HashMap();

	Properties sysProp = System.getProperties();
	String pkgs = (String) sysProp.get("java.protocol.handler.pkgs");
	String myPkg = "org";
	if(pkgs != null)
	    pkgs = pkgs + "|" + myPkg;
	else
	    pkgs = myPkg;
    
	sysProp.put("java.protocol.handler.pkgs", pkgs);
    }

      /**
       * Register a driver with the manager.
       * <P>
       * The driver will be asked if it can accept a particular
       * ace url, and if so, it may be used to create a
       * Database reprenting that url.
       */
    public static void registerDriver(Driver driver) {
	drivers.add(driver);
    }
  
    /**
     * Retrieves the database associated with this url.
     */

    static Database getDatabase(AceURL url) throws AceException {
      url = rootURL(url);
      System.out.println("Retrieving database for URL " + url);
      Database db = (Database) databases.get(url);
     
     LOAD_DRIVER:
      if(db == null) {
        for(Iterator i = drivers.iterator(); i.hasNext(); ) {
          Driver d = (Driver) i.next();
          if(d.accept(url)) {
            db = d.connect(url);
            if(db == null) {
              throw new NullPointerException("Driver returned null when connecting to URL " + url);
            }
            databases.put(url, db);
            break LOAD_DRIVER;
          }
        }
        throw new NullPointerException("Couldn't find driver for URL " + url);
      }
      return db;
    }

    /**
     * Get an entity from an ACeDB database.
     */

    public static AceSet fetch(AceURL url) throws AceException {
      Database db = getDatabase(url);
      if(db == null) {
        throw new NullPointerException("getDatabase(" + url + ") returned null");
      }
      return db.fetch(url);
    }

    public static Connection getConnection(AceURL url) throws AceException {
	Database db = getDatabase(url);
	return db.getConnection();
    }
    
    /**
     * Get the root URL for a database.
     */

    public static AceURL rootURL(AceURL url) {
	String protocol = url.getProtocol();
	String userInfo = url.getUserInfo();
	String authority = url.getAuthority();
	String host = url.getHost();
	int port = url.getPort();
	return new AceURL(protocol, host, port, null, null, null, userInfo, authority);
    }
    
    public static String encode(String s) {
      s = URLEncoder.encode(s);
      if(s.indexOf("%") != -1 || s.indexOf("-") != -1) {
        StringBuffer sb = new StringBuffer(s);
        for(int i = sb.length()-1; i >= 0; i--) {
          if(sb.charAt(i) == '%') {
            sb.setCharAt(i, '-');
          } else if(sb.charAt(i) == '-') {
            sb.insert(i, '-');
          }
        }
        s = sb.toString();
      }
      return s;
    }
    
    public static String decode(String s) throws AceException {
//      System.out.println("decoding '" + s + "'");
      if(s.indexOf("-") != -1) {
        StringBuffer sb = new StringBuffer(s);
        for(int i = 0; i < sb.length(); i++) {
          if(sb.charAt(i) == '-') {
            if(sb.charAt(i+1) == '-') {
              sb.delete(i, i+1);
            } else {
              sb.setCharAt(i, '%');
            }
          }
        }
        s = sb.toString();
      }
      try {
        s = URLDecoder.decode(s);
      } catch (Exception e) {
        throw new AceException(e, "Couldn't decode " + s);
      }
//      System.out.println("decoded as '" + s + "'");
      return s;
    }
}

