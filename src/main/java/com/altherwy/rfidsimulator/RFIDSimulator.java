package com.altherwy.rfidsimulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Vector;

import javax.naming.Context;
import javax.swing.JPanel;
import javax.swing.Box.Filler;
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.Unmarshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.JAXBContext;

public class RFIDSimulator {
    
    public static final String xml_File_Name = "src\\main\\java\\com\\altherwy\\rfidsimulator\\docs\\FloorPlan.xml";
    public AStarAlgorithm AST;
    public Vector<Integer> numbersForSort = new Vector<Integer>();
    public Vector<Vector<Integer>> RFIDPlaces = new Vector<Vector<Integer>>();
    public final int Number_OF_Readers = 10;
    public final int Number_Of_MS = 15;
    public final int readerRadious = 2;
    public final int MSRadious = 1;
    public final int virtualReaderRnage = 4;
    public final int virtualMSRange = 2;
    public Vector<Vector<Integer>> rfidreaders, readersRange, MS, MSranges;
    public final String link = "src\\main\\java\\com\\altherwy\\rfidsimulator\\docs\\startandend.txt";
    public final String StationaryLink = "src\\main\\java\\com\\altherwy\\rfidsimulator\\docs\\stationaryStartandEndPoint.txt";
    public MapEditor mapEditor;
    
    public int max = 0;
    
    public RFIDSimulator() {
        AST = new AStarAlgorithm();
        rfidreaders = new Vector<Vector<Integer>>();
        readersRange = new Vector<Vector<Integer>>();
        MS = new Vector<Vector<Integer>>();
        MSranges = new Vector<Vector<Integer>>();
        
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        RFIDSimulator RFID = new RFIDSimulator();

        //Vector<Vector<Integer>> startandEnd = RFID.fillStartandEndPoints(RFID.link);
        Vector<Vector<Integer>> startandEnd = RFID.fillStartandEndPoints(RFID.StationaryLink);
        
        for (int index = 0; index < startandEnd.size(); index++) {
            
            AStarAlgorithm AStar = new AStarAlgorithm();
            ArrayList<Vector<Integer>> path = new ArrayList<Vector<Integer>>();
            Vector<Integer> collection = new Vector<Integer>();
            collection = startandEnd.get(index);
            
            path = AStar.findPath(collection.get(0), collection.get(1),
                    collection.get(2), collection.get(3));
            path = RFID.finalSolution(RFID.swap(path), collection.get(0),
                    collection.get(1));
            
            if (path == null) {
                System.out.println("No cells in the path!!");
            }
            /**
             * * Here where placing the readers starts **********
             */
            RFID.extractNumbers(path);
        }
        
        Collections.sort(RFID.numbersForSort);
        
        RFID.RFIDPlaces = RFID.orderCells();
        
        Vector<Vector<Integer>> finalPlaces = RFID.RFIDReadersPlaces(RFID.RFIDPlaces); // find the readers spots
        RFID.rfidreaders = finalPlaces; // RFID Readers without ranges
        Vector<Vector<Integer>> readersWithoutRanges = finalPlaces;
        Vector<Vector<Integer>> readerWithRange = RFID.createPhysicalRange(finalPlaces, RFID.readerRadious); //create the readers ranges
        RFID.readersRange.addAll(readerWithRange); // RFID Readers with range
        
        Vector<Vector<Integer>> MSPlaces = RFID.MSPlaces(RFID.AST.doors, readerWithRange, readersWithoutRanges); // find the MS spots
        RFID.MS.addAll(MSPlaces); // MS without range
        RFID.MSranges = RFID.createPhysicalRange(MSPlaces, RFID.MSRadious); // MS with range

        //********************//
        Vector<Integer> entranceAndExitPoint = new Vector<Integer>();
        entranceAndExitPoint.add(15);
        entranceAndExitPoint.add(34);
        RFID.rfidreaders.add(0, entranceAndExitPoint); // Adding RFID Reader at the Entrance and Exit point
        RFID.MS.remove(1); // Removing any Motion Sensor at the Entrance/Exit point
        System.out.println("RFID Readers Locations in the form of [row, column]");
        for (Vector<Integer> item : RFID.rfidreaders) {
            System.out.println(item);
        }
        System.out.println("Motion Sensors Locations in the form of [row, column]");
        for (Vector<Integer> item : RFID.MS) {
            System.out.println(item);
        }
        
        TrackPersons track = new TrackPersons(RFID.readersRange, RFID.MSranges);
        Vector<Vector<Vector<Integer>>> trackingResultsforALL = track.allTrackingResults();
        
        RFID.mapEditor = new MapEditor(RFID.rfidreaders, RFID.MS, trackingResultsforALL); // Visual Representation for the first person
        
    }

