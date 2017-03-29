import java.awt.Color;
import java.io.IOException;

/**
 * Class that is responsible for reading images, feeding them into Kmeans, and
 * writing output images based on kMean classes.
 * 
 * @author Jack Steketee, Mike Eckmann
 */
public class ClusterImage {

	static Color[] colors = new Color[] { Color.blue, Color.cyan, Color.darkGray, Color.gray, Color.green,
			Color.magenta, Color.orange, Color.pink, Color.red, Color.white, Color.black, Color.yellow, Color.BLUE,
			Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.RED };
	static RGBImage inputImg;
	static KMeans km;
	static String imageName;
	static int K;

	public static void main(String args[]) throws IOException {

		imageName = args[0];
		K = Integer.parseInt(args[1]);
		KMeansType type = KMeansType.values()[Integer.parseInt(args[2])];
		if (K > colors.length && type.equals(KMeansType.INTENSITY_AND_TEXTURE))
			throw new IllegalArgumentException("Not enough colors to show that many classes");

		inputImg = new RGBImage(imageName);

		km = new KMeans(K, inputImg.getNumRows(), inputImg.getNumCols(), type);

		assignCharacteristics();
		km.runKmeans();
		recolor();
		outputImage();
	}

	private static void outputImage() throws IOException {
		switch (km.type) {
		case RGB_DISTANCE:
			inputImg.writeImage("RGBClustered_K=" + K + "_" + imageName);
			break;
		case RGB_AND_SPATIAL:
			inputImg.writeImage("RGBAndSpatial_K=" + K + "_" + imageName);
			break;
		case INTENSITY_AND_TEXTURE:
			inputImg.writeImage("IntensityAndTexture_K=" + K + "_" + imageName);
			break;
		case INTENSITY:
			inputImg.writeImage("Intensity_K=" + K + "_" + imageName);
			break;
		}
	}

	private static void assignCharacteristics() {
		RGBPixel pixel;
		switch (km.type) {
		case RGB_DISTANCE:
			double[] RGBVals = new double[3];
			for (int r = 0; r < inputImg.getNumRows(); r++) {
				for (int c = 0; c < inputImg.getNumCols(); c++) {
					pixel = inputImg.getPixel(r, c);
					RGBVals[0] = (double) pixel.getRed() / 255;
					RGBVals[1] = (double) pixel.getGreen() / 255;
					RGBVals[2] = (double) pixel.getBlue() / 255;
					km.assignPixelProperties(r, c, RGBVals);
				}
			}
			break;
		case RGB_AND_SPATIAL:
			double[] RGBRowColVals = new double[5];
			for (int r = 0; r < inputImg.getNumRows(); r++) {
				for (int c = 0; c < inputImg.getNumCols(); c++) {
					pixel = inputImg.getPixel(r, c);
					RGBRowColVals[0] = (double) pixel.getRed() / 255;
					RGBRowColVals[1] = (double) pixel.getGreen() / 255;
					RGBRowColVals[2] = (double) pixel.getBlue() / 255;
					RGBRowColVals[3] = (double) r / inputImg.getHeight();
					RGBRowColVals[4] = (double) c / inputImg.getWidth();
					km.assignPixelProperties(r, c, RGBRowColVals);
				}
			}
			break;
		case INTENSITY_AND_TEXTURE:
			double[] IntensityTexture = new double[2];
			int num;
			double[] neighborhood;
			for (int r = 0; r < inputImg.getNumRows(); r++) {
				for (int c = 0; c < inputImg.getNumCols(); c++) {
					pixel = inputImg.getPixel(r, c);
					double RGBSum = pixel.getBlue() + pixel.getRed() + pixel.getGreen();
					IntensityTexture[0] = RGBSum / ((double) 255 * 3);
					num = 0;
					neighborhood = new double[25];
					for (int i = r - 2; i <= r + 2; i++) {
						for (int j = c - 2; j <= c + 2; j++) {
							if (i >= 0 && i < inputImg.getHeight() && j >= 0 && j < inputImg.getWidth()) {
								neighborhood[num] = inputImg.getPixel(i, j).getRed() + inputImg.getPixel(i, j).getBlue()
										+ inputImg.getPixel(i, j).getGreen();
								num++;
							}
						}
					}
					IntensityTexture[1] = calcNormalizedStandardDev(neighborhood, num);
					km.assignPixelProperties(r, c, IntensityTexture);
				}
			}
			break;
		case INTENSITY:
			for (int r = 0; r < inputImg.getNumRows(); r++) {
				for (int c = 0; c < inputImg.getNumCols(); c++) {
					pixel = inputImg.getPixel(r, c);
					double RGBSum = pixel.getBlue() + pixel.getRed() + pixel.getGreen();
					km.assignPixelProperties(r, c, new double[] { RGBSum / ((double) 255 * 3) });
				}
			}
		}
	}

	private static void recolor() {
		if (km.type.equals(KMeansType.RGB_DISTANCE) || km.type.equals(KMeansType.RGB_AND_SPATIAL)) {
			Characteristic[][] pixelClasses = km.getPixelProperties();
			Characteristic[] means = km.getClassMeans();
			int classNum;
			for (int r = 0; r < inputImg.getNumRows(); r++) {
				for (int c = 0; c < inputImg.getNumCols(); c++) {
					classNum = pixelClasses[r][c].getClassNumber();
					inputImg.setPixel(r, c, (int) (means[classNum].getVal(0) * 255),
							(int) (means[classNum].getVal(1) * 255), (int) (means[classNum].getVal(2) * 255));
				}
			}
		}
		if (km.type.equals(KMeansType.INTENSITY_AND_TEXTURE)) {
			Characteristic[][] pixelClasses = km.getPixelProperties();
			int classNum;
			Color color;
			for (int r = 0; r < inputImg.getNumRows(); r++) {
				for (int c = 0; c < inputImg.getNumCols(); c++) {
					classNum = pixelClasses[r][c].getClassNumber();
					color = colors[classNum];
					inputImg.setPixel(r, c, color.getRed(), color.getGreen(), color.getBlue());
				}
			}
		}
		if (km.type.equals(KMeansType.INTENSITY)) {
			Characteristic[][] pixelClasses = km.getPixelProperties();
			Characteristic[] means = km.getClassMeans();
			int classNum;
			for (int r = 0; r < inputImg.getNumRows(); r++) {
				for (int c = 0; c < inputImg.getNumCols(); c++) {
					classNum = pixelClasses[r][c].getClassNumber();
					int averageVal = (int) (means[classNum].getVal(0) * 255.0);
					inputImg.setPixel(r, c, averageVal, averageVal, averageVal);
				}
			}
		}
	}

	private static double calcStandardDev(double[] nums) {
		double average = 0;
		for (double i : nums)
			average += i;
		average = average / nums.length;
		double sum = 0;
		for (double i : nums)
			sum += (average - i) * (average - i);
		double meanSum = sum / nums.length;
		double standardDev = Math.sqrt(meanSum);
		return standardDev;
	}

	private static double calcNormalizedStandardDev(double[] n, int numOfMembers) {
		double[] nums = new double[numOfMembers];
		double[] maxDev = new double[numOfMembers];
		for (int i = 0; i < numOfMembers; i++)
			nums[i] = n[i];
		for (int i = 0; i < numOfMembers / 2; i++)
			maxDev[i] = (255 * 3);
		for (int i = numOfMembers / 2; i < numOfMembers; i++)
			maxDev[i] = 0;
		return calcStandardDev(nums) / calcStandardDev(maxDev);
	}
}
