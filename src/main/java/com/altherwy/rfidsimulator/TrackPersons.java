package com.altherwy.rfidsimulator;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;

public class TrackPersons {

	public AStarAlgorithm AStar, star;
	public RFIDSimulator RFID;
	public final String firstLink = "src\\main\\java\\com\\altherwy\\rfidsimulator\\docs\\testScenarios1.txt";
    public final String secondLink = "src\\main\\java\\com\\altherwy\\rfidsimulator\\docs\\testScenarios2.txt";
	public Vector<Vector<Integer>> RRs, MSs;

	public TrackPersons(Vector<Vector<Integer>> RRs, Vector<Vector<Integer>> MSs) {
		AStar = new AStarAlgorithm();
		RFID = new RFIDSimulator();
		this.RRs = RRs;
		this.MSs = MSs;
	}

	/*
	 * The tracing algorithm
	 */

	public Vector<Vector<Integer>> Track(ArrayList<Vector<Integer>> path,
			Vector<Vector<Integer>> RRs, Vector<Vector<Integer>> MSs,
			int personID) {

		Vector<Vector<Integer>> results = new Vector<Vector<Integer>>();

		for (int index = 0; index < path.size(); index++) {
			
            // Reading for Rs
			for (int j = 0; j < RRs.size(); j++) {
				if (isEqual(path.get(index), RRs.get(j))) {
					Vector<Integer> temp = new Vector<Integer>();
					temp.add(RRs.get(j).get(2));
					temp.add(RRs.get(j).get(3));
					temp.add(personID);
					results.add(temp);
					j = RRs.size();

				}
			}
			// Reading for MSs
			for (int j = 0; j < MSs.size(); j++) {
				if (isEqual(path.get(index), MSs.get(j))) {
					Vector<Integer> temp = new Vector<Integer>();
					temp.add(MSs.get(j).get(2));
					temp.add(MSs.get(j).get(3));
					results.add(temp);
					j = MSs.size();

				}
			}

		}
		return eliminationDuplicate(results);
	}

	/*
	 * True if some specific elements in vectors are the same , false otherwise
	 */

	public boolean isEqual(Vector<Integer> person, Vector<Integer> device) {
		int Prow = person.get(1);
		int Pcolumn = person.get(2);
		int Drow = device.get(0);
		int Dcolumn = device.get(1);

		if (Prow == Drow && Pcolumn == Dcolumn)
			return true;
		return false;
	}

	/*
	 * Eliminate duplicated results
	 */
	public Vector<Vector<Integer>> eliminationDuplicate(
			Vector<Vector<Integer>> resultsBefore) {
		Vector<Vector<Integer>> resultsAfter = new Vector<Vector<Integer>>();
		int i = 0;
		while (i < resultsBefore.size()) {
			if (i == resultsBefore.size() - 1) {
				resultsAfter.add(resultsBefore.get(i));
				return resultsAfter;
			}

			if (i == resultsBefore.size() - 2) {
				if (!resultsBefore.get(i).equals(resultsBefore.get(i + 1)))
					resultsAfter.add(resultsBefore.get(i));
			} else {
				if (!resultsBefore.get(i).equals(resultsBefore.get(i + 1))
						&& !resultsBefore.get(i).equals(
								resultsBefore.get(i + 2)))
					resultsAfter.add(resultsBefore.get(i));
			}
			i++;

		}
		return resultsAfter;
	}

	/*
	 * Return MSs from Tracking Results
	 */
	public Vector<Vector<Integer>> returnMSs(Vector<Vector<Integer>> results) {
		Vector<Vector<Integer>> MSs = new Vector<Vector<Integer>>();
		for (int i = 0; i < results.size(); i++) {
			if (results.get(i).size() == 2)
				MSs.add(results.get(i));
		}
		return MSs;
	}

	/*
	 * Return RRs from Tracking Results
	 */
	public Vector<Vector<Integer>> returnRRs(Vector<Vector<Integer>> results) {
		Vector<Vector<Integer>> RRs = new Vector<Vector<Integer>>();
		for (int i = 0; i < results.size(); i++) {
			if (results.get(i).size() == 3)
				RRs.add(results.get(i));
		}
		return RRs;
	}
	/*
	 * Return a vector containing the two persons tracking results 
	 */

