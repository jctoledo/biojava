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

package org.biojava.bio.seq.io;

/**
 * <code>SeqIOConstants</code> contains constants used to identify
 * sequence formats, alphabets etc, in the context of reading and
 * writing sequences.
 *
 * <p>An <code>int</code> used to specify symbol alphabet and
 * sequence format type is derived thus:</p>
 *
 * <ul>
 *   <li>
 *    The two least significant bytes are reserved for format types
 *    such as RAW, FASTA, EMBL etc.
 *   </li>
 *
 *   <li>
 *    The two most significant bytes are reserved for alphabet and
 *    symbol information such as AMBIGUOUS, DNA, RNA, AA etc.
 *   </li>
 *
 *   <li>
 *    Bitwise OR combinations of each component <code>int</code> are used
 *    to specify combinations of format type and symbol information. To
 *    derive an <code>int</code> identifier for DNA with ambiguity codes
 *    in Fasta format, bitwise OR the AMBIGUOUS, DNA and FASTA values.
 *   </li>
 * </ul>
 *
 * @author Keith James
 */
public final class SeqIOConstants
{
    /**
     * <code>AMBIGUOUS</code> indicates that a sequence contains
     * ambiguity symbols. The first bit of the most significant word
     * of the int is set.
     */
    public static final int AMBIGUOUS = 1 << 16;

    /**
     * <code>DNA</code> indicates that a sequence contains DNA
     * (deoxyribonucleic acid) symbols. The second bit of the most
     * significant word of the int is set.
     */
    public static final int DNA = 1 << 17;

    /**
     * <code>RNA</code> indicates that a sequence contains RNA
     * (ribonucleic acid) symbols. The third bit of the most
     * significant word of the int is set.
     */
    public static final int RNA = 1 << 18;

    /**
     * <code>AA</code> indicates that a sequence contains AA (amino
     * acid) symbols. The fourth bit of the most significant word of
     * the int is set.
     */
    public static final int AA = 1 << 19;

    /**
     * <code>INTEGER</code> indicates that a sequence contains integer
     * alphabet symbols, such as used to describe sequence quality
     * data. The fifth bit of the most significant word of the int is
     * set.
     */
    public static final int INTEGER = 1 << 20;

    /**
     * <code>UNKNOWN</code> indicates that the sequence format is
     * unknown.
     */
    public static final int UNKNOWN = 0;

    /**
     * <code>RAW</code> indicates that the sequence format is raw
     * (symbols only).
     */
    public static final int RAW = 1;

    /**
     * <code>FASTA</code> indicates that the sequence format is Fasta.
     */
    public static final int FASTA = 2;

    /**
     * <code>NBRF</code> indicates that the sequence format is NBRF.
     */
    public static final int NBRF = 3;

    /**
     * <code>IG</code> indicates that the sequence format is IG.
     */
    public static final int IG = 4;

    /**
     * <code>EMBL</code> indicates that the sequence format is EMBL.
     */
    public static final int EMBL = 10;

    /**
     * <code>SWISSPROT</code> indicates that the sequence format is
     * SWISSPROT. Always protein, so already had the AA bit set.
     */
    public static final int SWISSPROT = 11 | AA;

    /**
     * <code>GENBANK</code> indicates that the sequence format is
     * GENBANK.
     */
    public static final int GENBANK = 12;

    /**
     * <code>GENPEPT</code> indicates that the sequence format is
     * GENPEPT. Always protein, so already had the AA bit set.
     */
    public static final int GENPEPT = 13 | AA;

    /**
     * <code>REFSEQ</code> indicates that the sequence format is
     * REFSEQ.
     */
    public static final int REFSEQ = 14;

    /**
     * <code>GCG</code> indicates that the sequence format is GCG.
     */
    public static final int GCG = 15;

    /**
     * <code>GFF</code> indicates that the sequence format is GFF.
     */
    public static final int GFF = 20;

    /**
     * <code>PDB</code> indicates that the sequence format is
     * PDB. Always protein, so already had the AA bit set.
     */
    public static final int PDB = 21 | AA;

    /**
     * <code>PHRED</code> indicates that the sequence format is
     * PHRED. Always DNA, so already had the DNA bit set. Also has
     * INTEGER bit set for quality data.
     */
    public static final int PHRED = 30 | DNA | INTEGER;

    public static final int    EMBL_DNA = EMBL    | DNA;
    public static final int    EMBL_RNA = EMBL    | RNA;
    public static final int     EMBL_AA = EMBL    | AA;
    public static final int GENBANK_DNA = GENBANK | DNA;
    public static final int GENBANK_RNA = GENBANK | RNA;
    public static final int  GENBANK_AA = GENBANK | AA;
    public static final int  REFSEQ_DNA = REFSEQ  | DNA;
    public static final int  REFSEQ_RNA = REFSEQ  | RNA;
    public static final int   REFSEQ_AA = REFSEQ  | AA;
    public static final int   FASTA_DNA = FASTA   | DNA;
    public static final int   FASTA_RNA = FASTA   | RNA;
    public static final int    FASTA_AA = FASTA   | AA;
}
