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
 
package org.biojava.bio.seq;

/**
 * Adds the concept of 'strand' to features.
 * <P>
 * Strandedness only applies to some types of sequence, such as DNA. Any
 * implementation should blow chunks to avoid being added to a sequence for
 * which strand is a foreighn concept.
 *
 * @author Matthew Pocock
 */
public interface StrandedFeature extends Feature {
  /**
   * Retrieve the strand that this feature lies upon.
   * <P>
   * This will be one of StrandedFeature.POSITIVE or NEGATIVE.
   *
   * @return an integer flag to indicate strandedness
   */
  int getStrand();
  
  /**
   * Return a list of residues that are contained in this feature.
   * <P>
   * The residues may not be contiguous in the original sequence, but they
   * will be concatinated together in the resulting ResidueList.
   * <P>
   * The order of the Residues within the resulting residue list will be 
   * according to the concept of ordering within the location object.
   * <P>
   * If the feature is on the negative strand then the ResidueList will be
   * reversecomplemented as apropreate.
   *
   * @return  a ResidueList containing each residue of the parent sequence contained
   *          within this feature in the order they appear in the parent
   */
  ResidueList getResidues();
  
  /**
   * int flag to indicate that a feature is on the positive strand.
   */
  static final int POSITIVE = +1;

  /**
   * int flag to indicate that a feature is on the negative strand.
   */
  static final int NEGATIVE = -1;
}
