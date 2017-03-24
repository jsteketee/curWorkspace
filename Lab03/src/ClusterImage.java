import java.io.IOException;

/**
 * Class that is responsible for reading images, feeding them into Kmeans, and
 * writing output images based on kMean classes.
 * 
 * @author Jack Steketee, Mike Eckmann
 */
public class ClusterImage {
	public static void main(String args[]) throws IOException  {

		
		String imageName = args[0];
		int K = Integer.parseInt(args[1]);
		System.out.println("KMeans Algorithm");
		System.out.println("Image Name: "+imageName);
		RGBImage inputImg = new RGBImage(imageName);

		
		KMeans km = new KMeans(K, 3, inputImg.getNumRows(), inputImg.getNumCols(), KMeansType.RGBEuclidianDistance);

		double[] RGBVals = new double[3];
		for (int r = 0; r < inputImg.getNumRows(); r++) {
			for (int c = 0; c < inputImg.getNumCols(); c++) {
				RGBVals[0] = inputImg.getPixel(r, c).getRed();
				RGBVals[1] = inputImg.getPixel(r, c).getGreen();
				RGBVals[2] = inputImg.getPixel(r, c).getBlue();
				km.assignPixelProperties(r, c, RGBVals);
			}
		}

		km.runKmeans();
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
		
		inputImg.writeImage("RGBClustered"+imageName);
		
	}
}