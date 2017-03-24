
/**
 * Represents the attributes associated with a pixel, the Kmeans class that a
 * pixel is in, and a method to calculate the Kmeans distances between two
 * pixels.
 * 
 * @author Jack Steketee
 *
 */
public class Characteristic {

	// Array of doubles containing a value for each pixel attribute.
	private double[] attributes;

	// Number denoting which K means class the pixel is in.
	private int classNumber;

	/**
	 * Constructs a characteristic object with the appropriate number of
	 * attribute values.
	 * 
	 * @param numDims The number of attributes associated with each pixel.
	 */
	public Characteristic(int numDims) {
		attributes = new double[numDims];
	}

	/**
	 * Adds the attribute values from one characteristic object to this one.
	 * 
	 * @param c The characteristic who's attribute values will be added.
	 */
	public void addValues(Characteristic c) {
		for (int i = 0; i < attributes.length; i++) {
			attributes[i] += c.attributes[i];
		}
	}

	/**
	 * Assigns the attribute values for this pixel.
	 * 
	 * @param vals an array of value to be copied.
	 */
	public void setValues(double[] vals) {
		for (int i = 0; i < vals.length; i++) {
			attributes[i] = vals[i];
		}
	}

	/**
	 * Retrieves a given attribute value.
	 * 
	 * @param idx The idx of the attribute value.
	 * @return The attribute value at the given idx.
	 */
	public double getVal(int idx) {
		return attributes[idx];
	}

	public void setVal(int idx, double value) {
		attributes[idx] = value;
	}

	public void setClassNumber(int cN) {
		classNumber = cN;
	}

	public int getClassNumber() {
		return classNumber;
	}

	/**
	 * Calculates the Normalized Euclidean distance between the two
	 * characteristics. ie. SquareRoot((x1-y1)^2+(x2-y2)^2...(xn-yn)^2)
	 * 
	 * @param c2 The characteristic of the pixel being compared with this
	 *            Characteristic.
	 * @return the normalized distance.
	 */
	public double UDistance(Characteristic c2) {
		double maxDistance = Math.sqrt(255 * 255 * this.attributes.length);
		double difference;
		double sumOfSquares = 0;
		for (int i = 0; i < this.attributes.length; i++) {
			difference = (this.attributes[i] - c2.attributes[i]);
			sumOfSquares += difference * difference;
		}
		return Math.sqrt(sumOfSquares) / maxDistance;
	}
}