	public Vector<Vector<Vector<Integer>>> allTrackingResults() {
		Vector<Vector<Vector<Integer>>> finalTracking = new Vector<Vector<Vector<Integer>>>();
		Vector<Vector<Integer>> firstScenario, secondScenarion;
		
		Vector<Vector<Integer>> PermenantPath = new Vector<Vector<Integer>>();
		Vector<Vector<Integer>> PermenantPath2 = new Vector<Vector<Integer>>();
		Vector<Vector<Integer>> RealPath1 = new Vector<Vector<Integer>>();
		Vector<Vector<Integer>> RealPath2 = new Vector<Vector<Integer>>();
		firstScenario = RFID.readFromFile(firstLink);
		secondScenarion = RFID.readFromFile(secondLink);
		/************************************************/
		for (int i = 0; i < firstScenario.size(); i++) {
			star=new AStarAlgorithm();
			ArrayList<Vector<Integer>> tempPath = new ArrayList<Vector<Integer>>();
			
			tempPath = star.findPath(firstScenario.get(i).get(0), firstScenario.get(i).get(1),
					firstScenario.get(i).get(2), firstScenario.get(i).get(3));
			tempPath =RFID.finalSolution(RFID.swap(tempPath), firstScenario.get(i).get(0), firstScenario.get(i).get(1));
			Vector<Vector<Integer>> temp = Track(tempPath, RRs, MSs, 5555);
			if(PermenantPath.size()!=0){
				if(temp.firstElement().equals(PermenantPath.lastElement()))
					temp.remove(0);
			}
			
			RealPath1.addAll(tempPath);
			PermenantPath.addAll(temp);
		}
		
		finalTracking.add(PermenantPath);
		
		for (int i = 0; i < secondScenarion.size(); i++) {
			star=new AStarAlgorithm();
			ArrayList<Vector<Integer>> tempPath = new ArrayList<Vector<Integer>>();
			
			tempPath = star.findPath(secondScenarion.get(i).get(0), secondScenarion.get(i).get(1),
					secondScenarion.get(i).get(2), secondScenarion.get(i).get(3));
			tempPath =RFID.finalSolution(RFID.swap(tempPath), secondScenarion.get(i).get(0), secondScenarion.get(i).get(1));
			Vector<Vector<Integer>> temp = Track(tempPath, RRs, MSs, 6666);
			if(PermenantPath2.size()!=0){
				if(temp.firstElement().equals(PermenantPath2.lastElement()))
					temp.remove(0);
			}
			
		    RealPath2.addAll(tempPath);
			PermenantPath2.addAll(temp);
		}
		finalTracking.add(PermenantPath2);
	
		
		Vector<Vector<Vector<Integer>>> TR =  matchingMStoPersons(finalTracking);
		System.out.println("The devices Reading and the assignment algorithm for the first person (Tracking Results)");
		for(Vector<Integer> item: PermenantPath){
			if (item.size() == 2)
				item.add(5555);
			if(item.size() == 4)
				item.remove(3);
			System.out.println(item);
		}
		System.out.println("The devices Reading and the assignment algorithm for the second person (Tracking Results)");
		for(Vector<Integer> item: PermenantPath2){
			if (item.size() == 2)
				item.add(6666);
			if(item.size() == 4)
				item.remove(3);
			System.out.println(item);
		}
		TR.add(printPath(PermenantPath));
		TR.add(printPath(PermenantPath2));
		TR.add(RealPath1);
		TR.add(RealPath2);
		return TR;
		
		//return finalTracking;
	   
	}
	/*
	 * Assign IDs to MSs reading 
	 */
	
