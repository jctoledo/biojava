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


package org.biojava.bio.dp;

import java.util.*;
import java.io.Serializable;

import org.biojava.bio.symbol.*;
import org.biojava.bio.dist.*;

/**
 * @author Matthew Pocock
 */
public class SimpleWeightMatrix implements WeightMatrix, Serializable {
  private final Distribution [] columns;
  private final Alphabet alpha;

  public Alphabet getAlphabet() {
    return alpha;
  }

  public int columns() {
    return this.columns.length;
  }
  
  public Distribution getColumn(int column) {
    return columns[column];
  }

  public SimpleWeightMatrix(Alphabet alpha, int columns, DistributionFactory dFact)
  throws IllegalAlphabetException {
    this.alpha = alpha;
    this.columns = new Distribution[columns];
    for(int i = 0; i < columns; i++) {
      this.columns[i] = dFact.createDistribution(alpha);
    }
  }
  
  public SimpleWeightMatrix(Distribution[] columns)
  throws IllegalAlphabetException {
    this.alpha = columns[0].getAlphabet();
    for(int c = 0; c < columns.length; c++) {
      if(columns[c].getAlphabet() != alpha) {
        throw new IllegalAlphabetException(
          "All columns must emit the same alphabet. Expecting " +
          alpha.getName() + ", but found " + columns[c].getAlphabet().getName()
        );
      }
    }
    this.columns = columns;
  }
}
