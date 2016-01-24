
package TreePackage.TreeKernel;

import java.io.ByteArrayOutputStream;

import org.w3c.dom.*;

import ca.eandb.jmist.framework.color.CIELab;
import ca.eandb.jmist.framework.color.CIEXYZ;
import ca.eandb.jmist.framework.color.RGB;
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

    /**
     */
    private String[] css = {
        // Position
        "css_position",
        // Background
        "css_background-color",           "css_background-image",
        // Border
        "css_border-bottom-color",        "css_border-bottom-style",         "css_border-bottom-width",
        "css_border-left-color",          "css_border-left-style",           "css_border-left-width",
        "css_border-right-color",         "css_border-right-style",          "css_border-right-width",
        "css_border-top-color",           "css_border-top-style",            "css_border-top-width",
        "css_outline-color",              "css_outline-style",               "css_outline-width",
        "css_border-bottom-left-radius",  "css_border-bottom-right-radius",
        "css_border-top-left-radius",     "css_border-top-right-radius",     "css_box-shadow",
        // Text - paragraph
        "css_direction",                  "css_letter-spacing",              "css_line-height",
        "css_text-align",                 "css_text-decoration",             "css_text-indent",
        "css_text-transform",             "css_vertical-align",              "css_white-space",
        "css_word-spacing",               "css_text-overflow",               "css_text-shadow",
        "css_word-break",                 "css_word-wrap",
        // Text - column
        /*"css_column-count",             "css_-webkit-column-count",*/      "css_-moz-column-count",
        /*"css_column-gap",               "css_-webkit-column-gap",*/        "css_-moz-column-gap",
        /*"css_column-rule-color",        "css_-webkit-column-rule-color",*/ "css_-moz-column-rule-color",
        /*"css_column-rule-style",        "css_-webkit-column-rule-style",*/ "css_-moz-column-rule-style",
        /*"css_column-rule-width",        "css_-webkit-column-rule-width",*/ "css_-moz-column-rule-width",
        /*"css_column-width",             "css_-webkit-column-width",*/      "css_-moz-column-width",
        // Text - list
        "css_list-style-image",           "css_list-style-position",         "css_list-style-type",
        // Text - font
        "css_font-family",                "css_font-size",                   "css_font-weight",
        "css_font-size-adjust",// Only Firefox supports this property
        "css_font-style",                 "css_font-variant",                "css_color"
    }; // private String[] css = { ... };


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

        double sum = 0.0;
        for (String css : this.css) {
            String value1 = ((Element)controlNode).getAttribute(css);
            String value2 = ((Element)testNode).getAttribute(css);
            if (css.contains("color") &&
                !value1.equalsIgnoreCase("transparent") && !value2.equalsIgnoreCase("transparent")) {
                System.out.println(value1 + ";" + value2); 
                String[] rgb1 = value1.substring(4, value1.length()-1).split(", ");
                String[] rgb2 = value2.substring(4, value2.length()-1).split(", ");
                CIELab lab1 = CIELab.fromXYZ(new RGB(Double.parseDouble(rgb1[0])/255.0,
                        Double.parseDouble(rgb1[1])/255.0,
                        Double.parseDouble(rgb1[2])/255.0).toXYZ(), CIEXYZ.D65);
                CIELab lab2 = CIELab.fromXYZ(new RGB(Double.parseDouble(rgb2[0])/255.0,
                                                     Double.parseDouble(rgb2[1])/255.0,
                                                     Double.parseDouble(rgb2[2])/255.0).toXYZ(), CIEXYZ.D65);
                sum += CIELab.deltaE(lab1, lab2) < 5.0 ? 1.0 : 0.0;
            } else {
                sum += value1.equalsIgnoreCase(value2) ? 1.0 : 0.0;
            } // else - if (css.contains("color") && ... )
        } // for (String css : this.css)
        return (sum / this.css.length) > 0.8 ? 1 : 0;
        /* This was used to compare nodes by base64 strings of the screenshots
        //assert controlNode.getNodeType() == Node.ELEMENT_NODE && testNode.getNodeType() == Node.ELEMENT_NODE;
        String value1 = ((Element)controlNode).getAttribute("info");
        String value2 = ((Element)testNode).getAttribute("info");

        if (value1.length() == 0 || value2.length() == 0) {
            value1 = ((Element)controlNode).getAttribute("name");
            value2 = ((Element)testNode).getAttribute("name");
            return value1.equalsIgnoreCase(value2) ? 1 : 0;
        } // if (value1.length() == 0 || value2.length() == 0)
        try {
            long min = Long.parseLong(((Element)controlNode).getAttribute("clength"));
            long max = Long.parseLong(((Element)testNode).getAttribute("clength"));
            if (min > max) {
                long tmp = min;
                min = max;
                max = tmp;
            } // if (min > max)
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            XZOutputStream outxz = new XZOutputStream(output, new LZMA2Options());
            outxz.write((value1 + value2).getBytes());
            outxz.close();
            return 1.0 * (output.toByteArray().length - min) / max < 0.25 ? 1 : 0;
        } catch (Exception e) {
            return 0;
        } // try - catch (Exception e)
        */
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
