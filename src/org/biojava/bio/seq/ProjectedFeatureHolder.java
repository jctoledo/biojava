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

import java.util.*;
import java.lang.reflect.*;

import org.biojava.utils.*;
import org.biojava.utils.bytecode.*;
import org.biojava.bio.*;
import org.biojava.bio.seq.impl.*;
import org.biojava.bio.seq.projection.*;
import org.biojava.bio.symbol.*;
import org.biojava.bio.program.das.*;

/**
 * Helper class for projecting Feature objects into an alternative
 * coordinate system.  This class offers a view onto a set of features,
 * projecting them into a different coordinate system, and also changing
 * their <code>parent</code> property.  The destination coordinate system
 * can run in the opposite direction from the source, in which case the
 * <code>strand</code> property of StrandedFeatures is flipped.
 *
 * <p>
 * The projected features returned by this class are small proxy objects.
 * Proxy classes are autogenerated on demand for any sub-interface of
 * <code>Feature</code>.  These <code>getLocation</code>, <code>getParent</code>,
 * <code>getSequence</code> and (where applicable) <code>getStrand</code> methods
 * of projected features may return different values from the underlying
 * feature.  All other methods are proxied directly.
 * </p>
 *
 * <p>
 * Originally, <code>ProjectedFeatureHolder</code> was a self-contained
 * class containing the full projection infrastructure.  Since BioJava
 * 1.2, most of the projection infrastructure has been moved to separate
 * classes in the package <code>org.biojava.bio.seq.projection</code>.
 * Custom applications may wish to use that code directly.
 * </p>
 *
 * @author Thomas Down
 * @author Matthew Pocock
 * @since 1.1
 */

public class ProjectedFeatureHolder extends AbstractFeatureHolder {
    private final FeatureHolder wrapped;
    private final FeatureHolder parent;
    private final int translate;
    private final boolean oppositeStrand;

    private FeatureFilter filter;

    private ChangeListener underlyingFeaturesChange;
    private PFHContext projectionContext;    

    public static FeatureHolder projectFeatureHolder(FeatureHolder fh,
						     FeatureHolder parent, 
						     int translation,
						     boolean flip)
    {
	    return new ProjectedFeatureHolder(fh, parent, translation, flip);    
    }

    /**
     * Construct a new FeatureHolder which projects a set of features
     * into a new coordinate system.  If <code>translation</code> is 0
     * and <code>oppositeStrand</code> is <code>false</code>, the features
     * are simply reparented without any transformation.
     *
     * @param fh The set of features to project.
     * @param filter A FeatureFilter to apply to the set of features before projection.
     * @param parent The FeatureHolder which is to act as parent
     *               for the projected features.
     * @param translation The translation to apply to map locations into
     *                    the projected coordinate system.  This is the point
     *                    in the destination coordinate system which is equivalent
     *                    to 0 in the source coordinate system.
     * @param oppositeStrand <code>true</code> if translating into the opposite coordinate system.
     *                       This alters the transformation applied to locations, and also flips
     *                       the <code>strand</code> property of StrandedFeatures.
     */

    public ProjectedFeatureHolder(FeatureHolder fh,
				                  FeatureFilter filter,
                                  FeatureHolder parent, 
                                  int translation,
                                  boolean oppositeStrand) 
    {
        this.wrapped = fh;
        this.parent = parent;
        this.translate = translation;
        this.oppositeStrand = oppositeStrand;
        this.filter = filter;

        this.projectionContext = new PFHContext();

        underlyingFeaturesChange = new ChangeListener() {
            public void preChange(ChangeEvent e)
                throws ChangeVetoException 
            {
                if (hasListeners()) {
                        getChangeSupport(FeatureHolder.FEATURES).firePreChangeEvent(new ChangeEvent(this,
								     FeatureHolder.FEATURES,
								     e.getChange(),
								     e.getPrevious(),
								     e));
                }
            }

            public void postChange(ChangeEvent e) {
               if (hasListeners()) {
                   getChangeSupport(FeatureHolder.FEATURES).firePostChangeEvent(new ChangeEvent(this,
								    FeatureHolder.FEATURES,
								    e.getChange(),
								    e.getPrevious(),
                                    e));
               }
            }
        } ;

        wrapped.addChangeListener(underlyingFeaturesChange);
    }

