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

package org.biojava.bio.program.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.biojava.bio.Annotation;
import org.biojava.bio.BioException;
import org.biojava.bio.SimpleAnnotation;
import org.biojava.bio.search.SeqSimilaritySearchHit;
import org.biojava.bio.search.SeqSimilaritySearchResult;
import org.biojava.bio.search.SeqSimilaritySearchSubHit;
import org.biojava.bio.search.SequenceDBSearchHit;
import org.biojava.bio.search.SequenceDBSearchResult;
import org.biojava.bio.search.SequenceDBSearchSubHit;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.db.SequenceDB;
import org.biojava.bio.seq.db.SequenceDBInstallation;
import org.biojava.bio.seq.io.TokenParser;
import org.biojava.bio.symbol.Alignment;
import org.biojava.bio.symbol.FiniteAlphabet;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SimpleAlignment;
import org.biojava.bio.symbol.SymbolList;
import org.biojava.utils.ChangeVetoException;

/**
 * A <code>FastaSearchBuilder</code> object organises search data
 * parsed from a search stream by a separate parser object. The parser
 * calls methods in the FastaSearchBuilder to coordinate creation of
 * FastaSearchResult objects.
 *
 * @author <a href="mailto:kdj@sanger.ac.uk">Keith James</a>
 * @since 1.1
 * @see SearchBuilder
 */
public class FastaSearchBuilder implements SearchBuilder
{
    private SearchParser            parser;

    private SequenceDBInstallation  subjectDBs;
    private SequenceDB              subjectDB;

    private SequenceDB              querySeqHolder;
    private SymbolList              querySeq;

    private Annotation              hitAnnotation;
    private Annotation              resultAnnotation;

    private Map                     resultPreAnnotation;
    private Map                     searchParameters;
    private Map                     hitData;

    // Hits are appended to this ArrayList as they are parsed from a
    // stream. The Fasta program pre-sorts the hits in order of
    // significance. This implementation currently relies of this to
    // sort its hits i.e. no sorting is performed.
    private ArrayList searchHits = new ArrayList();

    /**
     * Creates a new <code>FastaSearchBuilder</code> object.
     *
     * @param subjectDBs a <code>SequenceDBInstallation</code>
     * instance which must contain a SequenceDB with an identifier
     * which is the same as the filename of the database specified in
     * the search output. The filename of the search database used to
     * resolve a corresponding SequenceDB, provided that the filename
     * is recorded as one of its identifiers.
     * @param querySeqHolder a <code>SequenceDB</code> which should
     * contain one instance of each of the query sequences used in the
     * searches to be parsed. The sequence ID used in the search
     * output should correspond to the ID of a Sequence within this
     * database.
     */
    public FastaSearchBuilder(final SequenceDBInstallation subjectDBs,
			      final SequenceDB             querySeqHolder)
    {
	this.subjectDBs     = subjectDBs;
	this.querySeqHolder = querySeqHolder;
    }

    /**
     * The <code>makeSearchResult</code> method creates a new object
     * representing a the result of a single search.
     *
     * @return a <code>SeqSimilaritySearchResult</code> object.
     *
     * @exception BioException if parsing or instantiation fails
     * internally.
     */
    public SeqSimilaritySearchResult makeSearchResult()
    	throws BioException
    {
	return new SequenceDBSearchResult(subjectDB,
					  searchParameters,
					  querySeq,
					  resultAnnotation,
					  searchHits);
    }

    /**
     * <code>setQuerySeq</code> resolves the query sequence id parsed
     * from the search result to a SymbolList obtained from the
     * querySeqHolder SequenceDB.
     *
     * @param querySeqID a <code>String</code> which should be the
     * sequence ID in the querySeqHolder.
     *
     * @exception BioException if the sequence cannot be obtained.
     */
    public void setQuerySeq(final String querySeqID)
	throws BioException
    {
	try
	{
	    // System.out.println("Setting Query sequence with ID: " + querySeqID);
	    querySeq = (SymbolList) querySeqHolder.getSequence(querySeqID);
	}
	catch (BioException be)
	{
	    throw new BioException(be, "Failed to retrieve query sequence from holder using ID: "
				   + querySeqID);
	}
    }

