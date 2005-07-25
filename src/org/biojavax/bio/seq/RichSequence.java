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

package org.biojavax.bio.seq;

import org.biojava.bio.seq.Sequence;
import org.biojava.utils.ChangeVetoException;
import org.biojavax.bio.BioEntry;
import org.biojavax.bio.seq.RichSequenceFeatureHolder;

/**
 * A rich sequence is a combination of a <code>org.biojavax.bio.Bioentry</code>
 * and a <code>org.biojava.seq.Sequence</code>. It inherits and merges the methods
 * of both. The <code>RichSequence</code> is based on the BioSQL model and
 * provides a richer array of methods to access information than <code>Sequence</code>
 * does. The interface introduces no new methods of it's own. It is essentially
 * a <code>BioEntry</code> with sequence information.
 * <p>
 * Whenever possible <code>RichSequence</code> should be used in preference to
 * <code>Sequence</code>
 * @author Mark Schreiber
 */
public interface RichSequence extends BioEntry,Sequence,RichSequenceFeatureHolder {
    
    
    /**

     * The version of the associated symbol list.

     * @return  the version

     */

    public double getSeqVersion();

    

    /**
     * Sets the version of the associated symbol list.
     * @param seqVersion the version to set.
     * @throws ChangeVetoException if it doesn't want to change.
     */

    public void setSeqVersion(double seqVersion) throws ChangeVetoException;
}
