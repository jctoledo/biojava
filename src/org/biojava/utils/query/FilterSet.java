package org.biojava.utils.query;

import java.util.*;

/**
 * Filter that evaluates a set of other filters, returning the boolean AND of
 * their accept methods.
 * <P>
 * This class is provided as an optimization to the case when a list of Filters
 * follow one another in the query. Filter operations are by definition order
 *-independant, so to aid later query optimization, the query graph may be re
 *-written so that these orderless sections become a single FilterSet.
 *
 * @author Matthew Pocock
 * @since 1.2
 */
public class FilterSet extends Filter {
  private Set filters;
  private Type inputType;
  private Type outputType;
  
  /**
   * Create a new FilterSet instance.
   */
  public FilterSet() {
    filters = new HashSet();
    inputType = JavaType.getType(Object.class);
    outputType = JavaType.getType(Object.class);
  }
  
  public FilterSet(Set filters) {
    this();
    this.filters.addAll(filters);
  }
  
  public Iterator filters() {
    return filters.iterator();
  }
  
  public Type getInputType() {
    return inputType;
  }
  
  public Type getOutputType() {
    return outputType;
  }
  
  public boolean accept(Object o)
  throws OperationException {
    for(Iterator i = filters(); i.hasNext(); ) {
      Filter filt = (Filter) i.next();
      
      if(!filt.accept(o)) {
        return false;
      }
    }
    
    return true;
  }
  
  public void add(Filter filt) {
    filters.add(filt);
  }
  
  public void remove(Filter filt) {
    filters.remove(filt);
  }
  
  public int hashCode() {
    return filters.hashCode();
  }
  
  public boolean equals(Object o) {
    if(o instanceof FilterSet) {
      FilterSet that = (FilterSet) o;
      return that.filters.equals(this.filters);
    } else {
      return false;
    }
  }
  
  public String toString() {
    return filters.toString();
  }
}

