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

package org.biojava.utils.automata;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import org.biojava.bio.BioError;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.FiniteAlphabet;
import org.biojava.bio.symbol.AlphabetIndex;
import org.biojava.bio.symbol.AlphabetManager;
import org.biojava.bio.symbol.Symbol;

/**
 * Class for modelling finite automata.
 * <p>
 * This class models basic FA behaviour.  More specialised
 * behaviour is implemented by subclassing this.
 *
 * @author David Huen
 * @since 1.4
 */
public class FiniteAutomaton
{
    /**
     * describes a Node in this finite automaton.
     */
    class Node
    {
        private int nodeID;

        /**
         * Nodes are assigned nodeIDs sequentially.
         * Terminal nodes are assigned negative
         * Node IDs.
         */
        protected Node(boolean terminal) 
        { 
            if (terminal)
                nodeID = -(nodeCount++);
            else
                nodeID = nodeCount++; 
        }

        protected Node(int nodeID)
        {
            this.nodeID = nodeID;
        }

        /**
         * is this a terminal Node?
         */
        boolean isTerminal()
        {
            return nodeID < 0;
        }

        /**
         * returns the Node ID.
         */
        private int getID() { return nodeID; }

        /**
         * Two Nodes are equal if they share the same
         * parent and have the same node ID.
         */
        public boolean equals(Object o)
        {
            if (!(o instanceof Node))
                return false;

            // check that outer class is the same!
            Node other = (Node) o;

            if (other.parent() != parent())
                return false;

            if ((other.getID() != getID()))
                return true;
            else
                return false;
        }

        public FiniteAutomaton parent() { return FiniteAutomaton.this; }
        public int hashCode() { return nodeID; }
    }

    /**
     * a class that represents a Set of Nodes.
     */
    class NodeSet
        extends HashSet
    {
        /**
         * Adds a Node to this NodeSet.  The Node and the NodeSet
         * to which it is added must be from the same FiniteAutomaton
         * instance.
         */
        void addNode(Node node)
            throws AutomatonException
        {
            if (!(nodes.contains(node)))
                throw new AutomatonException("attempt to add foreign Node to this NodeSet.");

            add(node);
        }

        void addNodeSet(NodeSet otherSet)
            throws AutomatonException
        {
            // it will be assumed that the other NodeSet is legit
            // if it comes from this FiniteAutomaton.
            if (otherSet.parent() != FiniteAutomaton.this)
                throw new AutomatonException("attempt to add foreign NodeSet to this NodeSet.");

            addAll(otherSet);
        }

        /**
         * Equality implies that both Objects are NodeSets,
         * both from the same FiniteAutomaton instance and
         * have the same Nodes within them.
         */
        public boolean equals(Object o)
        {
            // only NodeSets can be compared.
            if ((o instanceof NodeSet))
                return false;

            // Contents of NodeSets must be from same model.
            NodeSet other = (NodeSet) o;
            if (parent() != other.parent())
                return false;

            // sets themselves must be equal
            return super.equals(o);
        }

        /**
         * The hashCode is currently the sum of Node hashCodes.
         */
        public int hashCode()
        {
            int sum = 0;

            for (Iterator setI = iterator(); setI.hasNext(); ) {
                sum += hashCode();
            }

            return sum;
        }

        /**
         * Returns the parental FiniteAutomaton that
         * this NodeSet is a child of.
         */
        FiniteAutomaton parent()
        {
            return FiniteAutomaton.this;
        }
    }

    /**
     * Models a Transition within the FiniteAutomaton.
     */
    class Transition
    {
        protected Node source;
        protected Node dest;
        protected Symbol sym;

        /**
         * @param source The source Node of the transition.
         * @param dest The destination Node of the transition.
         * @param sym The Symbol that invokes the transition.
         */
        private Transition(Node source, Node dest, Symbol sym)
        {
            this.source = source;
            this.dest = dest;
            this.sym = sym;
        }

        private Node getSource() { return source; }
        private Node getDest() { return dest; }
        private Symbol getSymbol() { return sym; }