    /**
     * <code>setSubjectDB</code> resolves the target database filename
     * parsed from the search result to a SequenceDB instance using a
     * SequenceDBInstallation to perform the mapping.
     *
     * @param subjectDBFileName a <code>String</code> which should be
     * an identifier of the corresponding database in the subjectDBs
     * installation.
     *
     * @exception BioException if the database cannot be obtained.
     */
    public void setSubjectDB(final String subjectDBFileName)
	throws BioException
    {
	// System.out.println("Setting subject DB with filename/ID: " + subjectDBFileName);
	subjectDB = subjectDBs.getSequenceDB(subjectDBFileName);

	if (subjectDB == null)
	    throw new BioException("Failed to retrieve database from installation using ID: "
				   + subjectDBFileName);
    }

    public void startSearch() { }

    public void endSearch() { }

    public void startHeader()
    {
	// System.out.println("startHeader called");
	resultPreAnnotation = new HashMap();
	searchParameters    = new HashMap();
    }

    public void addSearchProperty(final Object key, final Object value)
    {
	// System.out.println("addSearchProperty called: " + key + " -> " + value);
	resultPreAnnotation.put(key, value);
    }

    public void endHeader()
    {
	resultAnnotation = createAnnotation(resultPreAnnotation);
    }

    public void startHit()
    {
	// System.out.println("startHit called");
	hitData = new HashMap();
    }

    public void addHitProperty(final Object key, final Object value)
    {
	// System.out.println("addHitProperty called: " + key + " -> " + value);
	hitData.put(key, value);
    }

    public void endHit() { }

    public void startSubHit() { }

    public void addSubHitProperty(final Object key, final Object value)
    {
	// System.out.println("addSubHitProperty called: " + key + " -> " + value);
	hitData.put(key, value);
    }

    public void endSubHit()
    {
	hitAnnotation = createAnnotation(hitData);

	try
	{
	    SeqSimilaritySearchHit hit = createHit(hitData, hitAnnotation);
	    searchHits.add(hit);
	}
	catch (BioException be)
	{
	    be.printStackTrace();
	}
    }

    /**
     * The <code>createHit</code> method makes a new Hit object from
     * the hit data and an annotation.
     *
     * @param dataMap a <code>Map</code> object.
     * @param hitAnnotation an <code>Annotation</code> object.
     *
     * @return a <code>SeqSimilaritySearchHit</code> object.
     */
    private SeqSimilaritySearchHit createHit(final Map        hitData,
					     final Annotation hitAnnotation)
	throws BioException
    {
	String subjectSeqID  = (String) hitData.get("id");
	Double score         = (Double) hitData.get("fa_z-score");
	Double eValue        = (Double) hitData.get("fa_expect");
	Double pValue        = (Double) hitData.get("fa_expect");

	// There is only ever one subhit in a Fasta hit
	List subHits = new ArrayList();

	try
	{
	    // System.out.println("Making alignment with: " + hitData);
	    Alignment alignment = createAlignment(subjectSeqID, hitData);

	    SeqSimilaritySearchSubHit subHit =
		new SequenceDBSearchSubHit(score.doubleValue(),
					   eValue.doubleValue(),
					   pValue.doubleValue(),
					   alignment);

	    subHits.add(subHit);
	}
	catch (IllegalSymbolException ise)
	{
	    throw new BioException("Failed to create alignment for hit to "
				   + subjectSeqID);
	}

	return new SequenceDBSearchHit(subjectSeqID,
				       score.doubleValue(),
				       eValue.doubleValue(),
				       pValue.doubleValue(),
				       subHits,
				       hitAnnotation);
    }

