package ch.maybites.p3.sparck.remoterender.syphon;

import processing.core.PApplet;

import remixlab.dandelion.geom.*;
import oscP5.*;

public class SparckObject {
	public Vec pos;
	public Quat quat;
	public Vec scale;

	private String myAddressName;

	private PApplet parent;
	
	public SparckObject(PApplet _parent, String _addressName){
		pos = new Vec(0, 0, 0);
		quat = new Quat(0, 0, 0, 0);
		scale = new Vec(1, 1, 1);

		myAddressName = _addressName;
		parent = _parent;
	}

	protected void oscEvent(OscMessage theOscMessage) {
		if(theOscMessage.addrPattern().equals(myAddressName+"/worldpos")){
			setPosition(theOscMessage.get(0).floatValue(), theOscMessage.get(1).floatValue(), theOscMessage.get(2).floatValue());
		} else if(theOscMessage.addrPattern().equals(myAddressName+"/worldquat")){
			setQuat(theOscMessage.get(0).floatValue(), theOscMessage.get(1).floatValue(), theOscMessage.get(2).floatValue(), theOscMessage.get(3).floatValue());
		} else if(theOscMessage.addrPattern().equals(myAddressName+"/worldscale")){
			setScale(theOscMessage.get(0).floatValue(), theOscMessage.get(1).floatValue(), theOscMessage.get(2).floatValue());
		} else if(theOscMessage.addrPattern().indexOf(myAddressName + "/") == 0){
			parent.println(" invalid addrpattern: "+theOscMessage.addrPattern() + " | it only accepts: /worldpos, /worldquat and /worldscale" );  
		}
	}

	public void push(){
		if(pos != null){
			parent.pushMatrix();
			parent.translate(pos.x(), pos.y(), pos.z());
			Vec rot = quat.axis();
			parent.rotate(quat.angle(), rot.x(), rot.y(), rot.z());
			parent.scale(scale.x(), scale.y(), scale.z());
		}
	}

	public void pop(){
		if(pos != null){    
			parent.popMatrix();
		}
	}

	protected void setPosition(float x, float y, float z){
		pos = new Vec(x, y, z);
	}

	protected void setScale(float x, float y, float z){
		scale = new Vec(x, y, z);
	}

	protected void setQuat(float x, float y, float z, float u){
		quat = new Quat(x, y, z, u);
	}
}