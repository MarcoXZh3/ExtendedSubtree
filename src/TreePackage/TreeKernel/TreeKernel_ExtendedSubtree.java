

package TreePackage.TreeKernel;

import MachineLearning.Kernel.AbstractKernelM;
import TreePackage.TreeTraversal;
import TreePackage.ParserPackage.XMLParser_Java;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//-----------------------------------------------------------------------------------
// TreeCompare: SubTreeMatching (my proposed approach)-- finds all sub trees
//-----------------------------------------------------------------------------------
public class TreeKernel_ExtendedSubtree extends AbstractKernelM{
    
    
    //coefficiant
    public double alfa=2;//to amplify the worth of larger subtrees//1~inf
    public double beta=0.5;// 0~1, to reduce the worth of subtrees with different structural position (level), or low level
 
    public static String kernelName="ESubtree";
    
    
    class Mapping{
        double controlSubtreeWeight=0;
        double testSubtreeWeight=0;
        List<Integer> controlSubtree=new ArrayList();//contains all nodes' index in the mapping
        List<Integer> testSubtree=new ArrayList();
        
        @Override
        public String toString(){           
            String str="";
            str+="SubtreeWeight_CT("+controlSubtreeWeight+" - "+testSubtreeWeight+") ";
            
            String strControl="";
            for(int i=0;i<controlSubtree.size();i++){
                strControl+=controlSubtree.get(i).toString();
                strControl+="  ";
            }
            String strTest="";
            for(int i=0;i<testSubtree.size();i++){
                strTest+=testSubtree.get(i).toString();
                strTest+="  ";
            }
            
            str+="Subtree_CT("+controlSubtree.size()+": "+strControl+"--  "+testSubtree.size()+": "+strTest+")";
            
            return str;
        }
    }
    
    
    List<Node> controlNodeList;
    List<Node> testNodeList;

    ArrayList<Integer> controlNodeLevel;
    ArrayList<Integer> testNodeLevel;
    
    
    public double getSimilarityNormalized(Node controlNode, Node testNode){

        double score=this.getSimilarity(controlNode, testNode);
        
        //normalization
        //score=score/(controlNodeList.size()+ testNodeList.size());
        score=score/Math.max(controlNodeList.size(), testNodeList.size());
        score=score/2;
        
//        if(score>1){
//            TreePrint print=new TreePrint();
//            print.Print(testNode, "testTree.txt");
//            print.Print(controlNode, "controlTree.txt");
//        }

        return score;
        
    }
    
    public double getDistanceNormalized(Node controlNode, Node testNode){
        return 1-this.getSimilarityNormalized(controlNode, testNode);
    }
    
    public double getSimilarity(Node controlNode, Node testNode){
        Mapping[][] mappingMatrix=this.getMappings(controlNode, testNode);
        Mapping[] controlNodesLargestMapping=getControlNodesLargestMapping(mappingMatrix);
        Mapping[] testNodesLargestMapping=getTestNodesLargestMapping(mappingMatrix);
        fillSubtreeWeightsInMappings(mappingMatrix, controlNodesLargestMapping, testNodesLargestMapping);
        double score=this.analyseMappings(mappingMatrix);
        
        return score;
        
    }
    
    private double analyseMappings(Mapping[][] mappingMatrix){
        

        //coefficiants
        double gamma, gamma0=1;//gamma0 is a coefficiant used to calc gamma, gamma reduce the worth of mapping with high level
        //gamma0 is the weight of mapping in the highest level, bottom node.
        //gamma=Max(controlGamma, testGamma)
        //controlGamma=gamma0+(1-gamma0)*(1-nodelevel/controlDepth)
        
        //result
        double similarityScore=0;
        //tree depth measure
        /*double*/ int controlDepth=Collections.max(controlNodeLevel);
        /*double*/ int testDepth=Collections.max(testNodeLevel);
        
        //apply alfa and beta
        for(int c=0;c<controlNodeList.size();c++){
            for(int t=0;t<testNodeList.size();t++){
                if(mappingMatrix[c][t]!=null){
                    double mappingWeight=mappingMatrix[c][t].controlSubtreeWeight + mappingMatrix[c][t].testSubtreeWeight;
                    mappingWeight=Math.pow(mappingWeight, alfa);
                    //beta if required
                    if(controlNodeLevel.get(c).intValue()!=testNodeLevel.get(t).intValue())
                        mappingWeight*=beta;
                    //gamma if required
//                    double controlGamma=gamma0+(1-gamma0)*(1-controlNodeLevel.get(c).doubleValue()/controlDepth);
//                    double testGamma=gamma0+(1-gamma0)*(1-testNodeLevel.get(t).doubleValue()/testDepth);
//                    gamma=Math.max(controlGamma, testGamma);
//                    mappingWeight*=gamma;
                    //
                    similarityScore+=mappingWeight;
                }
            }
        }
        
        similarityScore=Math.pow(similarityScore, 1/alfa);
        return similarityScore;
    }
    