    /**
     * The <code>createAnnotation</code> method makes a new annotation
     * instance from the preannotation Map data.
     *
     * @param preAnnotation a <code>Map</code> object.
     *
     * @return an <code>Annotation</code> object.
     */
    private Annotation createAnnotation(final Map preAnnotation)
    {
	Annotation annotation = new SimpleAnnotation();
	Set  annotationKeySet = preAnnotation.keySet();

	for (Iterator ksi = annotationKeySet.iterator(); ksi.hasNext();)
	{
	    Object   annotationKey = ksi.next();
	    Object annotationValue = preAnnotation.get(annotationKey);

//  	    System.out.println("Setting annotation: key -> "
//  			       + annotationKey
//  			       + " value -> "
//  			       + annotationValue);

	    try
	    {
		annotation.setProperty(annotationKey, annotationValue);
	    }
	    catch (ChangeVetoException cve)
	    {
		cve.printStackTrace();
	    }
	}
	return annotation;
    }

    /**
     * The <code>createAlignment</code> method creates an Alignment
     * object from the sequence Strings parsed from the Fasta output.
     *
     * @param subjectSeqID a <code>String</code> which will be used to
     * set the label of the subject sequence. The query label is a
     * static field in the SeqSimilaritySearchSubHit interface.
     * @param hitData a <code>Map</code> object.
     *
     * @return an <code>Alignment</code> object.
     *
     * @exception IllegalSymbolException if tokens from the sequence
     * are not recognised in the chosen alphabet.
     */
    private Alignment createAlignment(final String subjectSeqID,
				      final Map    hitData)
	throws IllegalSymbolException
    {
	String seqType = (String) hitData.get("query_sq_type");

	FiniteAlphabet alpha;

	// What happens if Fasta is given an RNA sequence?
	if (seqType.equals("DNA"))
	    alpha = DNATools.getDNA();
	else
	    alpha = ProteinTools.getAlphabet();

	StringBuffer querySeqTokens =
	    new StringBuffer(prepSeqTokens("query",   hitData));
	StringBuffer subjectSeqTokens =
	    new StringBuffer(prepSeqTokens("subject", hitData));

	Map labelMap = new HashMap();

	// System.out.println("Passing query: "   + querySeqTokens);
	// System.out.println("Passing subject: " + subjectSeqTokens);

	GappedSymbolListBuilder gslBuilder =
	    new GappedSymbolListBuilder(alpha);

	labelMap.put(SeqSimilaritySearchSubHit.QUERY_LABEL, 
		     gslBuilder.makeGappedSymbolList(querySeqTokens));
	labelMap.put(subjectSeqID,
		     gslBuilder.makeGappedSymbolList(subjectSeqTokens));

	return new SimpleAlignment(labelMap);
    }

    /**
     * The <code>prepSeqTokens</code> method prepares the sequence
     * data extracted from the Fasta output. Two things need to be
     * done; firstly, the leading gaps are removed from the sequence
     * (these are just format padding and not really part of the
     * alignment) and secondly, as Fasta supplies some flanking
     * sequence context for its alignments, this must be removed
     * too. See the Fasta documentation for an explanation of the
     * format.
     *
     * @param name a <code>String</code> value (either "query" or
     * "subject" indicating which sequence to grab from the
     * accompanying Map object.
     * @param hitData a <code>Map</code> object containing the data to
     * process.
     *
     * @return a <code>String</code> value consisting of a subsequence
     * containing only the interesting alignment.
     */
    private String prepSeqTokens(final String name, final Map hitData)
    {
	// System.out.println("hitData: " + hitData);

	Integer alDispStart = (Integer) hitData.get(name + "_al_display_start");
	Integer     alStart = (Integer) hitData.get(name + "_al_start");
	Integer      alStop = (Integer) hitData.get(name + "_al_stop");    

	StringBuffer seqTokens =
	    new StringBuffer((String) hitData.get(name + "SeqTokens"));

	// Strip leading gap characters
	while (seqTokens.charAt(0) == '-')
	    seqTokens.deleteCharAt(0);

	int gapCount = 0;
	// Count gaps to add to number of chars returned
	for (int i = 0; i < seqTokens.length(); i++)
	{
	    if (seqTokens.charAt(i) == '-')
		gapCount++;
	}

	// Calculate the position at which the real alignment
	// starts/stops, allowing for the gaps, which are not counted
	// in the numbering system
	return seqTokens.substring(alStart.intValue() - alDispStart.intValue(),
				   alStop.intValue()  - alDispStart.intValue()
				   + gapCount + 1);
    }
}
