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

package org.biojava.bio;

import java.util.*;
import java.io.Serializable;

import org.biojava.utils.*;

public abstract class AbstractAnnotation
  implements
    Annotation,
    Serializable
{
  /**
   * The object to do the hard work of informing others of changes.
   */
  protected transient ChangeSupport changeSupport = null;

  /**
   * Implement this to return the Map delegate.
   *
   * From code in the 1.2 version of AbstractAnnotation
   *
   * @author Matthew Pocock
   */
  protected abstract Map getProperties();
  
  /**
   * A convenience method to see if we have allocated the properties map
   *
   * @return true if the properties have been allocated, false otherwise
   */
  protected abstract boolean propertiesAllocated();
  

  /**
   * @param key The key whose property to retrieve.
   * @throws NoSuchElementException if the property 'key' does not exist
   */
  public Object getProperty(Object key) throws NoSuchElementException {
    if(propertiesAllocated()) {
      Map prop = getProperties();
      if(prop.containsKey(key)) {
        return prop.get(key);
      }
    }
    throw new NoSuchElementException("Property " + key + " unknown");
  }

  public void setProperty(Object key, Object value)
  throws ChangeVetoException {
    if(changeSupport == null) {
      getProperties().put(key, value);
    } else {
      Map properties = getProperties();
      ChangeEvent ce = new ChangeEvent(
        this,
        Annotation.PROPERTY,
        new Object[] { key, value },
        new Object[] { key, properties.get(key)}
      );
      synchronized(changeSupport) {
        changeSupport.firePreChangeEvent(ce);
        properties.put(key, value);
        changeSupport.firePostChangeEvent(ce);
      }
    }
  }

  public boolean containsProperty(Object key) {  
    if(propertiesAllocated()) {
      return getProperties().containsKey(key);
    } else {
      return false;
    }
  }
  
  public Set keys() {
    if(propertiesAllocated()) {
      return getProperties().keySet();
    } else {
      return Collections.EMPTY_SET;
    }
  }

  public String toString() {
    StringBuffer sb = new StringBuffer("{");
    Map prop = getProperties();
    Iterator i = prop.keySet().iterator();
    if(i.hasNext()) {
      Object key = i.next();
      sb.append(key + "=" + prop.get(key));
    }
    while(i.hasNext()) {
      Object key = i.next();
      sb.append("," + key + "=" + prop.get(key));
    }
    sb.append("}");
    return sb.toString();
  }

  public Map asMap() {
    return Collections.unmodifiableMap(getProperties());
  }

  public void addChangeListener(ChangeListener cl) {
    if(changeSupport == null) {
      changeSupport = new ChangeSupport();
    }

    synchronized(changeSupport) {
      changeSupport.addChangeListener(cl);
    }
  }

  public void addChangeListener(ChangeListener cl, ChangeType ct) {
    if(changeSupport == null) {
      changeSupport = new ChangeSupport();
    }

    synchronized(changeSupport) {
      changeSupport.addChangeListener(cl, ct);
    }
  }

  public void removeChangeListener(ChangeListener cl) {
    if(changeSupport != null) {
      synchronized(changeSupport) {
        changeSupport.removeChangeListener(cl);
      }
    }
  }

  public void removeChangeListener(ChangeListener cl, ChangeType ct) {
    if(changeSupport != null) {
      synchronized(changeSupport) {
        changeSupport.removeChangeListener(cl, ct);
      }
    }
  }

  protected AbstractAnnotation() {
  }

  protected AbstractAnnotation(Annotation ann) throws IllegalArgumentException {
    if(ann == null) {
      throw new IllegalArgumentException(
        "Null annotation not allowed. Use Annotation.EMPTY_ANNOTATION instead."
      );
    }
    if(ann == Annotation.EMPTY_ANNOTATION) {
      return;
    }
    Map properties = getProperties();
    for(Iterator i = ann.keys().iterator(); i.hasNext(); ) {
      Object key = i.next();
      try {
        properties.put(key, ann.getProperty(key));
      } catch (IllegalArgumentException iae) {
        throw new BioError(
          iae,
          "Property was there and then disappeared: " + key
        );
      }
    }
  }

  public AbstractAnnotation(Map annMap) {
    if(annMap == null) {
      throw new IllegalArgumentException(
        "Null annotation Map not allowed. Use an empy map instead."
      );
    }
    if(annMap.isEmpty()) {
      return;
    }

    Map properties = getProperties();
    for(Iterator i = annMap.keySet().iterator(); i.hasNext(); ) {
      Object key = i.next();
      properties.put(key, annMap.get(key));
    }
  }

    
  public int hashCode() {
    return asMap().hashCode();
  }

  public boolean equals(Object o) {
    if (! (o instanceof Annotation)) {
      return false;
    }
    
    return ((Annotation) o).asMap().equals(asMap());
  }
}
