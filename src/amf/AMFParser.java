package amf;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.system.AppSettings;
import com.jme3.util.BufferUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class AMFParser extends SimpleApplication{

	private String filepath;
	private Material mat;
	private BitmapText txt;
	boolean cursorCaptive = true;

	public static void main(String[] args){

		File mFile = doFilePrompt();


		if(mFile != null){
			AMFParser app = new AMFParser(mFile.getAbsolutePath());

			app.setShowSettings(false);
			AppSettings settings = configureSettings();
			app.setSettings(settings);
			app.start();

		} else {
			System.out.println("Attachment cancelled by user.");
		}
	}

	public static AppSettings configureSettings(){

		AppSettings settings = new AppSettings(true);
		settings.put("Width", 1280);
		settings.put("Height", 720);
		settings.put("Title", "AMF Viewer");
		settings.put("VSync", true);
		//Anti-Aliasing
		settings.put("Samples", 4);
		return settings;
	}

	private void initKeys() {
		// You can map one or several inputs to one named action
		inputManager.addMapping("Left",   new KeyTrigger(KeyInput.KEY_J));
		inputManager.addMapping("Right",   new KeyTrigger(KeyInput.KEY_K));
		inputManager.addMapping("EX", new KeyTrigger(KeyInput.KEY_X));
		inputManager.addMapping("P", new KeyTrigger(KeyInput.KEY_P));
		inputManager.addListener(actionListener,"Left","Right","EX","P");

	}

	private ActionListener actionListener = new ActionListener() {
		public void onAction(String name, boolean isPressed, float tpf) {
			if (name.equals("Right")) {
				mat.getAdditionalRenderState().setWireframe(false);
			}
			if (name.equals("Left")) {
				mat.getAdditionalRenderState().setWireframe(true);
			}
			if (name.equals("EX") && !isPressed) {
				System.out.println("disppear");
				txt.setAlpha(0);
			}
			if (name.equals("P")){
				if(cursorCaptive){
					flyCam.setDragToRotate(true);
					cursorCaptive = false;
				} else{
					flyCam.setDragToRotate(false);
					cursorCaptive = true;
				}
			}
		}
	};

	public static File doFilePrompt(){
		JInternalFrame tempFrame = new JInternalFrame("Select your File");
		//System.out.println("newProjectDialog components count : " + newProjectDialog.getContentPane().getComponentCount());
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);

		tempFrame.add(fileChooser);
		//Show it.
		int returnVal = fileChooser.showDialog(null,
				"AMF Select");

		//Process the results.
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}
		fileChooser.setSelectedFile(null);
		return null;
	}

	public AMFParser(String filepath){
		this.filepath = filepath;
	}

	public static Document getDocument(File mFile){
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(mFile);
			doc.getDocumentElement().normalize();
			return doc;
		} catch (Exception ex) {
			Logger.getLogger(AMFParser.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	public void populateVertices(NodeList nList, Vector3f[] vertices){
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node oNode = nList.item(temp);

			if (oNode.getNodeType() == Node.ELEMENT_NODE) {

				NodeList oList = oNode.getChildNodes();

				int oLength = oList.getLength();

				float xVal=0,yVal=0,zVal=0;

				for(int i=0;i<oLength;i++){
					Node pNode = oList.item(i);
					String pName = pNode.getNodeName();
					if(pName == "coordinates"){
						Element eElement = (Element) pNode;
						xVal = Float.parseFloat(getTagValue("x", eElement));
						yVal = Float.parseFloat(getTagValue("y", eElement));
						zVal = Float.parseFloat(getTagValue("z", eElement));
						vertices[temp] = new Vector3f(xVal, yVal, zVal);
					}
				}
			}
		}

	}

	public void populateIndicesColors(NodeList mList, ArrayList<Float> mColors, ArrayList<Integer> mIndices){
		for (int temp = 0; temp < mList.getLength(); temp++) {

			Node mNode = mList.item(temp);

			if (mNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) mNode;

				int v1 = Integer.parseInt(getTagValue("v1", eElement));
				int v2 = Integer.parseInt(getTagValue("v2", eElement));
				int v3 = Integer.parseInt(getTagValue("v3", eElement));
				try{
					float r = Float.parseFloat(getTagValue("r", eElement));
					float g = Float.parseFloat(getTagValue("g", eElement));
					float b = Float.parseFloat(getTagValue("b", eElement));
					float a = Float.parseFloat(getTagValue("a", eElement));
					mColors.add(r);
					mColors.add(g);
					mColors.add(b);
					mColors.add(a);
					mColors.add(r);
					mColors.add(g);
					mColors.add(b);
					mColors.add(a);
					mColors.add(r);
					mColors.add(g);
					mColors.add(b);
					mColors.add(a);
				} catch(NullPointerException npe){
					System.out.println("no color");
				}

				mIndices.add(v1);
				mIndices.add(v2);
				mIndices.add(v3);

			}
		}
	}

	public void setFinalVertices(ArrayList<Integer> mIndices, ArrayList<Integer> indices, Vector3f[] vertices, Vector3f newVertices[]){
            
		for(int i=0; i< mIndices.size(); i++){

			Integer m = mIndices.get(i);
			Vector3f oldVertexM = vertices[m.intValue()];
			newVertices[i] = new Vector3f(oldVertexM.getX(), oldVertexM.getY(), oldVertexM.getZ());
			indices.add(i);
		}
	}

	public static NodeList getElements(File mFile, String mTag){
		Document mDocument = getDocument(mFile);
		return mDocument.getElementsByTagName(mTag);
	}

	public void simpleInitApp() {

		/** Create a pivot node at (0,0,0) and attach it to the root node */
		com.jme3.scene.Node pivot = new com.jme3.scene.Node("pivot");
		rootNode.attachChild(pivot); // put this node in the scene

		Vector3f [] vertices;

		ArrayList<Integer> mIndices = new ArrayList<Integer>();

		ArrayList<Float> mColors = new ArrayList<Float>();

		Mesh mesh = new Mesh();

		File amfFile = new File(filepath);

		NodeList nList = getElements(amfFile, "vertex");
		NodeList mList = getElements(amfFile,"triangle");

		vertices = new Vector3f[nList.getLength()];

		populateIndicesColors(mList, mColors, mIndices);

		populateVertices(nList, vertices);

		Vector3f [] newVertices = new Vector3f[mIndices.size()];

		ArrayList<Integer> indices = new ArrayList<Integer>();

		setFinalVertices(mIndices, indices, vertices, newVertices);

		int[] indexes = new int[indices.size()];
                
		for(int i=0; i<indices.size();i++){
			indexes[i] = indices.get(i);
		}

		mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(newVertices));
		//mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
		mesh.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indexes));

		mesh.updateBound();

		Geometry geo = new Geometry("OurMesh", mesh); // using our custom mesh object
		mat = new Material(assetManager, 
				"Common/MatDefs/Misc/Unshaded.j3md");

		if(mColors.size() > 0){
			float[] colors = new float[mColors.size()];
			for(int i=0;i<mColors.size(); i++) {
				colors[i] = mColors.get(i);
			}
			mesh.setBuffer(Type.Color, 4, colors);
			mat.setBoolean("VertexColor", true);
		} else{
			mat.setColor("Color", ColorRGBA.Blue);
		}

		mat.getAdditionalRenderState().setWireframe(true);

		geo.setMaterial(mat);
		rootNode.attachChild(geo);

		BitmapFont fnt = assetManager.loadFont("Interface/Fonts/Default.fnt");
		txt = new BitmapText(fnt, false);
		txt.setBox(new Rectangle(0, 600, 400, 100));
		txt.setSize(fnt.getPreferredSize());
		txt.setText("Click and Drag to rotate camera. Use arrow keys to move camera. JK to toggle wireframe/solid. X to close this text.");
		txt.setLocalTranslation(0, txt.getHeight(), 0);
		guiNode.attachChild(txt);


		initKeys();
		flyCam.setDragToRotate(true);
		flyCam.setMoveSpeed(7);

	}
	public String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

		Node nValue = (Node) nlList.item(0);

		return nValue.getNodeValue();
	}
}