    private void fillSubtreeWeightsInMappings(Mapping[][] mappingMatrix, Mapping[] controlNodesLargestMapping, Mapping[] testNodesLargestMapping){
        
        for(int i=0;i<controlNodesLargestMapping.length;i++){
            if(controlNodesLargestMapping[i]!=null)
                controlNodesLargestMapping[i].controlSubtreeWeight++;
        }
        
        for(int i=0;i<testNodesLargestMapping.length;i++){
            if(testNodesLargestMapping[i]!=null)
                testNodesLargestMapping[i].testSubtreeWeight++;
        }
        
    }
    private Mapping[] getControlNodesLargestMapping(Mapping[][] mappingMatrix){
        
        //gen NodeLargestMappingList
        Mapping[] nodesLargestMapping=new Mapping[controlNodeList.size()];
        //go through all the mappings and the all the nodes in each mapping
        for(int c=0;c<controlNodeList.size();c++){            
            for(int t=0;t<testNodeList.size();t++){
                
                if(mappingMatrix[c][t]==null)
                    continue;
                
                for(Integer nodeIndex: mappingMatrix[c][t].controlSubtree){
                    if(nodesLargestMapping[nodeIndex]==null)
                        nodesLargestMapping[nodeIndex]=mappingMatrix[c][t];
                    else if(nodesLargestMapping[nodeIndex].controlSubtree.size()<=mappingMatrix[c][t].controlSubtree.size())
                        nodesLargestMapping[nodeIndex]=mappingMatrix[c][t];
                }
            }
        }
        
        return nodesLargestMapping;
        
    }
    
    private Mapping[] getTestNodesLargestMapping(Mapping[][] mappingMatrix){
        
        //gen NodeLargestMappingList
        Mapping[] nodesLargestMapping=new Mapping[testNodeList.size()];
        //go through all the mappings and the all the nodes in each mapping
        for(int c=0;c<controlNodeList.size();c++){            
            for(int t=0;t<testNodeList.size();t++){
                
                if(mappingMatrix[c][t]==null)
                    continue;
                
                for(Integer nodeIndex: mappingMatrix[c][t].testSubtree){
                    if(nodesLargestMapping[nodeIndex]==null)
                        nodesLargestMapping[nodeIndex]=mappingMatrix[c][t];
                    else if(nodesLargestMapping[nodeIndex].testSubtree.size()<=mappingMatrix[c][t].testSubtree.size())
                        nodesLargestMapping[nodeIndex]=mappingMatrix[c][t];
                }
            }
        }
        
        return nodesLargestMapping;
        
    }
    
    private Mapping[][] getMappings(Node controlNode, Node testNode){
        NodeCompare nodeCompare=new NodeCompare();
        
        controlNodeList=new ArrayList();
        testNodeList=new ArrayList();
        controlNodeLevel=new ArrayList();
        testNodeLevel=new ArrayList();
        
        TreeTraversal controlTraverse=new TreeTraversal(controlNode,TreeTraversal.POSTORDER);
        TreeTraversal testTraverse=new TreeTraversal(testNode,TreeTraversal.POSTORDER);

        
        //preprossec
        Node node=null;
        while((node=controlTraverse.Next())!=null){
            controlNodeList.add(node);
            controlNodeLevel.add(new Integer(controlTraverse.GetLevel()));
        }
        
        node=null;
        while((node=testTraverse.Next())!=null){
            testNodeList.add(node);
            testNodeLevel.add(new Integer(testTraverse.GetLevel()));
        }
        
        
        
        Mapping[][] mappingMatrix=new Mapping[controlNodeList.size()][testNodeList.size()];
        
        //scan all nodes        
        long t0 = System.currentTimeMillis();
        for(int c=0;c<controlNodeList.size();c++){            
            for(int t=0;t<testNodeList.size();t++){
                if(nodeCompare.CompareStructure(controlNodeList.get(c), testNodeList.get(t))!=0){
                    mappingMatrix[c][t]=new Mapping();
                    mappingMatrix[c][t].controlSubtree.add(c);
                    mappingMatrix[c][t].testSubtree.add(t);
                    
                    if(controlNodeList.get(c).hasChildNodes() && testNodeList.get(t).hasChildNodes())
                        mappingMatrixUpdate( c,  t, mappingMatrix);
                }
                else{
                    mappingMatrix[c][t]=null;
                }
            }
//            if (c % 100 == 0)
                System.out.println(String.format("Comparing: %d/%d VS %d: %ds", c, controlNodeList.size(),
                                                 testNodeList.size(), (System.currentTimeMillis() - t0) / 1000));
        }

        return mappingMatrix;
 
    }
 
