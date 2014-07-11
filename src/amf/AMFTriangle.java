package amf;

public class AMFTriangle{
  AMFCoordinate v1, v2, v3;
  int index1, index2, index3;
  public AMFTriangle(AMFCoordinate v1, AMFCoordinate v2, AMFCoordinate v3, int i1, int i2, int i3){
    this.v1 = v1;
    this.v2 = v2;
    this.v3 = v3;
    index1 = i1;
    index2 = i2;
    index3 = i3;
  }
  
  @Override
  public String toString(){
    return "V1: {" + v1 + "}, V2: {" + v2 + "}, V3: {" + v3 + "}";
  }
  
  public int[] indexArray(){
      int[] temp = new int[3];
      temp[0] = index1;
      temp[1] = index2;
      temp[2] = index3;
      return temp;
  }
}