    /*
	 * (modify the given list of cells so that it start form the beginning all
	 * the way to the target)
     */
    public ArrayList<Vector<Integer>> swap(ArrayList<Vector<Integer>> path) {
        ArrayList<Vector<Integer>> route = new ArrayList<Vector<Integer>>();
        for (int i = path.size() - 1; i >= 0; i--) {
            route.add(path.get(i));
        }
        return route;
    }

    /*
	 * return the final route from the start to the target
     */
    public ArrayList<Vector<Integer>> finalSolution(
            ArrayList<Vector<Integer>> pathA, int startRow, int startColumn) {
        Vector<Integer> tempA = new Vector<Integer>();
        Vector<Integer> tempB = new Vector<Integer>();
        ArrayList<Vector<Integer>> theRoute = new ArrayList<Vector<Integer>>();
        int index = 0;
        tempA = pathA.get(index);
        theRoute.add(tempA);
        while (index + 1 < pathA.size()) {
            tempA = pathA.get(index);
            tempB = pathA.get(index + 1);
            if (this.isNeighbour(tempA, tempB)) {
                index++;
                theRoute.add(tempB);
            } else {
                theRoute = findPathtoTheStartPoint(tempB, startRow,
                        startColumn, pathA);
                if (theRoute == null) {
                    System.out.println("Route To Parent is null!!! ");
                    return null;
                }
                index++;
            }
            
        }
        return theRoute;
    }

    /*
	 * Find if two cells are neighbours ( one of them lies on the other 9
	 * neighbours cells)
     */
    public boolean isNeighbour(Vector<Integer> cellA, Vector<Integer> cellB) {
        int ARow = cellA.get(1);
        int AColumn = cellA.get(2);
        int BRow = cellB.get(1);
        int BColumn = cellB.get(2);
        if (ARow == BRow || ARow == BRow - 1 || ARow == BRow + 1) {
            if (AColumn == BColumn || AColumn == BColumn - 1
                    || AColumn == BColumn + 1) {
                
                return true;
            }
        }
        return false;
        
    }

    /*
	 * Find the route back to the start point for the given cell
     */
    public ArrayList<Vector<Integer>> findPathtoTheStartPoint(
            Vector<Integer> current, int startRow, int startColumn,
            ArrayList<Vector<Integer>> list) {
        
        ArrayList<Vector<Integer>> routetoParent = new ArrayList<Vector<Integer>>();
        int row = current.get(1);
        int column = current.get(2);
        while (true) {
            routetoParent.add(current);
            /*
			 * System.out.println("YRS " + current.get(0) + " / " +
			 * current.get(1) + "/" + current.get(2) + "/" + current.get(3) +
			 * "/  " + current.get(4) + " / " + current.get(5) + "/ " +
			 * current.get(6) + "/ " + current.get(7));
             */
            current = this.returnParentVector(current, list);
            if (current == null) {
                System.out.println("NO Parent !!!!");
            }
            row = current.get(1);
            column = current.get(2);
            if (row == startRow && column == startColumn) {
                routetoParent.add(current);
                
                return swap(routetoParent);
            }
            
        }
    }

    /*
	 * public ArrayList<Vector<Integer>> returnNewRoute(Vector<Integer>
	 * stranger, ArrayList<Vector<Integer>> route) {
	 * 
	 * int index = route.size() - 1;
	 * 
	 * while (!route.isEmpty()) { Vector<Integer> temp = route.get(index); if
	 * (this.isParent(stranger, temp)) { route.add(stranger); //
	 * System.out.println("Right"); return route; } else { route.remove(index);
	 * index = route.size() - 1; }
	 * 
	 * } return null; }
     */
 /*
	 * Return the parent of the current cell
     */
    public boolean isParent(Vector<Integer> son, Vector<Integer> parent) {
        int sonParentRow = son.get(3);
        int sonParentColumn = son.get(4);
        int parentRow = parent.get(1);
        int parentColumn = parent.get(2);
        if (sonParentRow == parentRow && sonParentColumn == parentColumn) {
            return true;
        }
        return false;
    }

