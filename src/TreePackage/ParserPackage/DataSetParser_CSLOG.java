
package TreePackage.ParserPackage;

import MachineLearning.DataSet;
import MachineLearning.Record;
import TreePackage.NewDocNode;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
//-----------------------------------------------------------------------------------
// Tree parser,
//-----------------------------------------------------------------------------------
public class DataSetParser_CSLOG{

    
    public DataSet parse(String filename){
        
        System.out.print("Parsing: "+filename+"  ...  ");
        DataSet dataset=new DataSet();
        String str="";
        BufferedReader file;
        
        try {
           
            file= new BufferedReader(new FileReader(filename));

            
            while(true){
                str=file.readLine();  
                if(str==null)
                    break;
                dataset.add(analyzeLine(str));
                //System.out.println(i+"-  "+str);
                
            }
            
            file.close();
            
        }//end try
        catch (IOException e) {
            System.err.println("read error ("+filename+")");
        }
        
        System.out.println("done");
        return dataset;
    }
    
    private Record analyzeLine(String line){//return two string 0: classID (0,1) 1:Tree
        
        String[] strArr=line.split(" ");
        
        //the first member is class id
        String label=strArr[0];
        
        //parse tree
        String[] strArr2=new String[strArr.length-4];
        for(int i=0;i<strArr2.length;i++){
            strArr2[i]=strArr[i+4];
        }
        Node tree=this.ParseTree(strArr2);

        Record record=new Record(tree, label);
        return record;


    }
    
    private Node ParseTree(String[] intree){
        
        NewDocNode DN=new NewDocNode();
        Document doc=DN.NewDoc();
        Node currentNode=(Node)doc;
        
        for(String str:intree){
            if(str.equals("-1")){//-1: return to parent node
                currentNode=currentNode.getParentNode();
            }
            else{
                str="a"+str;
                Node node=DN.NewNodeElement(str);
                currentNode.appendChild(node);
                currentNode=node;
            }
        }
        
        return doc.getFirstChild();
    }
    
}
