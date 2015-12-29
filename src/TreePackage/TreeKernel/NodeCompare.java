
package TreePackage.TreeKernel;

import java.io.ByteArrayOutputStream;

import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;
import org.w3c.dom.*;
//-----------------------------------------------------------------------------------
// NodeCompare
//-----------------------------------------------------------------------------------
public class NodeCompare{

    public static final int STRUCTURE=1;//return only 0:not similar type and tag (node name) or 1:similar  
    public static final int STRUCTURE_CONTENT=2;//default value //return only 0 or 1: similar type, name, value, and attributes
    public static final int STRUCTURE_CONTENT_Ad=3;//return the dgree of similarity 0~1

    public int Compare(Node controlNode, Node testNode){
        return (int)Math.round(this.Compare(controlNode, testNode, STRUCTURE_CONTENT));
    }
    
    public double Compare(Node controlNode, Node testNode, int option){ 
        double result=0.0;

        //compare structure
        int res=this.CompareStructure(controlNode, testNode);

        if(option==STRUCTURE)
            return (double)res;
        else if(res==0)
            return 0;


        //compare content
        int Type=controlNode.getNodeType();

        if(Type==Node.TEXT_NODE || Type==Node.COMMENT_NODE){
            if(option==STRUCTURE_CONTENT)
                result=this.StringCompare(controlNode.getNodeValue(), testNode.getNodeValue());
            else if(option==STRUCTURE_CONTENT_Ad)
                result=this.StringCompareAd(controlNode.getNodeValue(), testNode.getNodeValue());
        }
        else if(Type==Node.ELEMENT_NODE){
            if(option==STRUCTURE_CONTENT)
                result=this.ElementCompare(controlNode, testNode);
            else if(option==STRUCTURE_CONTENT_Ad)
                result=this.ElementCompareAd(controlNode, testNode);

        }
        else if(Type==Node.DOCUMENT_NODE || Type==Node.DOCUMENT_FRAGMENT_NODE){
            result=1;
        }
        else if(Type==Node.ENTITY_NODE || Type==Node.ENTITY_REFERENCE_NODE || Type==Node.NOTATION_NODE || Type==Node.DOCUMENT_TYPE_NODE){
            result=this.StringCompare(controlNode.getNodeName(), testNode.getNodeName());
        }

        else{
            System.out.println("Error: NodeCompare: No such node type");
            result=0;
        }


        return result;
    }

    public int CompareStructure(Node controlNode, Node testNode){
        /*
        int result;

        if (controlNode.getNodeType() != testNode.getNodeType() ||
            !controlNode.getNodeName().equalsIgnoreCase(testNode.getNodeName())  )
        {
            result=0;
        }
        else
            result=1; //similar type and tag (node name)

        return result;
*/

        if (controlNode.getNodeType() != Node.ELEMENT_NODE && testNode.getNodeType() != Node.ELEMENT_NODE)
            return 1;       // 1 means same

        if (controlNode.getNodeType() != Node.ELEMENT_NODE || testNode.getNodeType() != Node.ELEMENT_NODE)
            return 0;

        //assert controlNode.getNodeType() == Node.ELEMENT_NODE && testNode.getNodeType() == Node.ELEMENT_NODE;
        String value1 = ((Element)controlNode).getAttribute("info");
        String value2 = ((Element)testNode).getAttribute("info");

        if (value1.length() == 0 || value2.length() == 0) {
            value1 = ((Element)controlNode).getAttribute("name");
            value2 = ((Element)testNode).getAttribute("name");
            return value1.equalsIgnoreCase(value2) ? 1 : 0;
        } // if (value1.length() == 0 || value2.length() == 0)
        try {
            ByteArrayOutputStream output1 = new ByteArrayOutputStream();
            ByteArrayOutputStream output2 = new ByteArrayOutputStream();
            ByteArrayOutputStream output3 = new ByteArrayOutputStream();
            XZOutputStream outxz1 = new XZOutputStream(output1, new LZMA2Options());
            outxz1.write(value1.getBytes());
            outxz1.close();
            XZOutputStream outxz2 = new XZOutputStream(output2, new LZMA2Options());
            outxz2.write(value2.getBytes());
            outxz2.close();
            XZOutputStream outxz3 = new XZOutputStream(output3, new LZMA2Options());
            outxz3.write((value1 + value2).getBytes());
            outxz3.close();
            byte[] bs1 = output1.toByteArray(), bs2 = output2.toByteArray(), bs3 = output3.toByteArray();
            long min = bs1.length > bs2.length ? bs2.length : bs1.length;
            long max = bs1.length < bs2.length ? bs2.length : bs1.length;
            return 1.0 * (bs3.length - min) / max < 0.25 ? 1 : 0;
        } catch (Exception e) {
            return 0;
        } // try - catch (Exception e)
    }

    private int StringCompare(String str1, String str2){
        if(str1.trim().equalsIgnoreCase(str2.trim()))
            return 1;
        else
            return 0;
    }

    private double StringCompareAd(String str1, String str2){

        return (double)this.StringCompare(str1, str2);
         
    }

    private double ElementCompare(Node controlNode, Node testNode){
        double result=1;
        int NumOfAttr=0;

        if(controlNode.getNodeType()!=Node.ELEMENT_NODE || testNode.getNodeType()!=Node.ELEMENT_NODE)
            return 0;
        

        NamedNodeMap controlAttr=controlNode.getAttributes();
        NamedNodeMap testAttr=testNode.getAttributes();

        if (controlAttr.getLength() != testAttr.getLength())
            result=0;

        else{
            NumOfAttr=controlAttr.getLength();
            int NumOfMatches=0;
            for(int i=0;i<NumOfAttr;i++){
                for(int j=0;j<NumOfAttr;j++){
                    if(this.StringCompare(controlAttr.item(i).getNodeName(), testAttr.item(j).getNodeName())==1){
                        if(this.StringCompare(controlAttr.item(i).getNodeValue(), testAttr.item(j).getNodeValue())==1){
                            NumOfMatches++;
                            break;
                        }
                    }//end if
                }
            }//end for

            if(NumOfMatches==NumOfAttr)
                result=1;
            else
                result=0;
        }//end else
        
        return result;
    }

    private double ElementCompareAd(Node controlNode, Node testNode){

        return this.ElementCompare(controlNode, testNode);
       
    }

    
}
