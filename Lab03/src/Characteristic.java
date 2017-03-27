
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
	 * @param c2 The pixel being compared to this.
	 * @return the normalized distance.
	 */
	public double RGBDistance(Characteristic c2) {
		double maxDistance = this.attributes.length;
		double difference;
		double sumOfSquares = 0;
		for (int i = 0; i < this.attributes.length; i++) {
			difference = (this.attributes[i] - c2.attributes[i]);
			sumOfSquares += difference * difference;
		}
		return sumOfSquares / maxDistance;
	}

	private double RGBSpatialDistance(Characteristic c2, int maxr, int maxc) {
		double difference;
		double sumOfSquares = 0;
		for (int i = 0; i < this.attributes.length; i++) {
			difference = (this.attributes[i] - c2.attributes[i]);
			sumOfSquares += difference * difference;
		}
		sumOfSquares += .2 * ((this.attributes[3] - c2.attributes[3]) * (this.attributes[3] - c2.attributes[3]))
				+ .2 * ((this.attributes[4] - c2.attributes[4]) * (this.attributes[4] - c2.attributes[4]));

		sumOfSquares = sumOfSquares / 7;
		return sumOfSquares;
	}

	public double Distance(int r, int c, int maxr, int maxc, Characteristic c2, KMeansType type) {
		switch (type) {
		case RGB_DISTANCE:
			return RGBDistance(c2);
		case RGB_AND_SPATIAL:
			return RGBSpatialDistance(c2, maxr, maxc);
		case INTENSITY_AND_TEXTURE:
			return RGBDistance(c2);
		case INTENSITY:
			return (RGBDistance(c2));
		}
		return 0;
	}

}
