package org.biojava.bio.structure.quaternary.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.StructureException;
import org.biojava.bio.structure.io.mmcif.model.PdbxStructAssembly;
import org.biojava.bio.structure.io.mmcif.model.PdbxStructAssemblyGen;
import org.biojava.bio.structure.io.mmcif.model.PdbxStructOperList;
import org.biojava.bio.structure.quaternary.BiologicalAssemblyBuilder;
import org.biojava.bio.structure.quaternary.ModelTransformationMatrix;
import org.biojava3.structure.StructureIO;

public class MmCifBiolAssemblyProvider implements BioUnitDataProvider {

	MmCifPDBBiolAssemblyProvider provider; 
	
	public MmCifBiolAssemblyProvider(){
		provider  = new MmCifPDBBiolAssemblyProvider();
	}
	
	public Structure getAsymUnit(String pdbId){
	 
		provider.setPdbId(pdbId);
		
		Structure s1 = provider.getAsymUnit();
		
		
		
		return s1;
	}
	
	public void setAsymUnit(Structure s){
		provider.setAsymUnit(s);
	}
	
	@Override
	public List<ModelTransformationMatrix> getBioUnitTransformationList(
			String pdbId, int biolAssemblyNr) {
		
	// we start counting at 1!
		provider.setPdbId(pdbId);
		
		PdbxStructAssembly psa = provider.getPdbxStructAssembly(biolAssemblyNr-1) ;
		
		PdbxStructAssemblyGen psag = provider.getPdbxStructAssemblyGen(biolAssemblyNr-1);
		
		if ( psa == null || psag == null) {
			return null;
		}
		//System.out.println(psa);
		//System.out.println(psag);
		
		List<PdbxStructOperList> operators = provider.getPdbxStructOperList();
		//System.out.println(operators);
		
		
		/** 
		 * Now we start to rebuild the quaternary structure
		 */
		
		BiologicalAssemblyBuilder builder = new BiologicalAssemblyBuilder();
		
		// these are the transformations that need to be applied to our model
		List<ModelTransformationMatrix> transformations = builder.getBioUnitTransformationList(psa, psag, operators);
		//System.out.println(transformations);
		return transformations;
	}

	@Override
	public int getNrBiolAssemblies(String pdbId) {
		
		provider.setPdbId(pdbId);
		
		return provider.getNrBiolAssemblies();
	}

	@Override
	public boolean hasBiolAssembly(String pdbId) {


		provider.setPdbId(pdbId);
		
		return provider.hasBiolAssembly();
	}
	
	public Structure getBiolAssembly(String pdbId, int biolAssemblyNr) throws IOException, StructureException{

		
		provider.setPdbId(pdbId);
		
		if ( ! provider.hasBiolAssembly()){
			return null;
		}
				
		if (  provider.getNrBiolAssemblies() <= biolAssemblyNr){
			return null;
		}
		/** First we read the required data from wherever we get it from (configured in the factory)
		 * 
		 */
		PdbxStructAssembly psa = provider.getPdbxStructAssembly(biolAssemblyNr) ;
		
		PdbxStructAssemblyGen psag = provider.getPdbxStructAssemblyGen(biolAssemblyNr);
		
		//System.out.println(psa);
		//System.out.println(psag);
		
		List<PdbxStructOperList> operators = provider.getPdbxStructOperList();
		//System.out.println(operators);
		
		
		/** now we start to rebuild the quaternary structure
		 * 
		 */
		
		BiologicalAssemblyBuilder builder = new BiologicalAssemblyBuilder();
		
		// these are the transformations that need to be applied to our model
		ArrayList<ModelTransformationMatrix> transformations = builder.getBioUnitTransformationList(psa, psag, operators);
		
		
		
		Structure asymUnit = null;
		
		if ( provider instanceof MmCifPDBBiolAssemblyProvider){
			MmCifPDBBiolAssemblyProvider mmcifprov = (MmCifPDBBiolAssemblyProvider) provider;
			asymUnit = mmcifprov.getAsymUnit();
		} else {
			
			// how to request internal chain IDs?
			
			asymUnit = StructureIO.getStructure(pdbId);
			
		}
						
		return builder.rebuildQuaternaryStructure(asymUnit, transformations);
	}

}
