package coreClasses;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

public class XML2Hunter {

	
	public static void main(String[] args){
		Options options = new Options();
		options.addOption( OptionBuilder.withLongOpt( "wildtype")
										.withDescription("an XML File generated by MutantHunter or containing the mapping of wt rawdata against wt assembly.")
										.isRequired()
										.hasArg()
										.withArgName("wildtypePileupFile")
										.create('w'));
		options.addOption( OptionBuilder.withLongOpt( "mutants")
										.withDescription("a set of additional XML files generated by MutantHunter or RnaSeqPileup2XML containing the mapping of mutants rawdata against wt assembly")
										.hasArgs()
										.withArgName("mutantPileupFiles")
										.create('m'));
		options.addOption( OptionBuilder.withLongOpt( "output")
										.withDescription("the prefix for all output files")
										.hasArgs()
										.withArgName("outputPrefix")
										.create('o'));
		options.addOption( OptionBuilder.withLongOpt( "wildtypeCoverage")
										.withDescription("The minimum coverage of wildtype reads for a contig. Default is 10")
										.hasArgs()
										.withArgName("wtCov")
										.create('c'));
		options.addOption( OptionBuilder.withLongOpt( "minNumMutants")
										.withDescription("The minimum number of mutants to report a NBLRR region. Default 2")
										.hasArgs()
										.withArgName("minMutants")
										.create('u'));
		options.addOption( OptionBuilder.withLongOpt( "maxRefAlleleFreq")
										.withDescription("The maximum reference allele frequency for a SNP to be reported. Default 0.1")
										.hasArgs()
										.withArgName("maxRefAlleleFreq")
										.create('a'));
		options.addOption( OptionBuilder.withLongOpt( "minSNPCoverage")
										.withDescription("The minimum coverage to consider a SNP")
										.hasArgs()
										.withArgName("minSNPCoverage")
										.create('s'));
		options.addOption( OptionBuilder.withLongOpt( "minNumZeroCoveragePositions")
										.withDescription("The minimum number of positions that need coverage 0 to consider as a deletion mutant")
										.hasArgs()
										.withArgName("minNumZeroCoveragePositions")
										.create('z'));
		
		
		CommandLineParser parser = new PosixParser();
		
		try{
			CommandLine line = parser.parse( options, args );
			
			
			File wt = new File(line.getOptionValue('w'));
			
			MutantHunter hunter = new MutantHunter(wt);
			
			System.err.println("wildtype read");
			
			if( line.hasOption('m')){
				String[] s = line.getOptionValues('m');
				for( int i = 0; i< s.length; i++){
					hunter.addXML(new File(s[i]), false);
					System.err.println(s[i] + " read");
				}
				
			}
		
			
			
			int minMutants = 5;
			int minWtCov = 10;
			double maxRefAlleleFrequency = 0.1;
			int minCoverageToConsiderSNP = 10;
			int minNumberOfZeroCoveragePositions = 10;
			
			
			if( line.hasOption('c')){
				minWtCov = Integer.parseInt(line.getOptionValue('c'));
			}
			if( line.hasOption('u')){
				minMutants = Integer.parseInt(line.getOptionValue('u'));
			}
			if( line.hasOption('s')){
				minCoverageToConsiderSNP = Integer.parseInt(line.getOptionValue('s'));
			}
			if( line.hasOption('z')){
				minNumberOfZeroCoveragePositions = Integer.parseInt(line.getOptionValue('z'));
			}
			if( line.hasOption('a')){
				maxRefAlleleFrequency = Double.parseDouble(line.getOptionValue('a'));
			}
		

			String outputString = wt.getParentFile().getAbsolutePath() + "_MutantHunter";
			if(line.hasOption('o')){
				outputString = line.getOptionValue('o');
			}
			
			boolean filterSynonymous = true;
			
			System.err.println( "MutantHunter: " + hunter.getNumberOfContigs() + " contigs in total");
			
			//File backupFile = new File(outputString + ".backup.xml");
			//hunter.exportToXML(backupFile);
			//System.err.println("data backed up to " + backupFile.getAbsolutePath());
		
			System.err.println("calculating candidates");
			hunter.findCandidates(new File(outputString + ".hunter.txt"), minWtCov, maxRefAlleleFrequency, minCoverageToConsiderSNP, minNumberOfZeroCoveragePositions, minMutants, filterSynonymous);
			
			
		
		}catch( Exception exp ) {
			exp.printStackTrace();
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
	        
	        HelpFormatter formatter = new HelpFormatter();
	        formatter.printHelp( "java -jar XML2Hunter.jar -w input.xml [options]", options );
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
}