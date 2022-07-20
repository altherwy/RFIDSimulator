package com.altherwy.rfidsimulator;

import jakarta.xml.bind.annotation.XmlAttribute;

public class Wall {
	private double x,y;
	private double height,width;
	
	public Wall(double x, double y,double height,double width){
		this.setHeight(height);
		this.setWidth(width);
		this.setX(x);
		this.setY(y);
	}
	public Wall(){
		
	}
	public double getHeight() {
		return height;
	}
	@XmlAttribute(name ="height")
	public void setHeight(double height) {
		this.height = height;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}

}
