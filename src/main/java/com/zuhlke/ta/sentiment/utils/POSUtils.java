package com.zuhlke.ta.sentiment.utils;

public class POSUtils {
	
	public static boolean isVerb(String word){
		return word.endsWith("VB") || word.endsWith("VBD") || word.endsWith("VBG")
				|| word.endsWith("VBN") || word.endsWith("VBP") || word.endsWith("VBZ");
	}
	
	public static boolean isNoun(String word){
		return word.endsWith("NN") || word.endsWith("NNS");
	}

	public static boolean isAdjective(String word){
		return word.endsWith("JJ") || word.endsWith("JJR") || word.endsWith("JJS");
	}

	public static boolean isAdverb(String word){
		return word.endsWith("RB") || word.endsWith("RBR") || word.endsWith("RBS") || word.endsWith("WRB");
	}
	
	public static boolean isLimitWord(String word){
		return word.endsWith("CC") || word.endsWith("IN") || word.endsWith("WDT") ||
				word.endsWith("WP") || word.endsWith("WP$");
	}
	
	public static boolean isSubject(String word){
		return word.endsWith("NN") || word.endsWith("NNS") || word.endsWith("NNP") ||
				word.endsWith("NNPS") || word.endsWith("PRP") || word.endsWith("PRP$");
	}
	
	public static boolean isModal(String word){
		return word.endsWith("MD");
	}
	
	public static String stripWord(String word){
		return word.toLowerCase().split("_")[0];
	}
}
