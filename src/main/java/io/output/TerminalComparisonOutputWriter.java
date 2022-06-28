package io.output;

import comparison.Comparison;
import io.exceptions.ModelParserException;
import io.interfaces.ComparisonOutputWriter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class TerminalComparisonOutputWriter implements ComparisonOutputWriter {

	private static final DecimalFormat df = new DecimalFormat("0.00");

	@Override
	public void handleOutput(Comparison output) {
		System.out.println("Two deterministic finite automata have been created for the given models!");
		System.out.println();
		isEquivalent(output);
	}
	
	private void isEquivalent(Comparison output) {
		System.out.println("The test for equivalence of the two DFAs returned: " +  output.areEquivalent());
		System.out.println();
		if(!output.areEquivalent())
			testSubset(output);
	}

	private void testSubset(Comparison output) {
		System.out.print("Is L(DFA1) subset of L(DFA2): ");		
		if(output.subset1Of2TestResult().isEmpty()) {
			System.out.println("Yes!");
			return;
		}
		System.out.println("No! --> " + output.subset1Of2TestResult().get() + " is in L(DFA1) and not in L(DFA2)");
		
		System.out.print("Is L(DFA2) subset of L(DFA1): ");		
		if(output.subset2Of1TestResult().isEmpty()) {
			System.out.println("Yes!");
			return;
		}
		System.out.println("No! --> " + output.subset2Of1TestResult().get() + " is in L(DFA2) and not in L(DFA1)");
		System.out.println();
		getRegexes(output);
	}
	
	private void getRegexes(Comparison output) {
		System.out.println("An Equivalent regular expression for DFA1 is: " + output.getFirstRegex());
		System.out.println("An Equivalent regular expression for DFA2 is: " + output.getSecondRegex());
		System.out.println();
		getWords(output);
	}
	
	private void getWords(Comparison output) {
		System.out.println("All words in L(DFA1) of maximum length " + output.getMaxWordSize() + " are:");
		System.out.println(output.getWordsOf1());
		System.out.println("All words in L(DFA2) of maximum length " + output.getMaxWordSize() + " are:");
		System.out.println(output.getWordsOf2());
		System.out.println();
		System.out.println("All words in L(DFA1) - L(DFA2) of maximum length " + output.getMaxWordSize() + " are:");
		System.out.println(output.getWordsOf1NotIn2());
		System.out.println("All words in L(DFA2) - L(DFA1) of maximum length " + output.getMaxWordSize() + " are:");
		System.out.println(output.getWordsOf2NotIn1());
		System.out.println();
	}

	@Override
	public void handleCreationException(ModelParserException exception) {
		//TODO
		System.out.println("Error during construction of the DFAs! --> " + exception.getMessage());
	}

	@Override
	public void handleFileNotFoundException() {
		// TODO Auto-generated method stub
		System.out.println("Files not found!");		
	}

	@Override
	public void handleDFAOutput(Comparison output) {
		System.out.println("The first generated DFA:");
		System.out.println(output.getFirstDFA().toString());
		System.out.println();

		System.out.println("The second generated DFA:");
		System.out.println(output.getSecondDFA().toString());
		System.out.println();		
	}

	@Override
	public void handleMatrixOutput(Comparison comparison) {
		List<List<Integer>> matrix = comparison.calculateDamerauLevenshtein();
		System.out.println("Damerau-Levenshtein distance:");
		System.out.print("| \t |");
		comparison.getWordsOf2().forEach(word -> {
			System.out.print(word + " \t |");
		});
		System.out.println(" SUM \t| AVERAGE \t |");
		System.out.println();

		IntStream.range(0, comparison.getWordsOf1().size()).forEach(i -> {
			System.out.print("| " + comparison.getWordsOf1().get(i) + "\t |");
			matrix.get(i).forEach(word -> {
				System.out.print(" " + word + " \t |");
			});
			double sum = matrix.get(i).stream().mapToInt(Integer::intValue).sum();
			double average = sum / matrix.get(i).size();
			System.out.print(" " + sum + " \t| " +  average  + " \t|");
			System.out.println();
		});
		System.out.println("\n");
	}

	@Override
	public void handleNormalizedMatrixOutput(Comparison comparison) {
		List<List<Double>> matrix = comparison.calculateNormalizedDamerauLevenshtein();
		List<Double> maxListVertical = new ArrayList<>();
		List<Double> maxListHorizontal = new ArrayList<>();

		System.out.println("Damerau-Levenshtein Normalized distance:");
		System.out.print("| \t |");
		comparison.getWordsOf2().forEach(word -> {
			System.out.print(word + " \t\t |");
		});
////		System.out.println(" SUM \t| AVERAGE \t |");
		System.out.println(" MAX \t|");

//		IntStream.range(0, comparison.getWordsOf1().size()).forEach(i -> {
//			System.out.print("| " + comparison.getWordsOf1().get(i) + "\t |");
//			matrix.get(i).forEach(word -> {
//				System.out.print(" " + word + " \t |");
//			});
//			double sum = matrix.get(i).stream().mapToDouble(Double::doubleValue).sum();
//			double average = sum / matrix.get(i).size();
//			double max = matrix.get(i).stream().max(Comparator.comparing(Double::valueOf)).get();
//			maxList.add(max);
////			System.out.print(" " + sum + " \t| " +  average  + " \t|");
//			System.out.print(" " + max +  " \t|");
//			System.out.println();
//		});
//
//		System.out.print("| MAX \t|");

		for (int i = 0; i < comparison.getWordsOf1().size(); i++) {
			System.out.print("| " + comparison.getWordsOf1().get(i) + "\t |");
			double max = matrix.get(i).stream().max(Comparator.comparing(Double::valueOf)).get();
			maxListHorizontal.add(max);

			for (int j = 0; j < comparison.getWordsOf2().size(); j++) {
				System.out.print(" " + df.format(matrix.get(i).get(j)) + " \t |");
				if (maxListVertical.size() >= j + 1) {
					if (maxListVertical.get(j) < matrix.get(i).get(j)) {
						maxListVertical.set(j, matrix.get(i).get(j));
					}
				} else {
					maxListVertical.add(matrix.get(i).get(j));
				}
			}
			System.out.print(" " + df.format(max) + "\t|");
			System.out.println();

		}
		System.out.print("| MAX \t |");
		maxListVertical.forEach(value -> {
			System.out.print(" " + df.format(value) + "\t\t |");
		});
		System.out.print(" \t| " + df.format(maxListVertical.stream().mapToDouble(Double::doubleValue).sum()/maxListVertical.size()));
		System.out.println();

		System.out.print("| \t |");
		maxListVertical.forEach(max -> {
			System.out.print(" \t\t |");
		});
		System.out.print(" " + df.format(maxListHorizontal.stream().mapToDouble(Double::doubleValue).sum()/maxListHorizontal.size()) + " \t|");

		System.out.println("\n");
	}
}
