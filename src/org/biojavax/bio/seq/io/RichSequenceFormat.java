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

package org.biojavax.bio.seq.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import org.biojava.bio.BioException;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.io.SequenceFormat;
import org.biojava.bio.seq.io.SymbolTokenization;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.ontology.ComparableTerm;

/**
 * Allows a file format to be read/written as RichSequences.
 * @author Richard Holland
 */
public interface RichSequenceFormat extends SequenceFormat {
    /**
     * Sets the stream to write to.
     * @param os the PrintStream to write to.
     * @throws IOException if writing fails.
     */
    public void setPrintStream(PrintStream os);
    
    /**
     * Gets the print stream currently being written to.
     * @return the current print stream.
     */
    public PrintStream getPrintStream();
    
    /**
     * Informs the writer that we want to start writing. This will do any initialisation
     * required, such as writing the opening tags of an XML file that groups sequences together.
     * @throws IOException if writing fails.
     */
    public void beginWriting() throws IOException;
    
    /**
     * Informs the writer that are done writing. This will do any finalisation
     * required, such as writing the closing tags of an XML file that groups sequences together.
     * @throws IOException if writing fails.
     */
    public void finishWriting() throws IOException;
    
    /**
     * Reads a sequence from the given buffered reader using the given tokenizer to parse
     * sequence symbols. Events are passed to the listener, and the namespace used
     * for sequences read is the one given. If the namespace is null, then the default
     * namespace for the parser is used, which may depend on individual implementations
     * of this interface.
     * @param reader the input source
     * @param symParser the tokenizer which understands the sequence being read
     * @param listener the listener to send sequence events to
     * @param ns the namespace to read sequences into.
     * @return true if there is more to read after this, false otherwise.
     * @throws BioException in case of parsing errors.
     * @throws IllegalSymbolException if the tokenizer couldn't understand one of the
     * sequence symbols in the file.
     * @throws IOException if there was a read error.
     */
    public boolean readRichSequence(BufferedReader reader, SymbolTokenization symParser,
            RichSeqIOListener listener,Namespace ns) throws BioException, IllegalSymbolException, IOException;
    
    /**
     * Writes a sequence out to the outputstream given by beginWriting() using the default format of the
     * implementing class. If namespace is given, sequences will be written with that
     * namespace, otherwise they will be written with the default namespace of the
     * implementing class (which is usually the namespace of the sequence itself).
     * If you pass this method a sequence which is not a RichSequence, it will attempt to
     * convert it using RichSequence.Tools.enrich(). Obviously this is not going to guarantee
     * a perfect conversion, so it's better if you just use RichSequences to start with!
     * @param seq the sequence to write
     * @param ns the namespace to write it with
     * @throws IOException in case it couldn't write something
     */
    public void writeSequence(Sequence seq, Namespace ns) throws IOException;
    
    /**
     * Retrive the current line width. Defaults to 80.
     * @return the line width
     */
    public int getLineWidth();
    
    /**
     * Set the line width. When writing, the lines of sequence will never be longer than the line
     * width. Defaults to 80.
     * @param width the new line width
     */
    public void setLineWidth(int width);
    
    /**
     * Use this method to toggle reading of sequence data. 
     * @param elideSymbols set to true if you <em>don't</em> want the sequence data.
     */
    public void setElideSymbols(boolean elideSymbols);
    
    /**
     * Is the format going to emit events when sequence data is read?
     * @return true if it is <em>not</em> otherwise false (false is default) .
     */
    public boolean getElideSymbols();
        
    /**
     * Use this method to toggle reading of feature data. 
     * @param elideFeatures set to true if you <em>don't</em> want the feature data.
     */
    public void setElideFeatures(boolean elideFeatures);
    
    /**
     * Is the format going to emit events when feature data is read?
     * @return true if it is <em>not</em> otherwise false (false is default).
     */
    public boolean getElideFeatures();
    
