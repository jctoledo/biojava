package	seq;

import java.io.*;
import java.util.*;

import org.biojava.bio.*;
import org.biojava.bio.symbol.*;
import org.biojava.bio.seq.*;
import org.biojava.bio.seq.io.*;
import org.biojava.bio.taxa.*;

public class TestSwissprotOrg
{
	public static void main(String [] args)
	{
		try
		{
			if(args.length !=	1)
			{
				throw new Exception("Use: TestSwissProt swissprotFile");
			}

			File swissProtFile = new File(args[0]);
			SequenceFormat sFormat = new EmblLikeFormat();
			BufferedReader sReader = new BufferedReader(
					new	InputStreamReader(new FileInputStream(swissProtFile)));
			SequenceBuilderFactory sbFact = new SwissprotProcessor.Factory(
              new OrganismParser.Factory(
                SimpleSequenceBuilder.FACTORY,
                // SimpleTaxaFactory.GLOBAL, // in-memory implementation
                WeakTaxaFactory.GLOBAL, // only guarantees the bits you need exist
                EbiFormat.getInstance(),
                "OC",
                "OS",
                "OX"
              )
            );
			Alphabet alpha = ProteinTools.getAlphabet();
			SymbolTokenization rParser = alpha.getTokenization("token");
			SequenceIterator seqI =
					new	StreamReader(sReader, sFormat, rParser,	sbFact);

			while(seqI.hasNext())
			{
				Sequence seq = seqI.nextSequence();
				System.out.println(seq.getName() + " has " + seq.countFeatures() + " features");
        System.out.println("Species:");
        Taxa taxa = (Taxa) seq.getAnnotation().getProperty(OrganismParser.PROPERTY_ORGANISM);
        System.out.println("\t" + taxa);
        for(Iterator i = taxa.getAnnotation().asMap().entrySet().iterator(); i.hasNext(); ) {
          Map.Entry ent = (Map.Entry) i.next();
          System.out.println("\t : " + ent.getKey() + " -> " + ent.getValue());
        }
        
        
        //break;
			}
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			System.exit(1);
		}
	}
}
