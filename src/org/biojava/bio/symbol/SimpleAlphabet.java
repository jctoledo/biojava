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


package org.biojava.bio.symbol;

import java.util.*;
import java.io.*;

import org.biojava.utils.*;
import org.biojava.bio.*;

/**
 * A simple no-frills implementation of the FiniteAlphabet interface.
 *
 * @serial WARNING the serialized version of this class may not be compatible with future versions
 * @author Matthew Pocock
 */
public class SimpleAlphabet
extends AbstractAlphabet
implements Serializable {
  private String name;
  private Annotation annotation;
  private final Set symbols;
  private final Set ambig;

    //BE SURE TO CHANGE THIS VALUE IF YOU CHANGE THE IMPLEMENTATION
    //SUCH THAT SERIALIZATION WILL FAIL.
  private static final long serialVersionUID = 216254146;
  
  /**
   * A list of alphabets that make up this one - a singleton list containing
   * this.
   */
  private List alphabets;
  
  public Iterator iterator() {
    return symbols.iterator();
  }
  
  public String getName() {
    return name;
  }
  
  /**
   * Assign a name to the alphabet
   * @param name the name you wish to give this alphabet
   */
  public void setName(String name) {
    this.name = name;
  }
    
  public Annotation getAnnotation() {
    if(annotation == null)
      annotation = new SimpleAnnotation();
    return annotation;
  }

  public int size() {
    return symbols.size();
  }
  
  public SymbolList symbols() {
    try {
      return new SimpleSymbolList(this, new ArrayList(symbols));
    } catch (IllegalSymbolException ex) {
      throw new BioError(
        ex,
        "There should be no circumstances under which this failed"
      );
    }
  }

  protected boolean containsImpl(AtomicSymbol s) {
    return symbols.contains(s);
  }

  protected void addSymbolImpl(AtomicSymbol s)
  throws IllegalSymbolException, ChangeVetoException {
    symbols.add(s);
  }
  
  public void removeSymbol(Symbol s)
  throws IllegalSymbolException {
    validate(s);
    if(s instanceof AtomicSymbol) {
      symbols.remove(s);
    } else {
      FiniteAlphabet sa = (FiniteAlphabet) s.getMatches();
      Iterator i = ((FiniteAlphabet) sa).iterator();
      while(i.hasNext()) {
        Symbol sym = (Symbol) i.next();
        symbols.remove(sym);
      }
    }
  }

  public List getAlphabets() {
    if(this.alphabets == null) {
      this.alphabets = new SingletonList(this);
    }
    return this.alphabets;
  }
  
  protected AtomicSymbol getSymbolImpl(List symL)
  throws IllegalSymbolException {
    AtomicSymbol s = (AtomicSymbol) symL.get(0);
    return s;
  }
  
  public SimpleAlphabet() {
    this(new HashSet(), null);
  }
  
  public SimpleAlphabet(Set symbols) {
    this(symbols, null);
  }
  
  public SimpleAlphabet(String name) {
    this(new HashSet(), name);
  }

  public SimpleAlphabet(Set symbols, String name) {
    this.symbols = new HashSet();
    this.ambig = new HashSet();
    this.name = name;
    this.alphabets = null;
    
    // this costs, but I am tracking down a ClassCast exception.
    // roll on parameterised types.
    for(Iterator i = symbols.iterator(); i.hasNext(); ) {
      AtomicSymbol a = (AtomicSymbol) i.next();
      this.symbols.add(a);
    }
  }
}
