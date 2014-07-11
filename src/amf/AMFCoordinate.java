package amf;

public class AMFCoordinate{
  float x, y, z;
  float nx, ny, nz;
  float r, g, b, a;
  boolean NORMAL=false;
  boolean COLOR = false;
  public AMFCoordinate(float x1, float y1, float z1){
    x=x1;
    y=y1;
    z=z1;
  }
  
  public AMFCoordinate(float x1, float y1, float z1, float nx1, float ny1, float nz1){
    nx = nx1;
    ny = ny1;
    nz = nz1;
    x = x1;
    y = y1;
    z = z1;
    NORMAL = true;
  }
  
  public void setCoordinates(float x1, float y1, float z1){
    x=x1;
    y=y1;
    z=z1;
  }
  
  public void setNormals(float nx1, float ny1, float nz1){
    nx= nx1;
    ny = ny1;
    nz = nz1;
  }
  
  public void setColors(float r1, float g1, float b1, float a1){
    r=r1;
    g=g1;
    b=b1;
    a=a1;
    COLOR = true;
  }
  
  public String coordinatesToString(){
    return "x: " + x + ", y: " + y + ", z: " + z;
  }
  
  public String normalToString(){
    return ", nx: " + nx + ", ny: " + ny + ", nz: " + nz;
  }
  
  public String colorToString(){
    return ", r: " + r + ", g: " + g + ", b: " + b + ", a: " + a;
  }
  
  public String toString(){
    String out = coordinatesToString();
    if(NORMAL){
      out = out.concat(normalToString());
    }
    if(COLOR){
      out = out.concat(colorToString());
    }
    return out;
  }
}
