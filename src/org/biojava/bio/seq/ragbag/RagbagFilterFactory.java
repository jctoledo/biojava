/**
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
 
package org.biojava.bio.seq.ragbag;

import org.biojava.bio.seq.Feature;
//import org.biojava.bio.seq.io.ParseException;
import org.biojava.bio.seq.io.SequenceBuilder;
//import org.biojava.bio.seq.io.SequenceBuilderFilter;
//import org.biojava.bio.seq.io.SeqIOListener;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * @author David Huen
 */
public interface RagbagFilterFactory
{
/**
 * returns a SequenceBuilder object wrapped with an
 * a filter.
 */
  public SequenceBuilder wrap(SequenceBuilder delegate);
}