    /**
     * Use this method to toggle reading of bibliographic reference data. 
     * @param elideReferences set to true if you <em>don't</em> want the bibliographic reference data.
     */
    public void setElideReferences(boolean elideReferences);
    
    /**
     * Is the format going to emit events when bibliographic reference data is read?
     * @return true if it is <em>not</em> otherwise false (false is default) .
     */
    public boolean getElideReferences();
    
    /**
     * Use this method to toggle reading of comments data. Will also ignore remarks
     * lines in bibliographic references.
     * @param elideComments set to true if you <em>don't</em> want the comments data.
     */
    public void setElideComments(boolean elideComments);
    
    /**
     * Is the format going to emit events when comments data or remarks from 
     * bibliographic references are read?
     * @return true if it is <em>not</em> otherwise false (false is default).
     */
    public boolean getElideComments();
        
    /**
     * Stores a number of useful terms used across many sequence formats for consistency's sake.
     */
    public static class Terms {
        private static ComparableTerm SEC_ACCESSION_TERM = null;
        private static ComparableTerm KEYWORDS_TERM = null;
        private static ComparableTerm DATE_CREATED_TERM = null;
        private static ComparableTerm DATE_UPDATED_TERM = null;
        private static ComparableTerm DATE_ANNOTATED_TERM = null;
        private static ComparableTerm REL_CREATED_TERM = null;
        private static ComparableTerm REL_UPDATED_TERM = null;
        private static ComparableTerm REL_ANNOTATED_TERM = null;
        private static ComparableTerm MOLTYPE_TERM = null;
        private static ComparableTerm STRANDED_TERM = null;
        
        /**
         * Holds a reference to the key that must be used to store PubMed references.
         */
        public static final String PUBMED_KEY = "PUBMED";
        
        /**
         * Holds a reference to the key that must be used to store Medline references.
         */
        public static final String MEDLINE_KEY = "MEDLINE";
        
        /**
         * Holds a reference to the key that must be used to store DOI references.
         */
        public static final String DOI_KEY = "DOI";
        
        /**
         * Getter for the secondary/tertiary/additional accession term
         * @return A Term that represents the secondary accession tag
         */
        public static ComparableTerm getAdditionalAccessionTerm() {
            if (SEC_ACCESSION_TERM==null) SEC_ACCESSION_TERM = RichObjectFactory.getDefaultOntology().getOrCreateTerm("acc");
            return SEC_ACCESSION_TERM;
        }
        
        /**
         * Getter for the keyword term
         * @return a Term that represents the Keyword tag
         */
        public static ComparableTerm getKeywordTerm() {
            if (KEYWORDS_TERM==null) KEYWORDS_TERM = RichObjectFactory.getDefaultOntology().getOrCreateTerm("kw");
            return KEYWORDS_TERM;
        }
        
        /**
         * Getter for the date created term
         * @return a Term
         */
        public static ComparableTerm getDateCreatedTerm() {
            if (DATE_CREATED_TERM==null) DATE_CREATED_TERM = RichObjectFactory.getDefaultOntology().getOrCreateTerm("cdat");
            return DATE_CREATED_TERM;
        }
        
        /**
         * Getter for the date updated term
         * @return a Term
         */
        public static ComparableTerm getDateUpdatedTerm() {
            if (DATE_UPDATED_TERM==null) DATE_UPDATED_TERM = RichObjectFactory.getDefaultOntology().getOrCreateTerm("udat");
            return DATE_UPDATED_TERM;
        }
        
        /**
         * Getter for the date annotated term
         * @return a Term
         */
        public static ComparableTerm getDateAnnotatedTerm() {
            if (DATE_ANNOTATED_TERM==null) DATE_ANNOTATED_TERM = RichObjectFactory.getDefaultOntology().getOrCreateTerm("adat");
            return DATE_ANNOTATED_TERM;
        }
        
