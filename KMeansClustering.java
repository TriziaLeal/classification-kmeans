import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class KMeansClustering{
	int k;
	ArrayList<Coord> trainingData;
	ArrayList<Coord> centroid; 
	public KMeansClustering(){
		k = 0;
		trainingData = new ArrayList<Coord>();
		centroid = new ArrayList<Coord>();
	}

	void proper(){
		String input = readFile();
		ArrayList<Integer> prevCluster;
		ArrayList<Coord> prevCentroid;
		int iter = 1;
		convertTraining(input);
		ArrayList<Integer> centroidIndex = randomizeCentroid();
		initCentroid(centroidIndex);
		do{
			printTrainingData(centroid);
			writeFile(toStringCentroid(iter++));
			getDistance();
			prevCluster = getPrevCluster();
			assignCluster();
			// printTrainingData(this.trainingData);
			prevCentroid = getPrevCentroid();
			// printTrainingData(prevCentroid);
			this.centroid = newCentroid();
		}while(!(isSameCluster(prevCluster)) && !(isSameCentroid(prevCentroid)));

		printTrainingData(this.centroid);
		writeFile(toStringCentroid(iter++));

	}

	ArrayList<Integer> randomizeCentroid(){
		ArrayList<Integer> centroidIndex = new ArrayList<Integer>();
		Random rand = new Random();
		int  index = rand.nextInt(this.trainingData.size());

		centroidIndex.add(index);
		while(centroidIndex.size() != this.k){
			index = rand.nextInt(this.trainingData.size());
			if (!centroidExist(centroidIndex,index))
				centroidIndex.add(index);
		}
		// centroidIndex = new ArrayList<Integer>();
		// centroidIndex.add(0);
		// centroidIndex.add(6);
		return centroidIndex;
	}

	void initCentroid(ArrayList<Integer> centroidIndex){
		int count = 1;

		for (int i: centroidIndex){
			this.centroid.add(new Coord(this.trainingData.get(i).x,count++));
		}
	}

	double euclidianDistance(ArrayList<Double> c1, ArrayList<Double> c2){
		double distance = 0;
		for(int i = 0; i < c1.size(); i++){
			distance += (c1.get(i) - c2.get(i))*(c1.get(i) - c2.get(i));
		}
		return Math.sqrt(distance);
	}

	void getDistance(){
		for (Coord td: this.trainingData){
			td.distanceToCentroid = new ArrayList<Double>();
			for(Coord c: centroid){
				td.distanceToCentroid.add(euclidianDistance(td.x,c.x));
			}
		}
	}

	void assignCluster(){
		double min = -1;
		for (Coord td: this.trainingData){
			min = td.distanceToCentroid.get(0);
			for(int i = 1; i < td.distanceToCentroid.size(); i++){
				if (td.distanceToCentroid.get(i) < min)
					min = td.distanceToCentroid.get(i);
			}
			td.cluster = td.distanceToCentroid.indexOf(min)+1;
		}
	}
	boolean centroidExist(ArrayList<Integer> centroidIndex, int index){
		for (int i: centroidIndex){
			if (i == index)
				return true;
		}
		return false;
	}

	boolean isSameCluster(ArrayList<Integer> prevCluster){
		for (int i = 0; i < prevCluster.size(); i++){
			if (prevCluster.get(i) != this.trainingData.get(i).cluster)
				return false;
		}
		return true;
	}

	boolean isSameCentroid(ArrayList<Coord> prevCentroid){
		for (int i = 0; i < prevCentroid.size(); i++){
			for (int j = 0; j < prevCentroid.get(i).x.size(); j++){
				if (prevCentroid.get(i).x.get(j) - this.centroid.get(i).x.get(j) != 0){
					return false;
				}
			}
		}
		return true;
	}

	ArrayList<Coord> getPrevCentroid(){
		ArrayList<Coord> prevCentroid = new ArrayList<Coord>();
		Coord centroid;
		ArrayList<Double> centroidCoord;
		for (int i = 0; i < this.centroid.size(); i++){
			centroidCoord = new ArrayList<Double>();
			for (Double x : this.centroid.get(i).x){
				centroidCoord.add(x);
			}
			centroid = new Coord(centroidCoord,i+1);
			prevCentroid.add(centroid);
		}
		return prevCentroid;
	}

	ArrayList<Coord> newCentroid(){
		ArrayList<Coord> newCentroid = new ArrayList<Coord>();
		double count = 0;
		double sum = 0;
		for (Coord c: this.centroid){
			count = 0;
			ArrayList<Double> initCoord = initCoord();
			Coord newCoord = new Coord(initCoord,c.cluster);
			for (Coord td: this.trainingData){
				if (c.cluster == td.cluster){
					count++;
					for (int i = 0 ; i < td.x.size(); i++){
						sum = newCoord.x.get(i)+td.x.get(i);
						newCoord.x.set(i,sum);
					}

				}
			}
			for (int i = 0 ; i < newCoord.x.size(); i++){
				newCoord.x.set(i,newCoord.x.get(i)/count);
			}
			newCentroid.add(newCoord);
		}
		return newCentroid;
	}

	ArrayList<Double> initCoord(){
		ArrayList<Double> initCoord = new ArrayList<Double>();
		while (initCoord.size() != this.trainingData.get(0).x.size()){
			initCoord.add(0.0);
		}
		return initCoord;
	}	


	String readFile(){
		StringBuffer stringBuffer = new StringBuffer();
		try {
			File file = new File("input2.txt");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
				stringBuffer.append("\n");
			}
			fileReader.close();
		} catch (IOException e) {
			e.getMessage();
		}
		return stringBuffer.toString();
	}

	public void writeFile(String newText){
		FileWriter fw = null;
		BufferedWriter bw = null;
		//creates file
		try{    
        	fw=new FileWriter("output.txt",true);
        	bw = new BufferedWriter(fw);
        	bw.write(newText);    
        	// fw.close();    
        }catch(Exception e){System.out.println(e);
        } finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {System.out.println(ex);
			}

		}	
	}

	String toStringCentroid(int iter){
		String stringedCentroid = "Iteration " + iter +"\n";
		for (Coord c: this.centroid){
			stringedCentroid +="\tCentroid " + c.cluster + ":\t"+c.x + "\n";	
		}
		return stringedCentroid;
	}
	ArrayList<Integer> getPrevCluster(){
		ArrayList<Integer> prevCluster = new ArrayList<Integer>();
		for (Coord td: this.trainingData){
			prevCluster.add(td.cluster);
		}

		return prevCluster;
	}

	void convertTraining(String s){
		String[] string = s.split("\n");
		ArrayList<Double> coord;
		int c;
		this.k = Integer.parseInt(string[0]);
		for (int j = 1; j < string.length; j++){
			coord = new ArrayList<Double>();
			String[] elements = string[j].split(" ");
			for(int i = 0; i < elements.length; i++){
				coord.add(Double.parseDouble(elements[i]));
			}
			Coord coordinates = new Coord(coord,0);
			this.trainingData.add(coordinates);
		}
	}
	
	void printTrainingData(ArrayList<Coord> td){
		for (Coord d: td){
			for (Double c: d.x){
				System.out.printf("%.2f",c);
				System.out.print("\t");
			}
			for (Double distance: d.distanceToCentroid){
				System.out.printf("%.2f", distance);
				System.out.print("\t");
			}
			System.out.println(d.cluster);
		}
	}

}