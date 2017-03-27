
/*

	Author: Michael Eckmann
	Skidmore College
	for Spring 2017
	Digital Image Processing Course


*/
import java.io.*;

public class Colorize {

	public static void main(String args[]) throws IOException {

		/*
		 * RGBImage colorInput = new RGBImage(args[0]); RGBImage grayInput = new
		 * RGBImage(args[1]); LABImage colorLABInput = new LABImage(colorInput);
		 * LABImage grayLABInput = new LABImage(grayInput); // compute the mean
		 * and standard deviation of the luminace of each image
		 * colorLABInput.computeLuminanceMeanAndStdDev();
		 * grayLABInput.computeLuminanceMeanAndStdDev(); // only remap the
		 * luminances of the source to match the distribution of the target
		 * colorLABInput.remapLuminances(grayLABInput); // remap the luminances
		 * of the color (to match the distribution of the grayscale) // create
		 * random samples of the color image (and save this image for debugging)
		 * RGBImage samplesImage =
		 * colorLABInput.createRandomJitSamplesRC(25,21);
		 * samplesImage.writeImage("samplesImage" + args[0]); // colorize the
		 * grayscale image based on the color image
		 * grayLABInput.colorize(colorLABInput); RGBImage colorizedRGB =
		 * grayLABInput.createRGBFromLAB(); colorizedRGB.writeImage("colorized-"
		 * + args[1]);
		 */

		RGBImage colorInput = new RGBImage(args[0]);
		RGBImage grayInput = new RGBImage(args[1]);

		int K = 20;
		KMeans kmGrey = new KMeans(K, grayInput.getNumRows(), grayInput.getNumCols(), KMeansType.INTENSITY_AND_TEXTURE);
		KMeans kmColor = new KMeans(K, colorInput.getNumRows(), colorInput.getNumCols(),
				KMeansType.INTENSITY_AND_TEXTURE);
		assignTextureCharacteristicVals(kmGrey, grayInput);
		assignTextureCharacteristicVals(kmColor, colorInput);
		kmGrey.runKmeans();
		kmColor.runKmeans();

		// map groups to one another.
		Characteristic[] greyMeans = kmGrey.getClassMeans();
		Characteristic[] colorMeans = kmColor.getClassMeans();

		// Contains the class in color whos mean values are most similar.
		int[] mapGreyToColor = new int[K];
		double minDistance;
		double curDistance;
		int closestClass = 0;
		for (int i = 0; i < K; i++) {
			minDistance = Integer.MAX_VALUE;
			for (int j = 0; j < K; j++) {
				curDistance = greyMeans[i].RGBDistance(colorMeans[j]);
				if (curDistance < minDistance) {
					minDistance = curDistance;
					closestClass = j;
				}
			}
			mapGreyToColor[i] = closestClass;
		}

		long[] bluePalette = new long[K];
		long[] redPalette = new long[K];
		long[] greenPalette = new long[K];
		RGBPixel[] palette = new RGBPixel[K];

		int[] classCount = new int[K];
		RGBPixel curPixel;
		int curClass;
		for (int r = 0; r < colorInput.getHeight(); r++) {
			for (int c = 0; c < colorInput.getWidth(); c++) {
				curPixel = colorInput.getPixel(r, c);
				curClass = kmColor.getPixelProperties()[r][c].getClassNumber();
				classCount[curClass]++;
				bluePalette[curClass] += curPixel.getBlue();
				greenPalette[curClass] += curPixel.getGreen();
				redPalette[curClass] += curPixel.getRed();
			}
		}
		for (int i = 0; i < K; i++) {
			System.out.println("RGB=" + redPalette[i] + " , " + greenPalette[i] + " , " + bluePalette[i]);
		}
		for (int k = 0; k < K; k++) {
			palette[k] = new RGBPixel(0,0,0);
			if (classCount[k] != 0) {
				palette[k].setBlue((int) (bluePalette[k] / (double) classCount[k]));
				palette[k].setGreen((int) (greenPalette[k]/ (double) classCount[k]));
				palette[k].setRed((int) (redPalette[k] / (double) classCount[k]));
			}

		}
		for (RGBPixel p : palette) {
			System.out.println("RGB=" + p.getRed() + " , " + p.getGreen() + " , " + p.getBlue());
		}
		int greyClass = 0;
		int colorClass = 0;
		for (int r = 0; r < grayInput.getHeight(); r++) {
			for (int c = 0; c < grayInput.getWidth(); c++) {
				greyClass = kmGrey.getPixelProperties()[r][c].getClassNumber();
				colorClass = mapGreyToColor[greyClass];
				grayInput.setPixel(r, c, palette[colorClass]);
			}
		}

		grayInput.writeImage("Colorized_K="+K+"_" + args[1]);
	}

	private static void assignTextureCharacteristicVals(KMeans km, RGBImage inputImg) {
		RGBPixel pixel;
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
