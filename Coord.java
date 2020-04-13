import java.util.ArrayList;

public class Coord{
	ArrayList<Double> x;
	int cluster;
	ArrayList<Double> distanceToCentroid;
	public Coord(ArrayList<Double> x, int cluster){
		this.x = x;
		this.cluster = cluster;
		this.distanceToCentroid = new ArrayList<Double>();
	}
	
}