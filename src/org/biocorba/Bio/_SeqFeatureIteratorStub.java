/*
 * File: SRC/ORG/BIOCORBA/BIO/_SEQFEATUREITERATORSTUB.JAVA
 * From: IDL/BIO.IDL
 * Date: Mon Feb 07 12:51:47 2000
 *   By: idltojava Java IDL 1.2 Aug 18 1998 16:25:34
 */

package org.biocorba.Bio;
public class _SeqFeatureIteratorStub
	extends org.omg.CORBA.portable.ObjectImpl
    	implements org.biocorba.Bio.SeqFeatureIterator {

    public _SeqFeatureIteratorStub(org.omg.CORBA.portable.Delegate d) {
          super();
          _set_delegate(d);
    }

    private static final String _type_ids[] = {
        "IDL:Bio/SeqFeatureIterator:1.0",
        "IDL:GNOME/Unknown:1.0"
    };

    public String[] _ids() { return (String[]) _type_ids.clone(); }

    //	IDL operations
    //	    Implementation of ::GNOME::Unknown::ref
    public void ref()
 {
           org.omg.CORBA.Request r = _request("ref");
           r.invoke();
   }
    //	    Implementation of ::GNOME::Unknown::unref
    public void unref()
 {
           org.omg.CORBA.Request r = _request("unref");
           r.invoke();
   }
    //	    Implementation of ::GNOME::Unknown::query_interface
    public org.omg.CORBA.Object query_interface(String repoid)
 {
           org.omg.CORBA.Request r = _request("query_interface");
           r.set_return_type(org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_objref));
           org.omg.CORBA.Any _repoid = r.add_in_arg();
           _repoid.insert_string(repoid);
           r.invoke();
           org.omg.CORBA.Object __result;
           __result = r.return_value().extract_Object();
           return __result;
   }
    //	    Implementation of ::Bio::SeqFeatureIterator::next
    public org.biocorba.Bio.SeqFeature next()
 {
           org.omg.CORBA.Request r = _request("next");
           r.set_return_type(org.biocorba.Bio.SeqFeatureHelper.type());
           r.invoke();
           org.biocorba.Bio.SeqFeature __result;
           __result = org.biocorba.Bio.SeqFeatureHelper.extract(r.return_value());
           return __result;
   }
    //	    Implementation of ::Bio::SeqFeatureIterator::has_more
    public boolean has_more()
 {
           org.omg.CORBA.Request r = _request("has_more");
           r.set_return_type(org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean));
           r.invoke();
           boolean __result;
           __result = r.return_value().extract_boolean();
           return __result;
   }

};
