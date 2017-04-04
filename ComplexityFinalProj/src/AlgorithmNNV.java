/**
 * Solves 01 maximum knapsack instance in O(n^2V) time.
 * 
 * @author jstekete
 *
 */
public class AlgorithmNNV {
	public static int solve(Itemset itemset, int capacity) {

		Item[] items = itemset.items;

		int T = 0;
		for (Item i : items)
			T += i.value;

		int[][] value = new int[items.length][T + 1];
		Item curItem;
		int take;
		int skip;

		for (int i = items.length - 1; i >= 0; i--) {
			for (int t = 0; t < value[0].length; t++) {
				curItem = items[i];
				if (t == 0) {
					value[i][t] = 0;
				} else if (i == items.length - 1) {
					if (curItem.value >= t)
						value[i][t] = curItem.weight;
					else
						value[i][t] = 111;
				} else {
					if (t - curItem.value < 0)
						take = curItem.weight;
					else
						take = value[i + 1][t - curItem.value] + curItem.weight;
					skip = value[i + 1][t];
					if (take < skip)
						value[i][t] = take;
					else
						value[i][t] = skip;
				}
			}
		}
		int maxT = -1;
		for (int i = T; i >= 0; i--) {
			if (value[0][i] <= capacity) {
				maxT = i;
				break;
			}
		}
		/*
		 * for (int i = 0; i < items.length; i++) { for (int j = 0; j <
		 * value[0].length; j++) { System.out.print(value[i][j] + " "); }
		 * System.out.println(); }
		 */
		return maxT;
	}
}
