package com.altherwy.rfidsimulator;

import java.util.ArrayList;

//import javax.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Room")
public class Room {
	private ArrayList<Wall> walls = new ArrayList<Wall>();
	private double height;
	private double width;

	public Room(ArrayList<Wall> walls, double height, double width) {
		this.setWalls(walls);
		this.setHeight(height);
		this.setWidth(width);
	}
	public Room(){
		/*
		 * try{ File xmlFile = new File(xml_File_Name); JAXBContext context =
		 * JAXBContext.newInstance(Room.class); Unmarshaller un =
		 * context.createUnmarshaller(); Room room = (Room)
		 * un.unmarshal(xmlFile);
		 * System.out.println("Height is "+room.getHeight()+" width is "+
		 * room.getWidth()); for (int i=0;i<room.getWalls().size(); i++){
		 * System.out.println("Wall Height  "+
		 * room.getWalls().get(i).getHeight()); } } catch(Exception e){
		 * e.printStackTrace(); }
		 */
		
	}

	public ArrayList<Wall> getWalls() {
		return walls;
	}

	@XmlElement(name = "wall")
	public void setWalls(ArrayList<Wall> walls) {
		this.walls = walls;
	}

	public double getHeight() {
		return height;
	}

	@XmlAttribute(name = "height")
	public void setHeight(double height) {
		this.height = height;
	}

	public double getWidth() {
		return width;
	}

	@XmlAttribute(name = "width")
	public void setWidth(double width) {
		this.width = width;
	}

}
