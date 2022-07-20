package com.altherwy.rfidsimulator;

import java.util.ArrayList;
import java.util.Vector;

public class AStarAlgorithm {
	public  final int numberOfRowsinMap = 21;
	public  final int numberOfColumnmsinMap = 35;
	public int map[][] = new int[numberOfRowsinMap][numberOfColumnmsinMap]; // First is row
	public final int wakeable = 2;
	public final int unWakeable = 1;
	public int obstacles[][] = new int[250][2];
	public int doors[] = new int [14];
	// Vector contains ( index, row, column, FatherRow, FatherColumn, G,H)
	public ArrayList<Vector<Integer>> openList = new ArrayList<Vector<Integer>>();
	public ArrayList<Vector<Integer>> closeList = new ArrayList<Vector<Integer>>();
	public ArrayList<Vector<Integer>> thePath = new ArrayList<Vector<Integer>>();

	public AStarAlgorithm() {
		this.createObstacles();
		int x = 1;
		for (int i = 0; i < this.numberOfRowsinMap; i++) {
			for (int j = 0; j < this.numberOfColumnmsinMap; j++) {
				if (map[i][j] != unWakeable){
					map[i][j] = wakeable;
				
				}
			}
		}
		
	}

	public ArrayList<Vector<Integer>> findPath(int startRow, int startcolumn, int endRow,
			int endColumn) {
		Vector<Integer> vector = new Vector<Integer>();
		vector.add(0);
		vector.add(startRow);
		vector.add(startcolumn);
		vector.add(-1);
		vector.add(-1);
		vector.add(0);
		vector.add(0);
		vector.add(0);
		thePath.add(vector);
		this.openList.add(vector);
		if(startRow == endRow && startcolumn == endColumn){
			ArrayList<Vector<Integer>> identical =  new ArrayList<Vector<Integer>>();
			identical.add(vector);
			return identical;
		}
		// until here is initilazation of start point
		this.findNeighbours(startRow, startcolumn, endRow, endColumn, 0); // add
																			// all
																			// neighbours
																			// to
																			// open
																			// list
		this.closeList.add(vector);
		ArrayList<Vector<Integer>> neighbours = new ArrayList<Vector<Integer>>();
		neighbours = this.returnNeighboursinOpenList(startRow, startcolumn);
		this.openList.remove(vector); // add the current cell to the close list
										// then remove it from open list
		
		vector = this.minimumF(neighbours);
		thePath.add(vector);
		this.closeList.add(vector);
		this.openList.remove(vector);
		boolean countinue = true;
		while (countinue) {
			int row = vector.get(1);
			int column = vector.get(2);
			if (row == endRow && column == endColumn)
				countinue = false;
			else {
				this.findNeighbours(row, column, endRow, endColumn,
						vector.get(5));
				neighbours = this.returnNeighboursinOpenList(row, column);
				if (neighbours.isEmpty()){
					if(openList.isEmpty())
					{
						System.out.println("Dead End");
					return null;
					}
					this.openList.remove(vector);
					vector = this.minimumF(openList);
					thePath.add(vector);
					this.closeList.add(vector);
				}
				else {
					
					this.openList.remove(vector);
					vector = this.minimumF(neighbours);
					thePath.add(vector);
					this.closeList.add(vector);
				}
			}
		}
		return this.displayPath(startRow, startcolumn);
		//return thePath;
		
	}