    /**
     * Construct a new FeatureHolder which projects a set of features
     * into a new coordinate system.  If <code>translation</code> is 0
     * and <code>oppositeStrand</code> is <code>false</code>, the features
     * are simply reparented without any transformation.
     *
     * @param fh The set of features to project.
     * @param parent The FeatureHolder which is to act as parent
     *               for the projected features.
     * @param translation The translation to apply to map locations into
     *                    the projected coordinate system.  This is the point
     *                    in the destination coordinate system which is equivalent
     *                    to 0 in the source coordinate system.
     * @param oppositeStrand <code>true</code> if translating into the opposite coordinate system.
     *                       This alters the transformation applied to locations, and also flips
     *                       the <code>strand</code> property of StrandedFeatures.
     */

    public ProjectedFeatureHolder(FeatureHolder fh,
				  FeatureHolder parent, 
				  int translation,
				  boolean oppositeStrand) 
    {
        this(fh, null, parent, translation, oppositeStrand);
    }
    
    public int countFeatures() {
        if (filter != null) {
            return wrapped.filter(filter, false).countFeatures();
        } else {
            return wrapped.countFeatures();
        }
    }

    public Iterator features() {
        final Iterator wrappedIterator;
        if (filter == null) {
            wrappedIterator = wrapped.features();
        } else {
            wrappedIterator = wrapped.filter(filter, false).features();
        }
        return new Iterator() {
            public boolean hasNext() {
                return wrappedIterator.hasNext();
            }
            
            public Object next() {
                return projectFeature((Feature) wrappedIterator.next());
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
        } ;
    }
    
    public boolean containsFeature(Feature f) {
        for (Iterator fi = features(); fi.hasNext(); ) {
            if (f.equals(fi.next())) {
                return true;
            }
        }
        return false;
    }

    public FeatureHolder filter(FeatureFilter ff) {
        return filter(ff, true); // bit of a hack for now.
    }
    
    public FeatureHolder filter(FeatureFilter ff, boolean recurse) {
        ff = transformFilter(ff);
        if (filter != null) {
            ff = new FeatureFilter.And(ff, filter);
        }
        FeatureHolder toProject = wrapped.filter(ff, recurse);
        return new ProjectedFeatureHolder(toProject, parent, translate, oppositeStrand);
    }
    
    private FeatureFilter transformFilter(FeatureFilter ff) {
        FeatureFilter ff2 = _transformFilter(ff);
        if (ff2 == null) {
            System.err.println("Null filter!");
        }
        return ff2;
    }
    
    private FeatureFilter _transformFilter(FeatureFilter ff) {
        if (ff instanceof FeatureFilter.And) {
            FeatureFilter ff1 = ((FeatureFilter.And) ff).getChild1();
            FeatureFilter ff2 = ((FeatureFilter.And) ff).getChild2();
            return new FeatureFilter.And(transformFilter(ff1), transformFilter(ff2));
        } else if (ff instanceof FeatureFilter.Or) {
            FeatureFilter ff1 = ((FeatureFilter.Or) ff).getChild1();
            FeatureFilter ff2 = ((FeatureFilter.Or) ff).getChild2();
            return new FeatureFilter.Or(transformFilter(ff1), transformFilter(ff2));
        } else if (ff instanceof FeatureFilter.Not) {
            return new FeatureFilter.Not(transformFilter(((FeatureFilter.Not) ff).getChild()));
        } else if (ff instanceof FeatureFilter.OverlapsLocation) {
            return new FeatureFilter.OverlapsLocation(untransformLocation(((FeatureFilter.OverlapsLocation) ff).getLocation()));
        } else if (ff instanceof FeatureFilter.ContainedByLocation) {
            return new FeatureFilter.ContainedByLocation(untransformLocation(((FeatureFilter.ContainedByLocation) ff).getLocation()));
        } else if (ff instanceof FeatureFilter.StrandFilter) {
            return new FeatureFilter.StrandFilter(transformStrand(((FeatureFilter.StrandFilter) ff).getStrand()));
        } else {
            // should check for unknown cases.
            
            return ff;
        }
    }
    
    private Location untransformLocation(Location oldLoc) {
        if (oppositeStrand) {
            if (oldLoc.isContiguous()) {
                if (oldLoc instanceof PointLocation){
                    return new PointLocation(translate - oldLoc.getMin());
                } else {
                    return new RangeLocation(translate - oldLoc.getMax(),
    	                                     translate - oldLoc.getMin());
                }
            } else {
                Location compound = Location.empty;
                List locList = new ArrayList();
                for (Iterator i = oldLoc.blockIterator(); i.hasNext(); ) {
                    Location oldBlock = (Location) i.next();
                    locList.add(new RangeLocation(translate - oldBlock.getMax(),
                    		      			translate - oldBlock.getMin()));
                }
                compound = LocationTools.union(locList);
                return compound;
            }
        } else {
            return oldLoc.translate(-translate);
        }
    }
    
    private Location transformLocation(Location oldLoc) {
        if (oppositeStrand) {
            if (oldLoc.isContiguous()) {
                if (oldLoc instanceof PointLocation){
                    return new PointLocation(translate - oldLoc.getMin());
                } else {
                    return new RangeLocation(translate - oldLoc.getMax(),
    	                                     translate - oldLoc.getMin());
                }
            } else {
                Location compound = Location.empty;
                List locList = new ArrayList();
                for (Iterator i = oldLoc.blockIterator(); i.hasNext(); ) {
                    Location oldBlock = (Location) i.next();
                    locList.add(new RangeLocation(translate - oldBlock.getMax(),
                    		      			translate - oldBlock.getMin()));
                }
                compound = LocationTools.union(locList);
                return compound;
            }
        } else {
            return oldLoc.translate(translate);
        }
    }
    
    private StrandedFeature.Strand transformStrand(StrandedFeature.Strand s) {
        if (isOppositeStrand()) {
            if (s == StrandedFeature.POSITIVE) {
                return StrandedFeature.NEGATIVE;
            } else if (s == StrandedFeature.NEGATIVE) {
                return StrandedFeature.POSITIVE;
            } else {
                return StrandedFeature.UNKNOWN;
            }
        } else {
            return s;
        }
    }
    
    public Feature projectFeature(Feature f) {
        return ProjectionEngine.DEFAULT.projectFeature(f, projectionContext);
    }

    public int getTranslation() {
        return translate;
    }

    public boolean isOppositeStrand() {
        return oppositeStrand;
    }

    public FeatureHolder getParent() {
        return parent;
    }
        
    /**
     * ProjectionContext implementation tied to a given ProjectedFeatureHolder
     */

    private class PFHContext implements ProjectionContext {
        public FeatureHolder getParent(Feature f) {
            return parent;
        }	    

        public Sequence getSequence(Feature f) {
            FeatureHolder fh = parent;
            while (fh instanceof Feature) {
                fh = ((Feature) fh).getParent();
            }
            return (Sequence) fh;
        }

        public Location getLocation(Feature f) {
            Location oldLoc = f.getLocation();
            return transformLocation(oldLoc);
        }

        public StrandedFeature.Strand getStrand(StrandedFeature sf) {
            StrandedFeature.Strand s = sf.getStrand();
            return transformStrand(s);
        }

        public Annotation getAnnotation(Feature f) {
            return f.getAnnotation();
        }

        public FeatureHolder projectChildFeatures(Feature f, FeatureHolder parent) {
            return projectFeatureHolder(f, parent, getTranslation(), isOppositeStrand());
        }

        public Feature createFeature(Feature f, Feature.Template templ) 
	        throws ChangeVetoException
        {
            throw new ChangeVetoException("Can't create features in this projection");
        }

        public void removeFeature(Feature f, Feature f2) 
                throws ChangeVetoException
	    {
	        throw new ChangeVetoException("Can't create features in this projection");
	    }
    }
}
