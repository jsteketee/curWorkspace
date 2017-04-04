
/**
 * Algorithm to solve 0-1 Knapsack using dynamic programming and approximation
 * scheme. O(n^3*1/e).
 * 
 * @author jstekete
 *
 */
public class AlgorithmFPTAS {

	static final double EPSILON = 0.1;

	public static int solve(Itemset itemset, int capacity) {
		Item[] items = itemset.items;
		double curVal;
		double maxVal = 0;
		double n = items.length;
		for (int i = 0; i < items.length; i++) {
			if (items[i].value > maxVal)
				maxVal = items[i].value;
		}
		for (int i = 0; i < items.length; i++) {
			curVal = items[i].value;
			curVal = Math.round(curVal * (n / (maxVal * EPSILON)));
			items[i].value = (int) curVal;
		}
		return (int) (AlgorithmNNV.solve(itemset, capacity) * ((maxVal * EPSILON) / n));
	}
}