    private void mappingMatrixUpdate(int cnodeId, int tnodeId, Mapping[][] mappingMatrix){
        
        Node cnode=controlNodeList.get(cnodeId);
        Node tnode=testNodeList.get(tnodeId);
        Mapping mapping=mappingMatrix[cnodeId][tnodeId];
        
        NodeList controlChildList = cnode.getChildNodes();
        int m = controlChildList.getLength();
        NodeList testChildList = tnode.getChildNodes();
        int n = testChildList.getLength();
        
        //find the largest subtree
        int[][] MAT = new int[m+1][n+1]; //MAT Score
        int[][] MATA = new int[m+1][n+1]; //MAT Score Accumulative

        for (int i=1; i<=m; i++) {
            for (int j=1; j<=n; j++){
                int c=controlNodeList.indexOf(controlChildList.item(i-1));
                int t=testNodeList.indexOf(testChildList.item(j-1));
                
                if(mappingMatrix[c][t]==null)
                    MAT[i][j]=0;
                else
                    MAT[i][j]=mappingMatrix[c][t].controlSubtree.size();//or testSubtree.size(), has same size
                                
                MATA[i][j] = Math.max(MATA[i][j-1], MATA[i-1][j]);
                MATA[i][j] = Math.max(MATA[i][j], MATA[i-1][j-1] + MAT[i][j]);
            }
        }
        
        //find used ones 1: used child nodes
        int i=m;
        int j=n;
        //int[] MATControlUsed=new int[m+1]; //1:used 0:not used
        //int[] MATTestUsed=new int[n+1]; //1:used 0:not used
        
        while(i>0 && j>0){
            if(MATA[i][j]==MATA[i-1][j-1] + MAT[i][j]){
                int c=controlNodeList.indexOf(controlChildList.item(i-1));
                int t=testNodeList.indexOf(testChildList.item(j-1));
                //
                if(MAT[i][j]>0){
                    //c, t are index of nodes (children of cnode and tnode)that matched 
                    mapping.controlSubtree.addAll(mappingMatrix[c][t].controlSubtree);
                    mapping.testSubtree.addAll(mappingMatrix[c][t].testSubtree);
                }
                
                //MATControlUsed[i]=1;
                //MATTestUsed[j]=1;
                
                i--;
                j--;
            }
            else if(MATA[i][j]==MATA[i][j-1]){
                 
                j--;
            }   
            else{
                 
                i--;
            }
                       
        }

    }
    
    
    @Override
    public double getK(Object objA, Object objB){
        if(!(objA instanceof Node && objB instanceof Node)){
            throw new IllegalArgumentException("The input object to getK in a TreeKernel is not a Node instance");
        }
        
        return this.getDistanceNormalized((Node)objA, (Node)objB);

    }

    @Override
    public String getName() {
        return kernelName;
    } 
            
            
            

    /**
     * Traverse a tree in post-order and save all nodes to an array
     * @param root      {@code Node} the root of the tree
     * @param results   {@code ArrayList<Node>} the result array
     */
    private static void traverseSubtree(Node root, ArrayList<Node> results) {
        assert root != null;
        if (root.getNodeType() != Node.ELEMENT_NODE)
            return ;
        Node child = root.getFirstChild();
        if (child != null)
            traverseSubtree(child, results);
        while ((child = child.getNextSibling()) != null)
            traverseSubtree(child, results);
        results.add(root);
    } // private static void traverseSubtree(Node root, ArrayList<Node> results)

