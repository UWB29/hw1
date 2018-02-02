/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package homework1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author B-29
 */
public class NGrams implements java.io.Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8357754116746988220L;
	
	private int nSize;
    private double count;
    private Map<String, Double> Gram = new HashMap<>();
    private Map<String, Integer> vocab = new HashMap<>();
    private NGrams pre = null;
    private ValueComparator bvc =  new ValueComparator(Gram);
    private Map<String, Double> sorted_map = new TreeMap<String,Double>(bvc);
    private List<String> topTen;
    private final String[] intailize = {"*","*","*","*","*","*","*","*","*","*"};
    
    public NGrams(int Size) {
        nSize = Size;
        count = 0;
        topTen = new ArrayList<>(Arrays.asList(intailize));
        if (Size > 2) {
        	pre = new NGrams(Size - 1);
        }
    }
    
    public double getWordCount() {
        return count;
    }
    
    public int getVocabCount() {
        return vocab.size();
    }
    
    public void train(String message) {
        List<String> in = parse(message);
        count = count + in.size();
        updateVocab(in);
        updateNGram(in);
        if (nSize > 2) {
        	pre.train(message);
        }
        sorted_map.putAll(Gram);
        //updateTopTen();
    }
    
    private void updateTopTen() {
    	double cur = 0;
    	
    	for (String key : Gram.keySet()) {
    		//System.out.println(key);
    		
    		cur = getProbability(key);
    		//System.out.println(cur);
    		
    	}
	}

	public List<String> getTopTen(int size) {
    	List<String> temp = new ArrayList<>();
    	
    	if (nSize == size) {
	    	int i = 0;
	    	for (String key : sorted_map.keySet()) {
	    	    temp.add(key + ", " + getProbability(key));
	    		
	    		if (i++ == 10) {
	    	        break;
	    	    }
	    	}
    	} else {
    		temp = pre.getTopTen(size);
    	}
    	return temp;
    }
    
    public double getProbability(String input) {
		double prob = 0;
    	List<String> sentence = parse(input);
    	switch(sentence.size()) {
    		case 1:
    			System.out.println(input);
    			prob = vocab.get(input) / count;
    			break;
    		default:
    			if (Gram.containsKey(input)) {
    				prob = (1 + Gram.get(input));
    			} else {
    				prob = 1; 
    			}
    			prob = prob / (Gram.size() + vocab.size());
    			break;
    		
    	}
    	
    	return prob;
    }
    
    private void updateNGram(List<String> in) {
    	String x = ""; 
    	int set = in.size() - nSize + 1;
    	
    	for (int i = 0; i < set; i++) {
			x = "";
    		int j = 1;
			for (int y = 0; y < nSize; y++) {
				x = x + in.get(y + i);
				if (y + 1 < nSize) {
					x = x + " ";
				}
			}
			x = x + "";
			if(Gram.containsKey(x)){
				j += Gram.get(x);
			}
			
			Gram.put(x, (double) j);
		}
		
	}

	private void printMap(Map<String, Integer> input) {
		for (String key : input.keySet()) {
			System.out.print(key);
			System.out.print(", ");
			System.out.println(input.get(key));
		}
		
	}

	private void updateVocab(List<String> in) {
		String x; 
    	
    	for (int i = 0; i < in.size(); i++) {
			int j = 1;
			x = in.get(i);
			if(vocab.containsKey(x)){
				j += vocab.get(x);
			}
			
			vocab.put(x, j);
		}
	}

	private List<String> parse(String input) {
    	String[] temp = input.split(" ");    	
    	List<String> in = new ArrayList<>();
    	
    	for (String x: temp) {
    		if (!normalize(x).isEmpty())
    			in.add(normalize(x));
    	}
    	
    	return in;
    }

	private String normalize(String input) {
		input = input.trim();
		input = input.toLowerCase().replaceAll("\\W", "");
		
		
		
		return input;
	}
}

class ValueComparator implements Comparator<String> {

    Map<String, Double> base;
    public ValueComparator(Map<String, Double> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return 1;
        } else {
            return -1;
        } // returning 0 would merge keys
    }
}