        /**
         * Sets the source Node.
         */
        void setSource(Node source)
            throws AutomatonException
        {
            if (source.parent() != FiniteAutomaton.this)
                throw new AutomatonException("Attempt to Node from one Model in another.");

            this.source = source;
        }

        /**
         * Sets the destination Node.
         */
        void setDest(Node dest)
            throws AutomatonException
        {
            if (dest.parent() != FiniteAutomaton.this)
                throw new AutomatonException("Attempt to Node from one Model in another.");

            this.dest = dest;
        }

        /**
         * Two Transitions are equal if they are both
         * defined on the same FiniteAutomaton instance
         * and their source and destination Nodes and associated
         * Symbols are equal.
         */
        public boolean equals(Object o)
        {
            if (!(o instanceof Transition))
                return false;

            // check that outer class is the same!
            Transition other = (Transition) o;

            if (other.parent() != parent())
                return false;

            if ((source.equals(other.source)) &&
                (dest.equals(other.dest)) &&
                (sym == other.getSymbol()))
                return true;
            else
                return false;
        }

        public FiniteAutomaton parent() { return FiniteAutomaton.this; }
        public int hashCode() 
        { 
            try {
                return source.getID() + dest.getID() + alfaIdx.indexForSymbol(sym); 
            }
            catch (IllegalSymbolException ise) {
                throw new BioError("Fatal error: Unexpected IllegalSymbolException on computing indexForSymbol.");
            }
        }
    }

    private FiniteAlphabet alfa;
    private AlphabetIndex alfaIdx;
    private String name;

    // private data storage
    /**
     * User Nodes are assigned Node IDs of 2 and larger.
     */
    private int nodeCount = 2;
    protected Set nodes = new HashSet();
    protected Set transitions = new HashSet();

    private Map transitionMap = new HashMap();

    private int START = 0;
    private int END = -1;
    protected Node start = new Node(START);
    protected Node end = new Node(END);

    FiniteAutomaton(String name, FiniteAlphabet alfa)
    {
        this.alfa = alfa;
        this.name = name;
        alfaIdx = AlphabetManager.getAlphabetIndex(alfa);
    }

    FiniteAlphabet getAlphabet() { return alfa; }
    String getName() { return name; }
    Node getStart() { return start; }
    Node getEnd() { return end; }

    void addTransition(Node start, Node end, Symbol sym)
    {
        transitions.add(new Transition(start, end, sym));
    }

    /**
     * Add a node to the FA.
     * @param terminal Is the Node terminal?
     */
    Node addNode(boolean terminal)
    {
        Node node = new Node(terminal);
        nodes.add(node);
        return node;
    }

    /**
     * What is the Set of Nodes reachable from source
     * Node by transitions associated with Symbol.
     */
    NodeSet getClosure(Node source, Symbol sym)
    {
        NodeSet closureSet = createNodeSet();

        for (Iterator transI = getTransitions(source).iterator(); transI.hasNext(); ) {
            Transition currTransition = (Transition) transI.next();

            try {
                if (currTransition.sym == sym)
                    closureSet.addNode(currTransition.dest);
            }
            catch (AutomatonException ae) {
                throw new AssertionError(ae);
            }
        }

        return closureSet;
    }    

    /**
     * get Symbols associated with transitions from the specified source.
     */
    Set getSymbols(Node source)
    {
        Set symbolSet = new HashSet();

        for (Iterator transI = getTransitions(source).iterator(); transI.hasNext(); ) {
            Transition currTransition = (Transition) transI.next();
            symbolSet.add(currTransition.sym);
        }

        return symbolSet;
    }

    /**
     * retrieve Set of all transitions from a specified Node.
     */
    private Set getTransitions(Node source)
    {
        Set transitionSet;

        if ((transitionSet = (Set) transitionMap.get(source)) == null) {

            transitionSet = new HashSet();
    
            for (Iterator transI = transitions.iterator(); transI.hasNext(); ) {
                Transition currTransition = (Transition) transI.next();
                if (currTransition.source == source)
                    transitionSet.add(currTransition);
            }

            transitionMap.put(source, transitionSet);
        }
        
        return transitionSet;
    }

    NodeSet createNodeSet() { return new NodeSet(); }
}