    /*
	 * Return the parent of the given cell
     */
    public Vector<Integer> returnParentVector(Vector<Integer> current,
            ArrayList<Vector<Integer>> list) {
        
        for (int index = 0; index < list.size(); index++) {
            if (list.get(index).get(1) == current.get(3)
                    && list.get(index).get(2) == current.get(4)) {
                return list.get(index);
            }
        }
        return null;
    }

    /*
	 * add collections of start and end points
     */
    public Vector<Vector<Integer>> fillStartandEndPoints(String link) {
        Vector<Vector<Integer>> save = new Vector<Vector<Integer>>();
        save.add(fillStartandEndPointsHelper(12, 8, 19, 20));
        save.add(fillStartandEndPointsHelper(19, 20, 9, 32));
        save.add(fillStartandEndPointsHelper(9, 32, 4, 32));
        save.add(fillStartandEndPointsHelper(4, 32, 4, 19));
        save.add(fillStartandEndPointsHelper(4, 19, 2, 30));
        save.add(fillStartandEndPointsHelper(2, 30, 19, 29));
        save.add(fillStartandEndPointsHelper(19, 29, 7, 8));
        save.add(fillStartandEndPointsHelper(7, 8, 1, 3));
        save.add(fillStartandEndPointsHelper(1, 3, 7, 2));
        save.add(fillStartandEndPointsHelper(7, 2, 12, 4));
        Vector<Vector<Integer>> temp = this.readFromFile(link);
        // ****** The Second Person *************/
        for (int index = 0; index < temp.size(); index++) {
            save.add(fillStartandEndPointsHelper(temp.get(index).get(0), temp
                    .get(index).get(1), temp.get(index).get(2), temp.get(index)
                    .get(3)));
        }
        return save;
        
    }

    /*
	 * Help fillStartandEndPoints method to do its job
     */
    public Vector<Integer> fillStartandEndPointsHelper(int startRow,
            int startcolumn, int endRow, int endColumn) {
        Vector<Integer> StartAndEnd = new Vector<Integer>();
        StartAndEnd.add(startRow);
        StartAndEnd.add(startcolumn);
        StartAndEnd.add(endRow);
        StartAndEnd.add(endColumn);
        return StartAndEnd;
    }

    /*
	 * combine row and column as "Row Column" integer for sorting purposes
     */
    public void extractNumbers(ArrayList<Vector<Integer>> path) {
        
        for (int i = 0; i < path.size(); i++) {
            Integer row = path.get(i).get(1);
            Integer column = path.get(i).get(2);
            String temp = row.toString() + column.toString();
            if (temp.length() == 3) {
                if (row.toString().length() == 1) {
                    temp = "0" + row.toString() + column.toString();
                } else {
                    temp = row.toString() + "0" + column.toString();
                }
            }
            numbersForSort.add(Integer.parseInt(temp));
        }
    }