    /**
     * Run the test on all test cases
     * @param treeType      {@code String} type of the tree
     * @throws Exception    any possible exception
     */
    public static void runTest(String treeType) throws Exception {
        String logFile = "databases/EST-" + treeType + ".txt";
        FileWriter output = new FileWriter(new File(logFile));
        output.close();

        for (int i = 0; i < 10; i++) {
            File folder = new File(String.format("databases/Subset%02d/", i+1));
            System.out.println(String.format("databases/Subset%02d/", i+1));

            // Pares each XML file
            System.out.print("  Parsing Files: ");
            File[] files = folder.listFiles();
            int idx = 0;
            ArrayList<ArrayList<Node>> trees = new ArrayList<ArrayList<Node>>();
            ArrayList<String> fileNames = new ArrayList<String>();
            for (File f: files) {
                if (!f.getName().contains(treeType))
                    continue ;
                fileNames.add(f.toString());
                ArrayList<Node> treeNodes = new ArrayList<Node>();
                Node root = new XMLParser_Java().parse(f.toString()).getDocumentElement();
                traverseSubtree(root, treeNodes);
                trees.add(treeNodes);
                System.out.print(((idx++) / 10 + 1) + "");
            } // for (File f: files)
            System.out.println();

            // Cross compare each pair of documents
            idx = 0;
            for (int j = 0; j < trees.size(); j++) {
                for (int k = j+1; k < trees.size(); k++) {
                    long t1 = System.currentTimeMillis();
                    ArrayList<Node> tree1 = trees.get(j), tree2 = trees.get(k);
                    Node root1 = tree1.get(tree1.size() - 1), root2 = tree2.get(tree2.size() - 1);
                    TreeKernel_ExtendedSubtree kernal = new TreeKernel_ExtendedSubtree();
                    kernal.controlNodeList = new ArrayList<Node>();
                    for (int m = 0; m < tree1.size(); m ++)
                        kernal.controlNodeList.add(tree1.get(m));
                    kernal.testNodeList = new ArrayList<Node>();
                    for (int m = 0; m < tree2.size(); m ++)
                        kernal.testNodeList.add(tree2.get(m));
                    double est = kernal.getSimilarityNormalized(root1, root2);
                    long t2 = System.currentTimeMillis();
                    String log = String.format("  Group: %2d, %4d/1176: %s, %s, EST=%.4f, time=%ds",
                                               i+1, ++idx, fileNames.get(j), fileNames.get(k), est, (t2 - t1) / 1000);
                    System.out.println(log);
                    output = new FileWriter(new File(logFile), true);
                    output.write(log + "\n");
                    output.close();
                } // for (int k = j+1; k < trees.size(); k++)
            } // for (int j = 0; j < trees.size(); j++)
        } // for (int i = 0; i < 10; i++)
    } // public static void runTest(String treeType) throws Exception

    /**
     * Update a sub DOM tree of the XML
     * @param node      {@code Node} root node of the sub tree
     * @param attr      {@code String} the attribute to be compressed
     */
    private static void updateXMLNode(Node node, String attr) {
        if (node.getNodeType() != Node.ELEMENT_NODE)
            return ;
        String value = ((Element) node).getAttribute(attr);
        assert value != null && value.length() > 0;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            XZOutputStream outxz = new XZOutputStream(output, new LZMA2Options());
            outxz.write(value.getBytes());
            outxz.close();
        } catch (Exception e) {
            e.printStackTrace();
        } // try - catch (Exception e)
        ((Element) node).setAttribute("clength", output.toByteArray().length + "");

        Node child = node.getFirstChild();
        if (child != null)
            updateXMLNode(child, attr);
        while ((child = child.getNextSibling()) != null)
            updateXMLNode(child, attr);
    } // private static void updateXMLNode(Node node, String attr)

    /**
     * Add a new attribute "{@code clength}" to each XML element
     */
    public static void addAttrToXMLs() {
        for (int i = 0; i < 10; i++) {
            File folder = new File(String.format("databases/Subset%02d/", i+1));
            System.out.println(String.format("databases/Subset%02d/", i+1));

            File[] files = folder.listFiles();
            for (int j = 0; j < files.length; j++) {
                File f = files[j];
                Document doc = new XMLParser_Java().parse(f.toString());
                updateXMLNode(doc.getDocumentElement(), f.getName().contains("DT") ? "name" : "info");
                try {
                    Transformer transformer = TransformerFactory.newInstance().newTransformer();
                    Result output = new StreamResult(new File(f.toString()));
                    Source input = new DOMSource(doc);
                    transformer.transform(input, output);
                } catch (Exception e) {
                    e.printStackTrace();
                } // try - catch (Exception e)
                System.out.println(String.format("  Group %d - %d/%d: %s", i+1, j+1, files.length, f.getName()));
            } // for (int j = 0; j < files.length; j++)
        } // for (int i = 0; i < 10; i++)
    } // public static void addAttrToXMLs()

    /**
     * Main entry: run the test cases
     * @param args          {@code String[]} command line arguments
     * @throws Exception    any possible exception
     */
    public static void main(String[] args) throws Exception {
//        addAttrToXMLs();

        runTest("BT");
//        runTest("DT");
//        runTest("LT");
    } // public static void main(String[] args)

}
