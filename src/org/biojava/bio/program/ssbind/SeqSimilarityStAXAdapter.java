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

package org.biojava.bio.program.ssbind;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.biojava.bio.search.SearchContentHandler;
import org.biojava.utils.stax.DelegationManager;
import org.biojava.utils.stax.StAXContentHandler;
import org.biojava.utils.stax.StAXContentHandlerBase;

public class SeqSimilarityStAXAdapter extends StAXContentHandlerBase
{
    public static final String NAMESPACE = "http://www.biojava.org";

    // Incremented on startElement, decremented on endElement. Used to
    // identify which method calls to handle here and which to
    // delegate.
    private int depth;

    // The target handler
    private SearchContentHandler scHandler;
    // The name of the program which generated the results
    private String program = "unknown";

    public void startElement(String            nsURI,
                             String            localName,
                             String            qName,
                             Attributes        attrs,
                             DelegationManager dm)
        throws SAXException
    {
        depth++;

        if (! nsURI.equals(NAMESPACE))
            throw new SAXException("Failed to handle Element " + localName
                                   + " in namespace " + nsURI);

        if (depth == 1)
        {
            if (localName.equals("BlastLikeDataSetCollection"))
            {
                scHandler.setMoreSearches(true);
            }
        }
        else
        {
            if (localName.equals("BlastLikeDataSet"))
            {
                scHandler.startSearch();
                program = attrs.getValue("program");
                scHandler.addSearchProperty("program", program);
                scHandler.addSearchProperty("version", attrs.getValue("version"));
                return;
            }
            else if (localName.equals("Hit"))
            {
                dm.delegate(HitStAXHandler.HIT_HANDLER_FACTORY.getHandler(this));
                return;
            }
            else if (localName.equals("Header"))
            {
                dm.delegate(HeaderStAXHandler.HEADER_HANDLER_FACTORY.getHandler(this));
            }
        }
    }

    public void endElement(String             nsURI,
                           String             localName,
                           String             qName,
                           StAXContentHandler handler)
        throws SAXException
    {
        depth--;

        if (depth == 0)
        {
            if (localName.equals("BlastLikeDataSetCollection"))
            {
                scHandler.setMoreSearches(false);
            }
        }
        else
        {
            if (localName.equals("BlastLikeDataSet"))
            {
                scHandler.endSearch();
            }
        }
    }

    /**
     * <code>getSearchContentHandler</code> gets the handler which
     * will recieve the method calls generated by the adapter.
     *
     * @return a <code>SearchContentHandler</code>.
     */
    public SearchContentHandler getSearchContentHandler()
    {
        return scHandler;
    }

    /**
     * <code>setSearchContentHandler</code> sets the handler which
     * will recieve the method calls generated by the adapter.
     *
     * @param scHandler a <code>SearchContentHandler</code>.
     */
    public void setSearchContentHandler(SearchContentHandler scHandler)
    {
        this.scHandler = scHandler;
    }

    /**
     * <code>getProgram</code> returns the program type which
     * generated the results.
     *
     * @return a <code>String</code> indicating the progam
     * name.
     */
    String getProgram()
    {
        return program;
    }

    /**
     * <code>setProgram</code> informs the adapter which program type
     * it is working on.
     *
     * @param program a <code>String</code> indicating the progam
     * name.
     */
    void setProgram(String program)
    {
        this.program = program;
    }
}
