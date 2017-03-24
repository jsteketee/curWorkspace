public class LABImage
{

	private LABPixel[][] pic;
	private double stddev_l;
	private double mean_l;
	private double[][] remappedLuminances; // only for the (source) color image for the colorize code
	private Pair[] randomJitSamplesRC;

	private static final double epsilon = 0.0000000001;

	public LABImage(int height, int width)
	{
		// height = # rows
		// width = # cols

		pic = new LABPixel[height][width];
	}


	public LABImage(RGBImage rgbImg)
	{
		pic = new LABPixel[rgbImg.getNumRows()][rgbImg.getNumCols()];
		for (int r = 0; r < rgbImg.getNumRows(); r++)
		{
			for (int c = 0; c < rgbImg.getNumCols(); c++)
			{
		
			pic[r][c] = convertRGBToLAB(rgbImg.getPixel(r,c));

			}
		}

	}

	private LABPixel convertRGBToLAB(RGBPixel rgbP)
	{
		
		double l, alpha, beta;
		
		double rd = rgbP.getRed() + epsilon, gd = rgbP.getGreen() + epsilon, bd = rgbP.getBlue() + epsilon;
		
		double L,M,S;
		L = 0.3811 * rd + 0.5783 * gd + 0.0402 * bd;
		M = 0.1967 * rd + 0.7244 * gd + 0.0782 * bd;
		S = 0.0241 * rd + 0.1288 * gd + 0.8444 * bd;

		L = Math.log10(L);
		M = Math.log10(M);
		S = Math.log10(S);
		double oneOverSqr3 = 1.0 / Math.sqrt(3);
		double oneOverSqr6 = 1.0 / Math.sqrt(6);
		double oneOverSqr2 = 1.0 / Math.sqrt(2);
		l = oneOverSqr3*L + oneOverSqr3*M + oneOverSqr3*S;
		alpha = oneOverSqr6*L + oneOverSqr6*M + (-2)*oneOverSqr6*S;
		beta = oneOverSqr2*L - oneOverSqr2*M;

		return new LABPixel(l,alpha,beta);
	}



	public void setPixel(int r, int c, LABPixel p)
	{
		pic[r][c] = p;
	}

	public void setPixel(int r, int c, double l, double a, double b)
	{
		pic[r][c] = new LABPixel(l,a,b);
	}

	public LABPixel getPixel(int r, int c)
	{
		return pic[r][c];
	}

	// third parameter is width and heigh of window
	public double getStdDevOfActualLum(int r, int c, int width)
	{
		int halfWidth = width/2;
		int startR = r - halfWidth;
		int startC = c - halfWidth;
		double sum = 0;

		for (int i=startR; i <= r + halfWidth; i++)
		{
			for (int j=startC; j <= c + halfWidth; j++)
			{
				sum += pic[i][j].getL();
			}
		}

		double mean = sum / (width * width);

		double sum2 = 0;
		for (int i=startR; i <= r + halfWidth; i++)
		{
			for (int j=startC; j <= c + halfWidth; j++)
			{
				sum2 += Math.pow((pic[i][j].getL() - mean),2);
			}
		}
		sum2 = sum2 / (width * width);
		return Math.sqrt(sum2);
	}

	// third parameter is width and heigh of window
	public double getStdDevOfRemappedLum(int r, int c, int width)
	{
		int halfWidth = width/2;
		int startR = r - halfWidth;
		int startC = c - halfWidth;
		double sum = 0;

		for (int i=startR; i <= r + halfWidth; i++)
		{
			for (int j=startC; j <= c + halfWidth; j++)
			{
				sum += remappedLuminances[i][j];
			}
		}

		double mean = sum / (width * width);

		double sum2 = 0;
		for (int i=startR; i <= r + halfWidth; i++)
		{
			for (int j=startC; j <= c + halfWidth; j++)
			{
				sum2 += Math.pow((remappedLuminances[i][j] - mean),2);
			}
		}
		sum2 = sum2 / (width * width);
		return Math.sqrt(sum2);
	}


	public Pair bestMatch(double lu, double nbrhdStdDev, LABImage colImg)
	{
		double least = Double.MAX_VALUE;
		int bestPairIdx = 0;
		for (int  i=0; i<colImg.randomJitSamplesRC.length; i++)
		{
			double luCol = colImg.remappedLuminances[colImg.randomJitSamplesRC[i].getRow()][colImg.randomJitSamplesRC[i].getCol()]; 

			// NOTE: if the luminance range is different than the stddev of luminance range, this has the effect of
			/// weighting the distance more by the one with larger range
			double dist = Math.pow(lu - luCol, 2) + Math.pow(nbrhdStdDev - colImg.randomJitSamplesRC[i].getStdDev(),2);
			if (dist < least)
			{
				least = dist;
				bestPairIdx = i;
				
			}
		}
		colImg.randomJitSamplesRC[bestPairIdx].count++;
		return colImg.randomJitSamplesRC[bestPairIdx];
	}


	public void colorize(LABImage colorImg)
	{
		// colorImg has randomJitSamplesRC already created
		
		// colorize will be called on the grayscale image

		int halfHeight = 3, halfWidth = 3;
		for (int r=halfHeight; r < getNumRows() - halfHeight; r++)
		{
			for (int c=halfWidth; c < getNumCols() - halfWidth; c++)
			{
				double lum = pic[r][c].getL(); 
				double neighborhoodStdDev = getStdDevOfActualLum(r,c,5); // uses a 5x5 window
				Pair bestM = bestMatch(lum,neighborhoodStdDev, colorImg);
				pic[r][c].setA(colorImg.getPixel(bestM.getRow(),bestM.getCol()).getA());
				pic[r][c].setB(colorImg.getPixel(bestM.getRow(),bestM.getCol()).getB());
			}
		}

		// see how many times each pair was used for debugging purposes
		for (int i=0; i <colorImg.randomJitSamplesRC.length; i++)
		{
			System.out.println("i = " + i + " num times used = " + colorImg.randomJitSamplesRC[i].count);
			System.out.println("r, c of random sample: " + colorImg.randomJitSamplesRC[i].getRow() + ", " + colorImg.randomJitSamplesRC[i].getCol());
			System.out.println("remapped luminance of random sample = " + colorImg.remappedLuminances[colorImg.randomJitSamplesRC[i].getRow()][colorImg.randomJitSamplesRC[i].getCol()]);
			System.out.println("stdev of neighborhood of remapped luminances of random sample = " + colorImg.randomJitSamplesRC[i].getStdDev());
		}


	}

	public RGBImage createRandomJitSamplesRC(int rows, int cols)
	{
		RGBImage outRGB = new RGBImage(getNumRows(), getNumCols());
		for (int r=0; r< outRGB.getNumRows(); r++)
		{
			for (int c=0; c< outRGB.getNumCols(); c++)
			{
				outRGB.setPixel(r,c,127,127,127);
			}
		}

		double minL=100000, maxL=-100000, meanL, sumL=0;
		double minsdL=100000, maxsdL=-100000, meansdL, sumsdL=0;

		randomJitSamplesRC = new Pair[rows*cols];
		int numRowsInBox = (int)((double)getNumRows() / rows);
		int numColsInBox = (int)((double)getNumCols() / cols);
		int randRowOffset;
		int randColOffset;
		int topLeftR;
		int topLeftC;
		for (int i=0; i<randomJitSamplesRC.length; i++)
		{
			topLeftR = (i / cols)*numRowsInBox;
			topLeftC = (i % cols)*numColsInBox;
			randRowOffset = (int)(Math.random()*numRowsInBox);
			randColOffset = (int)(Math.random()*numColsInBox);
			while (topLeftR + randRowOffset < 4 || topLeftR + randRowOffset > (getNumRows() -4))
				randRowOffset = (int)(Math.random()*numRowsInBox);
			while (topLeftC + randColOffset < 4 || topLeftC + randColOffset > (getNumCols() -4))
				randColOffset = (int)(Math.random()*numColsInBox);
			randomJitSamplesRC[i] = new Pair(topLeftR + randRowOffset, topLeftC + randColOffset, this);
			outputNbrhood(outRGB, randomJitSamplesRC[i]);
			double thisL = pic[randomJitSamplesRC[i].getRow()][randomJitSamplesRC[i].getCol()].getL();
			sumL += thisL;
			if (thisL < minL) minL = thisL;
			if (thisL > maxL) maxL = thisL;
			double thissdL = randomJitSamplesRC[i].getStdDev();
			sumsdL += thissdL;
			if (thissdL < minsdL) minsdL = thissdL;
			if (thissdL > maxsdL) maxsdL = thissdL;
		}
		meanL = sumL / randomJitSamplesRC.length;
		meansdL = sumsdL / randomJitSamplesRC.length;
		System.out.println("minL = " + minL);
		System.out.println("maxL = " + maxL);
		System.out.println("meanL = " + meanL);

		System.out.println("minsdL = " + minsdL);
		System.out.println("maxsdL = " + maxsdL);
		System.out.println("meansdL = " + meansdL);
		return outRGB;
	}

	public void outputNbrhood(RGBImage outRGB, Pair sample)
	{
		int sR = sample.getRow() - 2;
		int sC = sample.getCol() - 2;
		for (int r = sR; r <= sample.getRow() + 2; r++)
		{
			for (int c = sC; c <= sample.getCol() + 2; c++)
			{
				outRGB.setPixel(r,c,pic[r][c].convertToRGB());
			}
		}

	}

	public void computeLuminanceMeanAndStdDev()
	{
		double sum = 0, sum2 = 0;
		for (int r=0; r < getNumRows(); r++)
		{
			for (int c=0; c < getNumCols(); c++)
			{
				double lum = pic[r][c].getL();
				sum += lum;
			}
		}
		this.mean_l = sum / (getNumRows() * getNumCols());
		
		for (int r=0; r < getNumRows(); r++)
		{
			for (int c=0; c < getNumCols(); c++)
			{
				double lum = pic[r][c].getL();
				sum2 += Math.pow((lum - mean_l),2);
			}
		}
		sum2 = sum2 / (getNumRows() * getNumCols());
		this.stddev_l = Math.sqrt(sum2);
		System.out.println("Mean Lum = " + mean_l);
		System.out.println("StdDev Lum = " + stddev_l);
	}

	// this is only called on the color image (with the grayscale image passed in as a parameter)
	public void remapLuminances(LABImage otherImg)
	{
		remappedLuminances = new double[getNumRows()][getNumCols()];
		for (int r=0; r < getNumRows(); r++)
		{
			for (int c=0; c < getNumCols(); c++)
			{
				remappedLuminances[r][c] = (otherImg.stddev_l / stddev_l)*(pic[r][c].getL() - mean_l) + otherImg.mean_l;
			}
		}
	}

	public RGBImage createRGBFromLAB()
	{
		RGBImage outRGB = new RGBImage(getNumRows(), getNumCols());
		for (int r=0; r < getNumRows(); r++)
		{
			for (int c=0; c < getNumCols(); c++)
			{
				outRGB.setPixel(r,c,pic[r][c].convertToRGB());
			}
		}
		return outRGB;
	}

	public int getNumRows()
	{
		return getHeight();
	}

	public int getNumCols()
	{
		return getWidth();
	}

	public int getHeight()
	{
		return pic.length;
	}

	public int getWidth()
	{
		return pic[0].length;
	}

}
