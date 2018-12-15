import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.Stack;




//Algo
/*
 *
 *  1. Get a stack <String>
 *  2. childDependency ==== if current line number is greater than last line number -> push last line to stack -> source = stack top && target = currentLine
 *  3. sameLevelDependency === if current line number is same as last line number -> source = stack top && target = currentLine
 *  4. ParentDependency ===if current Line number is less than last line number
 *               a. if line is empty or line number -4 -> move to next line
 *               b. if less than 2 from % last line number-> pop stack 1 ->  source = stack top && target=currentLine
 *                       6 to 4  => pop 1
 *                       6 to 2  => pop 2
 *
 * */


public class DFSonSBTFile {

    static Stack<String> dependencyStack;
    static File file;
    static JSONArray jsonDependencyArray;

    public static void main(String[] args) throws IOException {
        dependencyStack = new Stack<String>();
        //file = new File("/Users/gmhatre/IdeaProjects/InterviewPrep/src/SBTbrowser/DependencyTreev2.txt");
        file = new File("/Users/gmhatre/IdeaProjects/DependencyTracker/src/main/java/DependencyTreev2-ReactiveMongo.txt");
        jsonDependencyArray = new JSONArray();

        readDataFromFileAndProcess(file);
        writeJSONDependencyToFile(jsonDependencyArray);
    }

    public static void readDataFromFileAndProcess(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));

        String currentLine="";
        String lastLine="";

        while ((currentLine = br.readLine()) != null){
            //System.out.print(getSpacesFromStart(currentLine)+ "        ");
            //System.out.println(currentLine);

            if(getSpacesFromStart(lastLine)<0){
               // System.out.println("Skip Line");
            } else if(getSpacesFromStart(currentLine)<0) {
                continue;
            } else if(getSpacesFromStart(currentLine)==0 ){
                rootElementPush(currentLine);
            } else if(getSpacesFromStart(currentLine)>getSpacesFromStart(lastLine)){
                childDependency(lastLine,currentLine);
            }else if(getSpacesFromStart(currentLine)==getSpacesFromStart(lastLine)){
                sameLevelDependency(lastLine,currentLine);
            }else if(getSpacesFromStart(currentLine)<getSpacesFromStart(lastLine)){
                parentDependency(lastLine,currentLine,getSpacesFromStart(lastLine)-getSpacesFromStart(currentLine));
            }
            lastLine = currentLine;
        }
    }

    public static void rootElementPush(String string){
        dependencyStack.push(string);
    }

    public static int getSpacesFromStart(String string){
        int defaultSpaceInBeginnering = 3;
        return string.indexOf('-') - defaultSpaceInBeginnering;
    }

    public static void childDependency(String lastLine, String currentLine){
        dependencyStack.push(lastLine);
        getJsonArrayOfDependency(dependencyStack.peek(),currentLine);
    }

    public static void sameLevelDependency(String lastLine, String currentLine){
        try{
            getJsonArrayOfDependency(dependencyStack.peek(),currentLine);
        }catch (Exception e){ return; }
    }

    public static void parentDependency(String lastLine, String currentLine, int diff) {
        int popItemCount = diff / 2;
        for (int i = 0; i < popItemCount; i++) {
                dependencyStack.pop();
        }
        try{
        getJsonArrayOfDependency(dependencyStack.peek(), currentLine);
        }catch (Exception e){ return; }
    }

    private static void getJsonArrayOfDependency(String lastLine, String currentLine){
        System.out.println("source:"+lastLine.trim()+", target:"+currentLine.trim());

        JSONObject formDetailsJson = new JSONObject();
        formDetailsJson.put("source", lastLine.trim());
        formDetailsJson.put("target", currentLine.trim());
        jsonDependencyArray.add(formDetailsJson);
    }

    public static void writeJSONDependencyToFile(JSONArray jsonArray){
        try{
            FileWriter fileJsonWritter = new FileWriter("JSONDependencyMap-ReactiveMongo.json",false);
            fileJsonWritter.append(jsonDependencyArray.toJSONString());
            fileJsonWritter.close();
        }catch (Exception e){e.printStackTrace();}
    }

}

