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
import java.lang.reflect.*;
import java.io.*;

import org.biojava.utils.*;
import org.biojava.bio.*;

/**
 * The set of AtomicSymbols which can be concatanated together to make a
 * SymbolList.
 * <P>
 * A non-atomic symbol is considered to be contained within this alphabet if
 * all of the atomic symbols that it could match are members of this alphabet.
 * <P>
 * The alphabet concept may need to be widened to include alphabets that extend
 * others, or checks to see if two alphabets are equivalent, or other set-wise
 * operations. As yet, I have baulked at this as it may make Alphabet too heavy
 * to easily implement.
 *
 * @author Matthew Pocock
 */
public interface Alphabet extends Annotatable {
  /** 
   * This ChangeType indicates that some symbols have been added or removed from
   * the alphabet. The churrent and previous fields should indicate what symbols
   * were there originaly, and what they have been replaced with.
   * <P>
   * If the alphabet wishes to propogate that the sybmol has changed state, then
   * previous and current should be null, but the chainedEvent property should
   * rever to the ChangeEvent on the unerlying Symbol.
   */
  public static ChangeType SYMBOLS = new ChangeType(
    "The set of symbols in this alphabet has changed.",
    "org.biojava.bio.symbol.Alphabet",
    "SYMBOLS"
  );
  
  /**
   * This signals that the available parsers have changed. If a parser is added,
   * it will appear in getChanged(). If it is removed, it will appear in
   * getPrevious().
   */
  public static ChangeType PARSERS = new ChangeType(
    "The set of parsers has changed.",
    "org.biojava.bio.symbol.Alphabet",
    "PARSERS"
  );
  
  /**
   * Get the name of the alphabet.
   *
   * @return  the name as a string.
   */
  String getName();

  /**
   * Returns whether or not this Alphabet contains the symbol.
   * <P>
   * An alphabet contains an ambiguity symbol iff the ambiguity symbol's
   * getMemberAlphabet() returns an alphabet that is a proper sub-set of this
   * alphabet. That means that every one of the symbols that could mach the
   * ambiguity symbol is also a member of this alphabet.
   *
   * @param s the Symbol to check
   * @return  boolean true if the Alphabet contains the symbol and false otherwise
   */
  boolean contains(Symbol s);

  /**
   * Throws a precanned IllegalSymbolException if the symbol is not contained
   * within this Alphabet.
   * <P>
   * This function is used all over the code to validate symbols as they enter
   * a method. Also, the code is littered with catches for
   * IllegalSymbolException. There is a preferred style of handling this,
   * which should be covererd in the package documentation.
   *
   * @param s the Symbol to validate
   * @throws  IllegalSymbolException if r is not contained in this alphabet
   */
  void validate(Symbol s) throws IllegalSymbolException;
  
  /**
   * Get a parser by name.
   * <P>
   * The parser returned is guaranteed to return Symbols and SymbolLists that
   * conform to this alphabet.
   * <P>
   * Every alphabet should have a SymbolParser under the name 'token' that
   * uses the symbol token characters to translate a string into a
   * SymbolList. Likewise, there should be a SymbolParser under the name
   * 'name' that uses symbol names to identify symbols. Any other names may
   * also be defined, but the behaviour of that parser is not defined here.
   *
   * @param name  the name of the parser
   * @return  a parser for that name
   * @throws NoSuchElementException if the name is unknown
   * @throws BioException if for any reason the parser could not be built
   */
  SymbolParser getParser(String name)
  throws NoSuchElementException, BioException;
  
  /**
   * A really useful static alphabet that is always empty.
   */
  static final Alphabet EMPTY_ALPHABET = new EmptyAlphabet();
  
  /**
   * The class that implements Alphabet and is empty.
   */
  public class EmptyAlphabet implements FiniteAlphabet, Serializable {
    public String getName() {
      return "Empty Alphabet";
    }
    
    public Annotation getAnnotation() {
      return Annotation.EMPTY_ANNOTATION;
    }
    
    public boolean contains(Symbol s) {
      return s == AlphabetManager.getGapSymbol();
    }
    
    public void validate(Symbol sym) throws IllegalSymbolException {
      throw new IllegalSymbolException(
        "The empty alphabet does not contain symbol " + sym.getName());
    }
    
    public SymbolParser getParser(String name) throws NoSuchElementException {
      throw new NoSuchElementException("There is no parser for the empty alphabet. Attempted to retrieve " + name);
    }

    public int size() {
      return 0;
    }
    
    public Iterator iterator() {
      return SymbolList.EMPTY_LIST.iterator();
    }
    
    public SymbolList symbols() {
      return SymbolList.EMPTY_LIST;
    }
  
    public void addSymbol(Symbol sym) throws IllegalSymbolException {
      throw new IllegalSymbolException(
        "Can't add symbols to alphabet: " + sym.getName() +
        " in " + getName()
      );
    }
  
    public void removeSymbol(Symbol sym) throws IllegalSymbolException {
      throw new IllegalSymbolException(
        "Can't remove symbols from alphabet: " + sym.getName() +
        " in " + getName()
      );
    }
    
    public void addChangeListener(ChangeListener cl) {}
    public void addChangeListener(ChangeListener cl, ChangeType ct) {}
    public void removeChangeListener(ChangeListener cl) {}
    public void removeChangeListener(ChangeListener cl, ChangeType ct) {}

    private Object writeReplace() throws ObjectStreamException {
      try {
        return new StaticMemberPlaceHolder(Alphabet.class.getField("EMPTY_ALPHABET"));
      } catch (NoSuchFieldException nsfe) {
        throw new NotSerializableException(nsfe.getMessage());
      }
    }
  }
}
