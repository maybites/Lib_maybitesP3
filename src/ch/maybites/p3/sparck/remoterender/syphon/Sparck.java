package ch.maybites.p3.sparck.remoterender.syphon;

import processing.core.PApplet;
import remixlab.proscene.*;
import remixlab.dandelion.geom.*;
import oscP5.*;
import codeanticode.syphon.*;

import java.util.*;

/**
 * Sparck is the main class to create a remote render connection with processing
 * 
 * USAGE:
 * 
 * 	1. Create an global instance of Sparck
 *  2. then add a sparck object
 *  3. then register the object to the sparck instance
 *	4. pass on the oscEvent to sparck
 *	5. add the sendFrame() function at the end of the draw() call
 *  6. put the drawing functions inside the sparck objects push and pop functions.
 *  
 *  Sparck sparck;
 *	SparckObject objectA;
 *
 *	public void setup() {
 *		sparck = new Sparck(this, "ProcessingSparck");		
 *		objectA = new SparckObject(this, "/objectA"); //addressspace : /objectA/world...
 *		sparck.registerObject(objectA);
 *	}
 *
 *	public void draw() {
 *		  objectA.push();
 *		  box(10);
 *		  objectA.pop();
 *		  sparck.sendFrame();
 *	}
 *
 *	void oscEvent(OscMessage theOscMessage) {
 *		sparck.oscEvent(theOscMessage);
 *	}

 * @author maybites
 *
 */
public class Sparck{
	Scene myScene;
	PApplet parent;
	SyphonServer myServer;

	ArrayList<SparckObject> myObjects;

	public Sparck(PApplet _base, String _syphonServer){
		// store my applications parent
		parent = _base;
		// create a new scene
		myScene = new Scene(_base);
		myScene.disableMouseAgent();
		myScene.flip();
		myScene.setGridVisualHint(false);
		myScene.setAxesVisualHint(false);
		// Create syhpon server to send frames out.
		myServer = new SyphonServer(_base, _syphonServer);

		myObjects = new ArrayList<SparckObject>();
	}   

	public void registerObject(SparckObject _register){
		myObjects.add(_register);
	}

	private void oscEventObject(OscMessage theOscMessage) {
		for(int i = 0; i < myObjects.size(); i++){
			myObjects.get(i).oscEvent(theOscMessage);
		}
	}

	public Scene getScene(){
		return myScene;
	}

	public void sendFrame(){
		myServer.sendScreen();
	}

	public void oscEvent(OscMessage theOscMessage) {
		/* The following lines set the camera*/
		if(theOscMessage.addrPattern().equals("/camera/worldpos")){
			myScene.camera().setPosition(new Vec(theOscMessage.get(0).floatValue(), theOscMessage.get(1).floatValue(), theOscMessage.get(2).floatValue()));
			//println(" addrpattern: "+theOscMessage.get(0).floatValue());
		} else if(theOscMessage.addrPattern().equals("/camera/worldquat")){
			myScene.camera().setOrientation(new Quat(theOscMessage.get(0).floatValue(), theOscMessage.get(1).floatValue(), theOscMessage.get(2).floatValue(), theOscMessage.get(3).floatValue()));
			//println(" addrpattern: "+theOscMessage.addrPattern());
		} else if(theOscMessage.addrPattern().equals("/camera/fov")){
			myScene.camera().setAspectRatio(theOscMessage.get(0).floatValue());
			myScene.camera().setFieldOfView(theOscMessage.get(1).floatValue() / 180.f * (float)parent.PI);
			//myScene.camera().setZNearCoefficient(theOscMessage.get(2).floatValue());
			//myScene.camera().setZClippingCoefficient(theOscMessage.get(3).floatValue());
		} else if(theOscMessage.addrPattern().equals("/camera/camtype")){
			// change between perspective and orthographic cameras
		} else if(theOscMessage.addrPattern().indexOf("/camera/") == 0){
			parent.println(" invalid addrpattern: "+theOscMessage.addrPattern() + " | it only accepts: /worldpos, /worldquat and /fov" );  
		} else {
			oscEventObject(theOscMessage);
		}

		/* 
	      This is not functional and I dont know why. 
	    if(theOscMessage.addrPattern().equals("/camera/p_matrix")){
	      //println(" addrpattern: "+theOscMessage.addrPattern());
	      //println(" typetag: "+theOscMessage.typetag());
	      float[] mtx = new float[16];
	      byte[] typtag = theOscMessage.getTypetagAsBytes();
	      for(int i = 0; i < 16; i++){
	        if(typtag[i] == 'f'){
	          mtx[i] = theOscMessage.get(i).floatValue();
	        } else if (typtag[i] == 'i'){
	          mtx[i] = (float) theOscMessage.get(i).intValue();
	        }
	      }
	      myScene.camera().setProjection(mtx);
	    }
		 */
	}

}