    /*
	 * Read from file, start and end points
     */
    public Vector<Vector<Integer>> readFromFile(String link) {
        Vector<Vector<Integer>> temp = new Vector<Vector<Integer>>();
        BufferedReader br = null;
        Vector<Integer> points = new Vector<Integer>();
        
        try {
            
            String sCurrentLine;
            
            br = new BufferedReader(new FileReader(link));
            
            while ((sCurrentLine = br.readLine()) != null) {
                if (!sCurrentLine.equals("**")) {
                    points.add(Integer.parseInt(sCurrentLine.trim()));
                } else {
                    temp.add(points);
                    points = new Vector<Integer>();
                }
                
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return temp;
        
    }

    /*
	 * Order cells based on the popularity
     */
    public Vector<Vector<Integer>> orderCells() {
        int count = 1;
        int maximum_repeatition = 0;
        Vector<Vector<Integer>> ordered = new Vector<Vector<Integer>>();
        
        for (int i = 0; i < numbersForSort.size(); i++) {
            Vector<Integer> temp = new Vector<Integer>();
            if (i != numbersForSort.size() - 1) {
                int firstnumber = numbersForSort.get(i);
                int secondnumber = numbersForSort.get(i + 1);
                if (firstnumber == secondnumber) {
                    count++;
                    if (count > maximum_repeatition) {
                        maximum_repeatition = count;
                    }
                } else {
                    temp.add(numbersForSort.get(i));
                    temp.add(count);
                    ordered.add(temp);
                    count = 1;
                }
            } else {
                temp.add(numbersForSort.get(i));
                temp.add(count);
                ordered.add(temp);
                count = 1;
            }
        }
        this.max = maximum_repeatition;
        return ordered;
    }

    /*
	 * Return Vector containing the cells where Readers should be placed
     */
    public Vector<Vector<Integer>> RFIDReadersPlaces(Vector<Vector<Integer>> ordered) {
        
        Vector<Vector<Integer>> places = new Vector<Vector<Integer>>();
        Vector<Vector<Integer>> temp = new Vector<Vector<Integer>>();
        for (int i = max; i > 0; i--) {
            temp.addAll(this.equaltonumber(ordered, i));
            //System.out.println("FFFFF "+temp);
        }
        
        places.addAll(createVirtualRange(temp));
        
        int difference = places.size() - Number_OF_Readers;
        while (difference > 0) {
            places.removeElementAt(places.indexOf(places.lastElement()));
            difference--;
        }
        return places;
        
    }

    /*
	 * Return Vector containing the cells where MSs should be placed
     */
    
    public Vector<Vector<Integer>> MSPlaces(int[] listOfDoors, Vector<Vector<Integer>> readersWithRanges, Vector<Vector<Integer>> readersWithoutRange) {
        int index = 0;
        Vector<Vector<Integer>> places = new Vector<Vector<Integer>>();
        
        while (places.size() <= Number_Of_MS && index < listOfDoors.length) {
            Vector<Integer> temp = new Vector<Integer>();
            temp = splitRowfromColumn(listOfDoors[index]);
            
            if (legitForReader(readersWithRanges, temp.get(0), temp.get(1), virtualMSRange)) {
                places.add(temp);
                readersWithRanges.add(temp);
            }
            
            index++;
        }
        
        int count = 0;

        //while (places.size() < Number_Of_MS && count < readersWithoutRange.size() ){
        //Vector<Vector<Integer>> savePlaces = new Vector<Vector<Integer>>();
        //savePlaces = places;
        //places.addAll(returnPlacesforMS(readersWithoutRange.get(count).get(0), readersWithoutRange.get(count).get(1), readersWithRanges, savePlaces));
        returnPlacesforMS(readersWithRanges, places, readersWithoutRange);
        //count ++;
        //}
        if (places.size() > Number_Of_MS) {
            int x = places.size() - Number_Of_MS;
            for (int i = 0; i < x; i++) {
                places.remove(places.lastElement());
            }
        }
        
        return places;
        
    }

    /*
	 * return candidate sites to place MSs near readers
     */
    public Vector<Vector<Integer>> returnPlacesforMS(Vector<Vector<Integer>> readersWithRanges, Vector<Vector<Integer>> places, Vector<Vector<Integer>> readersWithoutRange) {
        int count = 0;
        while (places.size() < Number_Of_MS && count < readersWithoutRange.size()) {
            //Vector<Vector<Integer>> places = new Vector<Vector<Integer>>();
            int row = readersWithoutRange.get(count).get(0);
            int column = readersWithoutRange.get(count).get(1);
            for (int i = 0; i < 3; i++) {
                if (row + i < AST.numberOfRowsinMap && column + 3 < AST.numberOfColumnmsinMap) {
                    if (isAvaliableAndWakeable(row + i, column + 3, readersWithRanges)) {
                        Vector<Integer> temp = new Vector<Integer>();
                        temp.add(row + i);
                        temp.add(column + 3);
                        if (!places.contains(temp) && legitForReader(places, temp.get(0), temp.get(1), virtualMSRange)) {
                            places.add(temp);
                        }
                    }
                }
                if (row + i < AST.numberOfRowsinMap && column - 3 >= 0) {
                    if (isAvaliableAndWakeable(row + i, column - 3, readersWithRanges)) {
                        Vector<Integer> temp = new Vector<Integer>();
                        temp.add(row + i);
                        temp.add(column - 3);
                        if (!places.contains(temp) && legitForReader(places, temp.get(0), temp.get(1), virtualMSRange)) {
                            places.add(temp);
                        }
                    }
                }
                if (row - i >= 0 && column + 3 < AST.numberOfColumnmsinMap) {
                    if (isAvaliableAndWakeable(row - i, column + 3, readersWithRanges)) {
                        Vector<Integer> temp = new Vector<Integer>();
                        temp.add(row - i);
                        temp.add(column + 3);
                        if (!places.contains(temp) && legitForReader(places, temp.get(0), temp.get(1), virtualMSRange)) {
                            places.add(temp);
                        }
                    }
                }
                if (row - i >= 0 && column - 3 >= 0) {
                    if (isAvaliableAndWakeable(row - i, column - 3, readersWithRanges)) {
                        Vector<Integer> temp = new Vector<Integer>();
                        temp.add(row - i);
                        temp.add(column - 3);
                        if (!places.contains(temp) && legitForReader(places, temp.get(0), temp.get(1), virtualMSRange)) {
                            places.add(temp);
                        }
                    }
                }
            }
            count++;
        }
        return places;
        
    }

    /*
	 * Find out if a cell is available to add MS on it and wakeable 
     */
    public boolean isAvaliableAndWakeable(int ARow, int AColumn, Vector<Vector<Integer>> readersWithRanges) {
        for (int i = 0; i < readersWithRanges.size(); i++) {
            if ((readersWithRanges.get(i).get(0) == ARow && readersWithRanges.get(i).get(1) == AColumn) || AST.map[ARow][AColumn] == 1) {
                return false;
            }
        }
        return true;
    }

    /*
	 * Return all cells where the number of repeatition = number
     */
    public Vector<Vector<Integer>> equaltonumber(
            Vector<Vector<Integer>> ordered, int number) {
        int index = 0;
        Vector<Vector<Integer>> temp = new Vector<Vector<Integer>>();
        while (index < ordered.size()) {
            if (ordered.get(index).get(1) == number) {
                temp.add(ordered.get(index));
            }
            index++;
        }
        return temp;
    }

    /*
	 * Confirm that a cell is legit to place a reader on it, then return all legit cells 
     */
    
    public Vector<Vector<Integer>> createVirtualRange(Vector<Vector<Integer>> candidateSites) {
        Vector<Vector<Integer>> blackList = new Vector<Vector<Integer>>();
        for (int i = 0; i < candidateSites.size(); i++) {
            Vector<Integer> rc = new Vector<Integer>();
            rc = splitRowfromColumn(candidateSites.get(i).get(0));
            if (legitForReader(blackList, rc.get(0), rc.get(1), virtualReaderRnage)) {
                blackList.add(rc);
            }
            
        }
        return blackList;
    }

    /*
	 * Split row from column
     */
    public Vector<Integer> splitRowfromColumn(Integer RowColumn) {
        Vector<Integer> rc = new Vector<Integer>();
        String temp = RowColumn.toString();
        int row = -1;
        int column = -1;
        if (temp.length() == 4) {
            row = Integer.parseInt(temp.substring(0, 2));
            column = Integer.parseInt(temp.substring(2, 4));
            rc.add(row);
            rc.add(column);
            return rc;
        }
        if (temp.length() == 3) {
            row = Integer.parseInt(temp.substring(0, 1));
            column = Integer.parseInt(temp.substring(1, 3));
            rc.add(row);
            rc.add(column);
            return rc;
        }
        row = Integer.parseInt(temp.substring(0, 1));
        column = Integer.parseInt(temp.substring(1, 2));
        rc.add(row);
        rc.add(column);
        return rc;
    }

    /*
	 * True if a spot is legit for a reader to be placed on, false otherwise 
     */
    
    public boolean legitForReader(Vector<Vector<Integer>> blackList, int Arow, int Acolumn, int numberOfSpots) {
        for (int i = 1; i <= numberOfSpots; i++) {
            for (int j = 0; j <= numberOfSpots; j++) {
                if (!legitForReaderHelper(Arow - i, Acolumn - j, blackList)) {
                    return false;
                }
            }
        }
        for (int i = 1; i <= numberOfSpots; i++) {
            for (int j = 0; j <= numberOfSpots; j++) {
                if (!legitForReaderHelper(Arow - i, Acolumn + j, blackList)) {
                    return false;
                }
            }
        }
        for (int i = 1; i <= numberOfSpots; i++) {
            for (int j = 0; j <= numberOfSpots; j++) {
                if (!legitForReaderHelper(Arow + i, Acolumn - j, blackList)) {
                    return false;
                }
            }
        }
        for (int i = 1; i <= numberOfSpots; i++) {
            for (int j = 0; j <= numberOfSpots; j++) {
                if (!legitForReaderHelper(Arow + i, Acolumn + j, blackList)) {
                    return false;
                }
            }
        }
        for (int j = 1; j <= numberOfSpots; j++) {
            if (!legitForReaderHelper(Arow, Acolumn + j, blackList)) {
                return false;
            }
            if (!legitForReaderHelper(Arow, Acolumn - j, blackList)) {
                return false;
            }
        }
        
        return true;
        
    }
    
    public boolean legitForReaderHelper(int Arow, int Acolumn, Vector<Vector<Integer>> blackList) {
        int Brow = -1;
        int Bcolumn = -1;
        for (int index = 0; index < blackList.size(); index++) {
            Brow = blackList.get(index).get(0);
            Bcolumn = blackList.get(index).get(1);
            if (Arow == Brow && Acolumn == Bcolumn) {
                return false;
            }
        }
        return true;
    }

    /*
	 * Create physical ranges
     */
    
    public Vector<Vector<Integer>> createPhysicalRange(Vector<Vector<Integer>> readers, int rangeRadious) {
        Vector<Vector<Integer>> readerWithRange = new Vector<Vector<Integer>>();
        for (int index = 0; index < readers.size(); index++) {
            readerWithRange.addAll(physicalRangeHelper(readers.get(index).get(0), readers.get(index).get(1), rangeRadious));
        }
        return readerWithRange;
    }
    
    public Vector<Vector<Integer>> physicalRangeHelper(int Arow, int Acolumn, int rangeRadious) {
        int highestRow = AST.numberOfRowsinMap;
        int highestColumn = AST.numberOfColumnmsinMap;
        Vector<Vector<Integer>> readerWithRange = new Vector<Vector<Integer>>();
        for (int i = 1; i <= rangeRadious; i++) {
            for (int j = 0; j <= rangeRadious; j++) {
                if (Arow + i < highestRow && Acolumn + j < highestColumn) {
                    Vector<Integer> temp = new Vector<Integer>();
                    temp.add(Arow + i);
                    temp.add(Acolumn + j);
                    temp.add(Arow);
                    temp.add(Acolumn);
                    readerWithRange.add(temp);
                }
            }
        }
        for (int i = 1; i <= rangeRadious; i++) {
            for (int j = 0; j <= rangeRadious; j++) {
                if (Arow - i >= 0 && Acolumn + j < highestColumn) {
                    Vector<Integer> temp = new Vector<Integer>();
                    temp.add(Arow - i);
                    temp.add(Acolumn + j);
                    temp.add(Arow);
                    temp.add(Acolumn);
                    readerWithRange.add(temp);
                }
            }
        }
        for (int i = 1; i <= rangeRadious; i++) {
            for (int j = 0; j <= rangeRadious; j++) {
                if (Arow - i >= 0 && Acolumn - j >= 0) {
                    Vector<Integer> temp = new Vector<Integer>();
                    temp.add(Arow - i);
                    temp.add(Acolumn - j);
                    temp.add(Arow);
                    temp.add(Acolumn);
                    readerWithRange.add(temp);
                }
            }
        }
        for (int i = 1; i <= rangeRadious; i++) {
            for (int j = 0; j <= rangeRadious; j++) {
                if (Arow + i < highestRow && Acolumn - j >= 0) {
                    Vector<Integer> temp = new Vector<Integer>();
                    temp.add(Arow + i);
                    temp.add(Acolumn - j);
                    temp.add(Arow);
                    temp.add(Acolumn);
                    readerWithRange.add(temp);
                }
            }
        }
        
        for (int j = 0; j <= rangeRadious; j++) {
            if (Acolumn - j >= 0) {
                Vector<Integer> temp = new Vector<Integer>();
                temp.add(Arow);
                temp.add(Acolumn - j);
                temp.add(Arow);
                temp.add(Acolumn);
                readerWithRange.add(temp);
            }
            
        }
        for (int j = 1; j <= rangeRadious; j++) {
            if (Acolumn + j < highestColumn) {
                Vector<Integer> temp = new Vector<Integer>();
                temp.add(Arow);
                temp.add(Acolumn + j);
                temp.add(Arow);
                temp.add(Acolumn);
                readerWithRange.add(temp);
            }
            
        }
        
        return readerWithRange;        
    }
    
}
