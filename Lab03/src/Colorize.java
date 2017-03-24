/*

	Author: Michael Eckmann
	Skidmore College
	for Spring 2017
	Digital Image Processing Course


*/
import java.io.*;

public class Colorize
{

	public static void main(String args[]) throws IOException
	{

		RGBImage colorInput = new RGBImage(args[0]);
		RGBImage grayInput = new RGBImage(args[1]);
		LABImage colorLABInput = new LABImage(colorInput);
		LABImage grayLABInput = new LABImage(grayInput);

		// compute the mean and standard deviation of the luminace of each image
		colorLABInput.computeLuminanceMeanAndStdDev();
		grayLABInput.computeLuminanceMeanAndStdDev();

		// only remap the luminances of the source to match the distribution of the target
		colorLABInput.remapLuminances(grayLABInput);  // remap the luminances of the color (to match the distribution of the grayscale)

		// create random samples of the color image (and save this image for debugging)
		RGBImage samplesImage = colorLABInput.createRandomJitSamplesRC(25,21);
		samplesImage.writeImage("samplesImage" + args[0]);

		// colorize the grayscale image based on the color image
		grayLABInput.colorize(colorLABInput);
		RGBImage colorizedRGB = grayLABInput.createRGBFromLAB();
		colorizedRGB.writeImage("colorized-" + args[1]);

	}
}
