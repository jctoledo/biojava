// CandyVocabulary.java
//
//    senger@ebi.ac.uk
//    February 2001
//

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
package org.biojava.utils.candy;

import org.biojava.utils.*;
import java.util.*;
import java.beans.*;

/**
 * This interface defines functionality of a controlled vocabulary.
 * The implementation is supposed to behave as a Java bean
 * (regarding accessing vocabulary properties).
 *
 *<P>
 * Each vocabulary consists of (usually many) vocabulary entries
 * which are represented by {@link CandyEntry CandyEntries}.
 *
 *<P> 
 * @author <A HREF="mailto:senger@ebi.ac.uk">Martin Senger</A>
 * @version $Id$
 */

public interface CandyVocabulary
    extends PropertyChangeListener {

    /*************************************************************************
     * It checks if a given entry exists in this vocabulary.
     * <P>
     * @param name of a vocabulary entry to be checked
     * @return true if the given entry exists in this vocabulary
     * @throws NestedException if the vocabulary is suddenly not available
     *************************************************************************/
    boolean contains (String name)
	throws NestedException;

    /*************************************************************************
     * It returns a selected vocabulary entry.
     * <P>
     * @see #getAllEntries getAllEntries
     * @param name a name of a vocabulary entry to be looked up
     * @return a vocabulary entry
     * @throws NestedException when the given vocabulary entry does not exist
     *************************************************************************/
    CandyEntry getEntryByName (String name)
	throws NestedException;

    /*************************************************************************
     * It returns all available vocabulary entries.
     * <P>
     * @see #getEntryByName getEntryByName
     * @return an Enumeration object containing all available entries
     * @throws NestedException if the vocabulary is suddenly not available
     *************************************************************************/
    Enumeration getAllEntries()
	throws NestedException;

    /*************************************************************************
     * It return all names (entry identifiers) available in this vocabulary.
     * <P>
     * @return an Enumeration object containing all available names
     * @throws NestedException if the vocabulary is suddenly not available
     *************************************************************************/
    Enumeration getAllNames()
	throws NestedException;

    /*************************************************************************
     * It frees all resources related to this vocabulary.
     * <P>
     * @throws NestedException if the vocabulary is suddenly not available
     *************************************************************************/
    void destroy()
	throws NestedException;

    /*************************************************************************
     * 
     *    P r o p e r t i e s
     *
     *************************************************************************/

    //
    // Property names
    //

    /** A property name. Its value is a name of this vocabulary. */
    static final String PROP_VOCAB_NAME     = "vocab_name";

    /** A property name. Its value is a short description of the whole vocabulary. */
    static final String PROP_VOCAB_DESC     = "vocab_description";

    /** A property name. Its value contains a version of this vocabulary. */
    static final String PROP_VOCAB_VERSION  = "vocab_version";

    /** A property name. Its boolean value is <tt>true</tt>
     *  if the vocabulary entries names should be considered as case-sensitive.
     */ 
    static final String PROP_CASE_SENSITIVE = "vocab_case_sensitive";

    /** A property name. Its value is a number of vocabulary entries
     *  in this vocabulary.
     */
    static final String PROP_ENTRY_COUNT    = "entry_count";

    /** A property name. Its type is {@link CandyVocabulary} and
     *  it can be used to set an entire vocabulary.
     *  An implementation may use it together with an empty
     *  constructor.
     */
    static final String PROP_VOCABULARY     = "vocabulary";

    //
    // Property access methods
    //

    /** It returns a name of this vocabulary.
     *  The name should be unique within a {@link CandyFinder}
     *  instance who delivers this vocabulary.
     */
    String getName() throws NestedException;

    /** It returns a description of this vocabulary. */
    String getDescription() throws NestedException;

    /** It returns a vesrion of this vocabulary. */
    String getVersion() throws NestedException;

    /** It returns a number of entries contained in this vocabulary. */
    int getCount() throws NestedException;

    /** It returns <tt>true</tt> if the vocabulary entries should
     *  be considered as case-sensitive.
     */
    boolean isCaseSensitive() throws NestedException;

    /** A property name.
     *<P>
     *  An implementation may use this boolean property to make sure that
     *  returned vocabulary entries are in the same order as they were
     *  read from its original source.
     */
    static final String CANDIES_NOT_SORTED = "candies_not_sorted";

}