	public Vector<Vector<Vector<Integer>>> matchingMStoPersons(Vector<Vector<Vector<Integer>>> tracking){
		Vector<Vector<Vector<Integer>>> finalResults = new Vector<Vector<Vector<Integer>>>();
		Vector<Vector<Integer>> first = new Vector<Vector<Integer>>();
		Vector<Vector<Integer>> second = new Vector<Vector<Integer>>();
		Vector<Vector<Integer>> newFirst = new Vector<Vector<Integer>>();
		Vector<Vector<Integer>> newSecond = new Vector<Vector<Integer>>();
		Vector<Integer> firstElement = new Vector<Integer>();
		Vector<Integer> firstElement2 = new Vector<Integer>();
		first.addAll(tracking.get(0));
		second.addAll(tracking.get(1));
		/************ Dirty Work ***********************/
		firstElement.add(15);
		firstElement.add(34);
		firstElement.add(5555);
		firstElement2.add(15);
		firstElement2.add(34);
		firstElement2.add(6666);

		newFirst.add(0,firstElement);
		newSecond.add(0,firstElement2);
		/************************************************/
		int size = -1;
		if(first.size() <= second.size())
			 size = first.size();
		else
			 size = second.size();
		
		for (int i=1; i< size; i++ ){
			if(first.get(i).size() == 2){
				Vector<Integer> temp = new Vector<Integer>();
				temp = resolveAmbiguity(i, first.get(i), first, second, size,0);
				if(temp.get(2).equals(5555) || temp.get(2).equals(0)){
					temp.remove(2);
					temp.add(2,5555);
					first.get(i).add(5555);
			       newFirst.add(temp);
				}
				else{
					newSecond.add(temp);
					second.get(i).add(6666);
				}
			}
			else
				newFirst.add(first.get(i));
			/*******************************/
			if(second.get(i).size() == 2){
				Vector<Integer> temp = new Vector<Integer>();
				temp = resolveAmbiguity(i, second.get(i), first, second, size,1);
				
				if(temp.get(2).equals(6666) || temp.get(2).equals(1)){
					second.get(i).add(6666);
					if(temp.size() >= 3){
					temp.remove(2);
					temp.add(2,6666);
			       newSecond.add(temp);
					}
				}
				else{
					first.get(i).add(5555);
					newFirst.add(temp);
				}
				
				
			}
			else
				newSecond.add(second.get(i));
				
		}
		for(int i=size-1; i<second.size(); i++){
			if(second.get(i).size() == 2)
				second.get(i).add(6666);
			newSecond.add(second.get(i));
		}
		finalResults.add(newFirst);
		finalResults.add(newSecond);
		return finalResults;
	}
	/*
	 * Resolve MSs ambiguity !
	 */
	
	public Vector<Integer> resolveAmbiguity(int index,Vector<Integer> current,Vector<Vector<Integer>> first, Vector<Vector<Integer>> second, int size, int ID){
		Vector<Integer> beforeFirst = first.get(index-1);
		Vector<Integer> beforeSecond = second.get(index-1);
		
		if(index+1 < size){
			Vector<Integer> afterFirst =first.get(index+1);
		}
		Vector<Integer> afterSecond = second.get(index+1);
		
		int a1 = Math.abs(beforeFirst.get(0) - current.get(0)); // First's row
 	 	int a2 = Math.abs(beforeSecond.get(0) - current.get(0)); // Second's row
 	 	int b1 = Math.abs(beforeFirst.get(1) - current.get(1)); // First's column
 	 	int b2 = Math.abs(beforeSecond.get(1) - current.get(1));  // Second's column
 	 	
 	 	
 	 	if(a1 == a2 && b1 == b2){
 	 		current.add(ID);
 	 		return current;
 	 	}
 	 	if(a1 < a2 && b1<b2 ){
 	 		current.add(5555);return current;
 	 	}
 	 	if(a1 > a2 && b2 > b1){
 	 		current.add(6666);return current;
 	 	}
 	 	int A = Math.min(a1, a2);
 	 	int B = Math.min(b1, b2);
 	 	int min = Math.min(A, B);
 	 	
 	 	if(min == a1 || min == b1){
 	 		current.add(5555); return current;
 	 	}
 	 	
 	 		current.add(6666);	return current;
 	 	
 	 	
	}
	/*
	 * the results of tracking cell by cell
	 */
	public Vector<Vector<Integer>> printPath( Vector<Vector<Integer>> trackingPath){
		Vector<Vector<Integer>> trackingResults = new Vector<Vector<Integer>>();
		for(int i=0; i< trackingPath.size(); i++){
			if(i+1 <= trackingPath.size()-1){
				star = new AStarAlgorithm();
		ArrayList<Vector<Integer>> tempPath = star.findPath(trackingPath.get(i).get(0), trackingPath.get(i).get(1),
				trackingPath.get(i+1).get(0), trackingPath.get(i+1).get(1));
		tempPath =RFID.finalSolution(RFID.swap(tempPath), trackingPath.get(i).get(0), trackingPath.get(i).get(1));
		
		for(Vector<Integer> item : tempPath )
		trackingResults.add(item);
		}
			}
		
		return trackingResults;
	}
	
	 
}
