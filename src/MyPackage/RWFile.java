package MyPackage;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ali Shahbazi
 */


import java.io.*;
import java.util.*;



public class RWFile {
    
    protected String fileName;
    static public enum WriteType{Append, Create}
    
    public RWFile(String fileName, WriteType type){//rewriteExistingFile=true: the existing file is deleted and a new one is created
        //rewriteExistingFile=false: keep the existing file and create another one
        
        if(type==WriteType.Append)
            this.fileName=fileName;
        else if(type==WriteType.Create){
            File file=new File(fileName);
            if(file.exists()==true)
                file.delete();
        }

        
        this.fileName=fileName;
    }
    
    public RWFile(String fileName){
        this(fileName, WriteType.Append);
    }

    
    public void print(String data){

        try {
            FileWriter file=new FileWriter(fileName,true);//true: append to the file
            file.write(data);

            file.close();
            //System.out.println("write complete ("+filename+")");
        }
        catch (IOException e) {
            System.out.println("write error ("+fileName+")");
        }

    }
    public void println(String data){
        this.print(data+"\r\n");
    }
    
    
    
    
}
