/*
 * File: SRC/ORG/BIOCORBA/BIO/_PRIMARYSEQOPERATIONS.JAVA
 * From: IDL/BIO.IDL
 * Date: Mon Feb 07 12:51:47 2000
 *   By: idltojava Java IDL 1.2 Aug 18 1998 16:25:34
 */

package org.biocorba.Bio;
/**

 */public interface _PrimarySeqOperations
	extends org.biocorba.GNOME._UnknownOperations {
     org.biocorba.Bio.SeqType type()
;
     int length()
;
     String get_seq()
        throws org.biocorba.Bio.RequestTooLarge;
     String get_subseq(int start, int end)
        throws org.biocorba.Bio.OutOfRange, org.biocorba.Bio.RequestTooLarge;
     String display_id()
;
     String primary_id()
;
     String accession_number()
;
     int version()
;
     int max_request_length()
;
}
