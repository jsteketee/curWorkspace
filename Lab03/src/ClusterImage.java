import java.awt.Color;
import java.io.IOException;

/**
 * Class that is responsible for reading images, feeding them into Kmeans, and
 * writing output images based on kMean classes.
 * 
 * @author Jack Steketee, Mike Eckmann
 */
public class ClusterImage {
	public static void main(String args[]) throws IOException {

		String imageName = args[0];
		int K = Integer.parseInt(args[1]);
		KMeansType type = KMeansType.values()[Integer.parseInt(args[2])];

		RGBImage inputImg = new RGBImage(imageName);

		KMeans km = new KMeans(K, inputImg.getNumRows(), inputImg.getNumCols(), type);

		assignCharacteristics(km, inputImg, type);
		km.runKmeans();
		recolor(km, inputImg, type);
		outputImage(inputImg, type, K, imageName);
	}

	private static void outputImage(RGBImage inputImg, KMeansType type, int K, String imageName) throws IOException {
		switch (type) {
		case RGB_Distance:
			inputImg.writeImage("RGBClustered_K=" + K + "_" + imageName);
			break;
		case RGBSpatialDistance:
			inputImg.writeImage("RGBSpatialDistance_K=" + K + "_" + imageName);
			break;
		case Intensity:
			break;
		case TextureAndIntensity:
			break;
		default:
			break;
		}
	}

	private static void assignCharacteristics(KMeans km, RGBImage inputImg, KMeansType type) {
		if (type.equals(KMeansType.RGB_Distance) || type.equals(KMeansType.RGBSpatialDistance)) {
			RGBPixel pixel;
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
		}
	}

	private static void recolor(KMeans km, RGBImage inputImg, KMeansType type) {
		if (type.equals(KMeansType.RGB_Distance) || type.equals(KMeansType.RGBSpatialDistance)) {
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
	}
}
