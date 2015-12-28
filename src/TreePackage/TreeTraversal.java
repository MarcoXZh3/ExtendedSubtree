// package org.w3c.dom. help: http://download.oracle.com/javase/1.4.2/docs/api/org/w3c/dom/package-summary.html
package TreePackage;

import org.w3c.dom.*;

//-----------------------------------------------------------------------------------
// TreeTraversal
//-----------------------------------------------------------------------------------


//how to use
//TreeTraversal traverse=new TreeTraversal(tree);
//Node node=null;
//while((node=traverse.Next())!=null){
//            
//}
public class TreeTraversal{
    
    public static final int PREORDER=1;//from top to down
    public static final int POSTORDER=2;//bottom - up
            
    private int node_status=-1;//1: child done or no child 2:sibling done or no cibling-->should go to parent
    private int max_level=-1;//-1: all levels //only for preorder mode
    private int level=0;//root is 0
    private int order=PREORDER;
    private Node current_node;
    private Node root_node;

    public TreeTraversal(Node RootNode, int TraversalOrder, int MaxLevel){ //the input node is level 0
        max_level=MaxLevel;
        current_node=RootNode;
        root_node=RootNode;
        order=TraversalOrder;
        
    }
    public TreeTraversal(Node RootNode, int TraversalOrder){ 
        max_level=-1;
        current_node=RootNode;
        root_node=RootNode;
        order=TraversalOrder;
        
    }
    public TreeTraversal(Node RootNode){ //the input node is level 0
        max_level=-1;
        current_node=RootNode;
        root_node=RootNode;
        order=PREORDER;
    }
    
    public Node Next(){
        if(order==PREORDER){
            return NextPreOrder();
        }
        else if(order==POSTORDER){
            return NextPostOrder();
        }
        else{
            return null;
        }
    }
    
    private Node NextPreOrder(){
        boolean NodeFound=false;

        if(node_status==-1){//return the current node
            node_status=0;
            return current_node;
        }

        while(NodeFound==false){//loop until find next node
            if(node_status==0){//children are not analyzed
                if(current_node.hasChildNodes() && (level<max_level || max_level==-1)){
                    current_node=current_node.getFirstChild();
                    level++;
                    NodeFound=true;
                }
                else
                    node_status=1;
            }
            else if(node_status==1){//children are analyzed, time to move on next sibling
                if(level==0){//done
                    return null;
                }
                
                if(current_node.getNextSibling()==null){//no other sibling
                    node_status=2;
                }
                else{
                    current_node=current_node.getNextSibling();
                    NodeFound=true;
                    node_status=0;
                }

            }
            else if(node_status==2){//children and siblings are analyzed, time to move on to parent and then the next sibling
                current_node=current_node.getParentNode();
                node_status=1;
                level--;

                if(level==0){//done 
                    return null;
                }

            }
        }
        
        
        return current_node;
    }
    
    private Node NextPostOrder(){
        DOMProperties prop=new DOMProperties();
        int[] le=new int[1];
        
        if(node_status==-1){//return the left leaf node
                 
            current_node=prop.LeftLeafDescendant(root_node,le);
            level=le[0];              
            node_status=0;
            
        }
        
        else if(level==0)//end
            current_node=null;
                
        else if(current_node.getNextSibling()==null){//all the childeren are done
            current_node=current_node.getParentNode();
            level--;
            
        }
        else{//go down in the next sibling
            current_node=prop.LeftLeafDescendant(current_node.getNextSibling(),le);
            level=level+le[0];
            
        }

        
        
        return current_node;
    }
    

    public int GetLevel(){
        return level;
    }

    public String GetPath(){
        String str="";
        Node node=current_node;
        for(int i=0;i<=level;i++){
            str="/"+node.getNodeName()+str;
            node=node.getParentNode();
        }
        return str;
    }
    
    


}
