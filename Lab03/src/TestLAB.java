/*

	Author: Michael Eckmann
	Skidmore College
	for Spring 2017
	Digital Image Processing Course


*/
import java.io.*;

public class TestLAB
{

	public static void main(String args[]) throws IOException
	{
		RGBImage inputImg = new RGBImage(args[0]);
		System.out.println("first pixel r,g,b = " + inputImg.getPixel(0,0).getRed() + " " + inputImg.getPixel(0,0).getGreen() + " " + inputImg.getPixel(0,0).getBlue() + " " );
		System.out.println("second pixel r,g,b = " + inputImg.getPixel(0,1).getRed() + " " + inputImg.getPixel(0,1).getGreen() + " " + inputImg.getPixel(0,1).getBlue() + " " );
		LABImage inputImgLAB = new LABImage(inputImg);

		RGBImage outImg = inputImgLAB.createRGBFromLAB();
		outImg.writeImage("convertedBackAndForthLAB" + args[0]);
		System.out.println("# pixels converted = " + LABPixel.count);
		System.out.println("# pixels have r,g or b < 0 = " + LABPixel.count0);
		System.out.println("# pixels have r,g or b > 255 = " + LABPixel.count255);
	

	}
}

