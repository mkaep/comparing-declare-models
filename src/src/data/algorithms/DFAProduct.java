package data.algorithms;

import java.util.HashSet;
import java.util.Set;

import data.algorithms.util.PDFALanguageType;
import data.algorithms.util.SetOperations;
import data.automaton.definitions.Alphabet;
import data.automaton.definitions.DFA;
import data.automaton.definitions.State;
import data.automaton.definitions.Symbol;
import data.automaton.implementations.AutomatonFactory;
import data.math.definitions.BinaryTuple;
import data.math.definitions.TransitionFunction;
import data.math.exceptions.OutOfDomainException;
import data.math.implementations.MathFactory;

class DFAProduct {

	private DFAProduct() {}

	/**
	 * This method returns a product dfa = dfa1 x dfa2. The Language that is decided by the product dfa is defined by the type parameter.
	 * @param dfa1 the first dfa of the operation
	 * @param dfa2 the second dfa of the operation
	 * @param type defines the language of the P-DFA
	 * @throws IllegalArgumentException if the alphabets of the input dfas are not equal.
	 * @return the product dfa
	 */
	static DFA productDFA(DFA dfa1, DFA dfa2, PDFALanguageType type) {
		if(!dfa1.getAlphabet().equals(dfa2.getAlphabet()))
			throw new  IllegalArgumentException("The alphabets must be equal!");
		Set<BinaryTuple<State,State>> stateTuples = SetOperations.cartesianProduct(dfa1.getStates(),dfa2.getStates());
		Alphabet alphabet = dfa1.getAlphabet();
		BinaryTuple<State,State> startStateTuple = MathFactory.createBinaryTuple(dfa1.getStartState(), dfa2.getStartState());
		Set<BinaryTuple<State,State>> acceptingStateTuples = type.getAcceptingStatesProduct(dfa1, dfa2);
		TransitionFunction function = getProductDFATransitionFunction(alphabet,stateTuples,dfa1.getTransitionFunction(),dfa2.getTransitionFunction());
		
		return AutomatonFactory.createDFA(
				getStateSet(stateTuples), 
				alphabet, 
				function, 
				AutomatonFactory.createState(getProductDFAStateName(startStateTuple)), 
				getStateSet(acceptingStateTuples));
	}
	
	/**
	 * Returns the transition function of a product dfa.
	 * @param alphabet	alphabet of the product dfa
	 * @param stateTupels tupels that represent the new state names
	 * @param trans1 the transition function of the original first dfa
	 * @param trans2 the transition function of the original second dfa
	 * @return  the transition function for the product dfa
	 */
	private static TransitionFunction getProductDFATransitionFunction(Alphabet alphabet, Set<BinaryTuple<State,State>> stateTupels, TransitionFunction trans1, TransitionFunction trans2) {
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> tupels = new HashSet<>();
		
		for(BinaryTuple<State,State> stateTupel: stateTupels)
			for(Symbol symbol: alphabet) {
				try {
					State newOrigin = AutomatonFactory.createState(getProductDFAStateName(stateTupel));
					State dest1 = trans1.apply(stateTupel.getFirst(), symbol);
					State dest2 = trans2.apply(stateTupel.getSecond(), symbol);
					State newTarget = AutomatonFactory.createState(getProductDFAStateName(MathFactory.createBinaryTuple(dest1,dest2)));
					tupels.add(MathFactory.createTransition(newOrigin, symbol, newTarget));
				} catch (OutOfDomainException e) {}
			}
				
		return MathFactory.createTransitionFunction(tupels);
	}
	
	/**
	 * Returns the state set for a product dfa.
	 * @param tuples tuple representation of the names of the product-dfa-states
	 * @return the new state set for a product dfa.
	 */
	private static Set<State> getStateSet(Set<BinaryTuple<State,State>> tuples){
		Set<State> states = new HashSet<>();
		for(BinaryTuple<State,State> tuple: tuples)
			states.add(AutomatonFactory.createState(getProductDFAStateName(tuple)));
		return states;
	}
	
	/**
	 * Returns the new name for a state in a product dfa.
	 * @param stateTuple the tuple defining the new state
	 * @return the new state name
	 */
	private static String getProductDFAStateName(BinaryTuple<State,State> stateTuple) {
		return BinaryTuple.getElementsString(stateTuple);
	}
}