	/*
	 * From createObstacles 2-D array, add the obstacles to the map.
	 */
	public void createObstacles() {
		Vector<Integer> rowCol = new Vector<Integer>();
		int length =this.fillObstacles();
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < 2; j++) {
				rowCol.add(obstacles[i][j]);
			}
			map[rowCol.get(0)][rowCol.get(1)] = unWakeable;
			rowCol.removeAllElements();
		}
	}

	/*
	 * Get the obstacles indexes in the map and put it in createObstacles 2-D
	 * array
	 */
	public int fillObstacles() {
		int[] rowColObs = {0, 0, 0, 1, 0, 2, 0, 16,0,17,0,18,0,28,0,29,0,30,0,31,0,32,0,33,0,34
				
				,1,0,1,1,1,2,1,16,1,17,1,18,1,28,1,29,1,30,1,31,1,32,1,33,1,34,
				
				2,0,2,1,2,16,2,17,2,18,2,25,2,26,2,27,2,33,2,34
				
				,3,16,3,17,3,18,3,25,3,26,3,27,3,33,3,34,
				
				4,0,4,1,4,16,4,17,4,18,4,25,4,26,4,27,4,33,4,34,
				
				5,0,5,1,5,25,5,26,5,27,5,33,5,34,
				
				6,25,6,26,6,27,
				
				8,33,8,34,9,33,9,34,
				
				10,0,10,1,10,2,10,3,10,4,10,5,10,6,10,7,10,8,10,9,10,10,10,11,10,12,10,13,10,14,10,15,
				10,26,10,27,10,33,10,34,
				11,2,11,3,11,9,11,10,
				13,15,
				
				14,15,14,16,14,20,14,21,14,22,14,23,14,24,14,25,
				15,25
				
				,16,25,17,25,18,15,18,25,18,26,18,27,18,34,
				
				19,15,19,16,19,17,19,18,19,25,19,26,19,27,
				20,5,20,6,20,15,20,16,20,17,20,18,20,25,20,26,20,27}; 
		createDoors();
		int index = 0;
		for (int i = 0; i < (rowColObs.length)/2; i++) {
			for (int j = 0; j < 2; j++) {
				obstacles[i][j] = rowColObs[index];
				index++;
			}
		}
		return (rowColObs.length)/2;

	}
	/*
	 * return the cells that represents door, for motion sensors only 
	 */
	public void createDoors(){
		doors [0] = 1115;
		doors [1] = 1215;
		doors [2] = 1515;
		doors [3] = 1615;
		doors [4] = 1715;
		doors [5] = 1417;
		doors [6] = 1418;
		doors [7] = 1419;
		doors [8] = 1829;
		doors [9] = 1830;
		doors [10] = 1831;
		doors [11] = 1832;
		doors [12] = 1534;
		doors [13] = 1634;
				
	}

	/*
	 * Find all neighbours around the current cell
	 */

	public void findNeighbours(int row, int column, int endRow, int endColumn,
			int ParentG) {
		
		this.checkNeighbours(row - 1, column - 1, row, column, endRow,
				endColumn, 14, ParentG);
		this.checkNeighbours(row - 1, column, row, column, endRow, endColumn,
				10, ParentG);
		this.checkNeighbours(row - 1, column + 1, row, column, endRow,
				endColumn, 14, ParentG);
		this.checkNeighbours(row, column - 1, row, column, endRow, endColumn,
				10, ParentG);
		this.checkNeighbours(row + 1, column - 1, row, column, endRow,
				endColumn, 14, ParentG);
		this.checkNeighbours(row, column + 1, row, column, endRow, endColumn,
				10, ParentG);
		this.checkNeighbours(row + 1, column, row, column, endRow, endColumn,
				10, ParentG);
		this.checkNeighbours(row + 1, column + 1, row, column, endRow,
				endColumn, 14, ParentG);

	}

	/*
	 * Check if a cell is in the map and is not in the open list
	 */

	public void checkNeighbours(int row, int column, int originalRow,
			int originalColumn, int endRow, int endColumn, int G, int ParentG) {
		Vector<Integer> temp = new Vector<Integer>();
		
		if (row >= 0 && column >= 0 && row < numberOfRowsinMap 
				&& column < numberOfColumnmsinMap ) {
			
			if (map[row][column] == wakeable) {

				if (inOpenList(row, column) == -100) {
					if (NOTincloseList(row, column)) {
						temp.add(openList.size()); // Id
						temp.add(row);
						temp.add(column);
						temp.add(originalRow); // Parent row
						temp.add(originalColumn); // Parent column
						temp.add(G + ParentG);// G value
						temp.add(this
								.calculateH(row, column, endRow, endColumn)); // H
																				// value
						temp.add(G
								+ ParentG
								+ this.calculateH(row, column, endRow,
										endColumn)); // F
														// value
														// (i.e
														// G+H)
						openList.add(temp);
					}
				} else
					this.updateCellGValue(row, column,originalRow, originalColumn, G, ParentG);

			}

		}

	}

	/*
	 * return ID if the cell is already in the open list, -100 otherwise.
	 */

	public int inOpenList(int row, int column) {
		for (int i = 0; i < openList.size(); i++) {
			if (row == openList.get(i).get(1)
					&& column == openList.get(i).get(2))
				return openList.get(i).get(0);
		}
		return -100;
	}

	/*
	 * retuen True if the cell is NOT in the close list, false otherwise
	 */

	public boolean NOTincloseList(int row, int column) {
		for (int i = 0; i < closeList.size(); i++) {
			if (closeList.get(i).get(1) == row
					&& closeList.get(i).get(2) == column)
				return false;
		}
		return true;
	}

	/*
	 * Calculate H for given cell
	 */
	public int calculateH(int row, int column, int endRow, int endColumn) {
		int i = 0;
		int j = 0;
		if (endRow >= row)
			i = endRow - row;
		else
			i = row - endRow;
		if (endColumn >= column)
			j = endColumn - column;
		else
			j = column - endColumn;
		return (i * 10) + (j * 10);
	}

	/*
	 * Update the given cell G value, if possible
	 */
	public void updateCellGValue(int row, int column,int parentRow, int parentColumn, int G, int ParentG) {
		for (int i = 0; i < this.openList.size(); i++) {
			Vector<Integer> v = this.openList.get(i);
			if (v.get(0) == this.inOpenList(row, column))

			{
				if (v.get(5) > (ParentG + G)) {
					// change the parent
					v.remove(5);
					v.add(5, G + ParentG);
					v.remove(3);
					v.add(3,parentRow);
					v.remove(4);
					v.add(4,parentColumn);
					this.openList.remove(i);
					this.openList.add(i, v);
					//System.out.println("Here !!!!!");
					return ;
				}
			}

		}
	}

	/*
	 * return the neighbours of the current cell (that have been added in the
	 * open list before)
	 */

	public ArrayList<Vector<Integer>> returnNeighboursinOpenList(int row,
			int column) {
		Vector<Integer> temp = new Vector<Integer>();
		ArrayList<Vector<Integer>> list = new ArrayList<Vector<Integer>>();
		for (int i = 0; i < openList.size(); i++) {
			temp = openList.get(i);
			if (temp.get(3) == row && temp.get(4) == column)
				list.add(temp);
		}
		return list;
	}

	/*
	 * return the cell with the minimum F from the given list
	 */

	public Vector<Integer> minimumF(ArrayList<Vector<Integer>> neighbours) {
		int index = 0;
		int i = 1;
		while (i < neighbours.size()) {
			if (neighbours.get(i).get(7) <= neighbours.get(index).get(7))
				index = i;
			i++;

		}
		return neighbours.get(index);
	}
	public ArrayList<Vector<Integer>> displayPath(int startRow, int startColumn){
		int parentRow = -100;
		int parentColumn = -100;
		ArrayList<Vector<Integer>> tempPAth = new ArrayList<Vector<Integer>>();
		Vector<Integer> root = new Vector<Integer>();
		for (int i=thePath.size()-1; i>= 0;i--){
			parentRow = thePath.get(i).get(3);
			parentColumn = thePath.get(i).get(4);
			if(parentRow == startRow && parentColumn == startColumn){
				tempPAth.add(thePath.get(i));
				i = -100;
			}
			else
				tempPAth.add(thePath.get(i));		
		}
		
		root.add(0);
		root.add(startRow);
		root.add(startColumn);
		root.add(-1);
		root.add(-1);
		root.add(0);
		root.add(0);
		root.add(0);
		tempPAth.add(root);
		for(int i=tempPAth.size()-1; i>= 0; i--){
			//System.out.println("Row: "+tempPAth.get(i).get(1)+" Column: "+tempPAth.get(i).get(2));
		}
		return tempPAth;
	}

}