        /**
         * Getter for the release created term
         * @return a Term
         */
        public static ComparableTerm getRelCreatedTerm() {
            if (REL_CREATED_TERM==null) REL_CREATED_TERM = RichObjectFactory.getDefaultOntology().getOrCreateTerm("crel");
            return REL_CREATED_TERM;
        }
        
        /**
         * Getter for the release updated term
         * @return a Term
         */
        public static ComparableTerm getRelUpdatedTerm() {
            if (REL_UPDATED_TERM==null) REL_UPDATED_TERM = RichObjectFactory.getDefaultOntology().getOrCreateTerm("urel");
            return REL_UPDATED_TERM;
        }
        
        /**
         * Getter for the release annotated term
         * @return a Term
         */
        public static ComparableTerm getRelAnnotatedTerm() {
            if (REL_ANNOTATED_TERM==null) REL_ANNOTATED_TERM = RichObjectFactory.getDefaultOntology().getOrCreateTerm("arel");
            return REL_ANNOTATED_TERM;
        }
        
        /**
         * getter for the MolType term
         * @return a Term that represents the molecule type
         */
        public static ComparableTerm getMolTypeTerm() {
            if (MOLTYPE_TERM==null) MOLTYPE_TERM = RichObjectFactory.getDefaultOntology().getOrCreateTerm("moltype");
            return MOLTYPE_TERM;
        }
        
        /**
         * Getter for the Strand term
         * @return a Term that represents the Strand tag
         */
        public static ComparableTerm getStrandedTerm() {
            if (STRANDED_TERM==null) STRANDED_TERM = RichObjectFactory.getDefaultOntology().getOrCreateTerm("stranded");
            return STRANDED_TERM;
        }
    }
    
    /**
     * Provides a basic format with simple things like line-widths precoded.
     */
    public abstract class BasicFormat implements RichSequenceFormat  {
        
        private int lineWidth = 80;
        private boolean elideSymbols = false;
        private boolean elideFeatures = false;
        private boolean elideComments = false;
        private boolean elideReferences = false;
        private PrintStream os;
        
        /**
         * {@inheritDoc}
         */
        public int getLineWidth() { return this.lineWidth; }
        
        /**
         * {@inheritDoc}
         */
        public void setLineWidth(int width) {
            if (width<1) throw new IllegalArgumentException("Width cannot be less than 1");
            this.lineWidth = width;
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean getElideSymbols() { return this.elideSymbols; }
        
        /**
         * {@inheritDoc}
         */
        public void setElideSymbols(boolean elideSymbols) { this.elideSymbols = elideSymbols; }
        
        /**
         * {@inheritDoc}
         */
        public boolean getElideFeatures() { return this.elideFeatures; }
        
        /**
         * {@inheritDoc}
         */
        public void setElideFeatures(boolean elideFeatures) { this.elideFeatures = elideFeatures; }
        
        /**
         * {@inheritDoc}
         */
        public boolean getElideReferences() { return this.elideReferences; }
        
        /**
         * {@inheritDoc}
         */
        public void setElideReferences(boolean elideReferences) { this.elideReferences = elideReferences; }
        
        /**
         * {@inheritDoc}
         */
        public boolean getElideComments() { return this.elideComments; }
        
        /**
         * {@inheritDoc}
         */
        public void setElideComments(boolean elideComments) { this.elideComments = elideComments; }
        
        /**
         * {@inheritDoc}
         */
        public void setPrintStream(PrintStream os) {
            if (os==null) throw new IllegalArgumentException("Print stream cannot be null");
            this.os = os;
        }
        
        /**
         * {@inheritDoc}
         */
        public PrintStream getPrintStream() { return this.os; }
    }
    
       /**
        * Provides the basic implementation required for simple header/footer-less files such as Genbank.
        */
        public abstract class HeaderlessFormat extends BasicFormat {
        /**
         * {@inheritDoc}
         */
        public void beginWriting() throws IOException {}
        
        /**
         * {@inheritDoc}
         */
        public void finishWriting() throws IOException {}
    }
}
