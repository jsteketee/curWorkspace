import java.util.Random;

public class KMeans {

	// Number of classes
	private int K;

	// 2d Array containing characteristics of each pixel.
	private Characteristic[][] pixelProperties;

	// Array containing the mean pixel characteristics for each class.
	private Characteristic[] means;

	// Number of dimensions in characteristics.
	private int numDimensions;

	// Tells the KMeans object what distance method to use.
	KMeansType type;

	// Random number generator
	private Random rand = new Random();

	/**
	 * Constructs a KMeans object with the appropriate number of classes,
	 * characteristic dimensions, number of rows and number of columns.
	 * 
	 * @param numClasses The number of classes
	 * @param numDims The number of dimensions in characteristic
	 * @param numRows The number of rows
	 * @param numCols The number of columns
	 * @param type The specific characteristic/distance scheme being used.
	 */
	public KMeans(int numClasses, int numDims, int numRows, int numCols, KMeansType type) {
		this.type = type;
		K = numClasses;
		means = new Characteristic[K];
		pixelProperties = new Characteristic[numRows][numCols];
		numDimensions = numDims;
	}

	/**
	 * Iteratively Groups pixels into one of K classes based on minimum distance
	 * to class mean. Class means are then recalculated based on average
	 * attributes of its members. This process is repeated until class
	 * membership becomes static.
	 */
	public void runKmeans() {
		randomizeMeans();
		while (determineClass()) {
			recomputeMeans();
		}
	}

	/**
	 * Creates the initial mean attribute value(s) for each class. Note that
	 * attributes values will be normalized.
	 */
	private void randomizeMeans() {
		rand.setSeed(1);
		for (int k = 0; k < K; k++) {
			for (int i = 0; i < numDimensions; i++) {
				means[k].setVal(i, rand.nextDouble());
			}
		}
	}

	/**
	 * Calculates the average attribute values for all members of each class and
	 * then assigns those values to be the class means.
	 */
	private void recomputeMeans() {
		int[] classCardinality = new int[K];
		Characteristic[] sums = new Characteristic[K];
		for (Characteristic[] pArray : pixelProperties) {
			for (Characteristic pixel : pArray) {
				sums[pixel.getClassNumber()].addValues(pixel);
				classCardinality[pixel.getClassNumber()]++;
			}
		}
		for (int classNum = 0; classNum < K; classNum++) {
			for (int dimension = 0; dimension < numDimensions; dimension++) {
				means[classNum].setVal(dimension, sums[classNum].getVal(dimension) / classCardinality[classNum]);
			}
		}
	}

	/**
	 * Assigns the attribute values to the specified pixel.
	 * 
	 * @param r the row
	 * @param c the column
	 * @param vals the values to be assigned
	 */
	public void assignPixelProperties(int r, int c, double[] vals) {
		pixelProperties[r][c] = new Characteristic(numDimensions);
		pixelProperties[r][c].setValues(vals);
	}

	/**
	 * Determines the class of every pixel in PixelProperties based on the
	 * shortest distance to the current class means.
	 * 
	 * @return whether or not any class reassignments.
	 */
	private boolean determineClass() {
		boolean classChange = false;
		int closestClass = 0;
		double minDistance = 1;
		double curDistance;
		for (Characteristic[] pArray : pixelProperties) {
			for (Characteristic pixel : pArray) {
				for (int i = 0; i < means.length; i++) {
					curDistance = pixel.UDistance(means[i]);
					if (curDistance < minDistance) {
						minDistance = curDistance;
						closestClass = i;
					}
				}
				if (pixel.getClassNumber() != closestClass)
					classChange = true;
				pixel.setClassNumber(closestClass);
			}
		}
		return classChange;
	}

	public Characteristic[][] getPixelProperties() {
		return pixelProperties;
	}

	public Characteristic[] getClassMeans() {
		return means;
	}
}