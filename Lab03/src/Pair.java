public class Pair {

    private int row;
    private int col;
    private double stddev;
    public int count;

    public Pair(int r, int c, LABImage img) {
        this.row = r;
        this.col = c;
	stddev = img.getStdDevOfRemappedLum(r,c,5); // uses a 5x5 window
	count = 0;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public double getStdDev() {
        return stddev;
    }

}

