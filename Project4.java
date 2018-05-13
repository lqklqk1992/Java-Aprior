import java.util.*;
import java.io.*;

public class Project4{

	private static final String FILENAME = "asso.csv";
	private static final double supportIndex = 0.1;
	private static final double confidence = 0.9;
	private static boolean endTag = false;
	private static int support;
	static List<List<Integer>> records=new ArrayList<List<Integer>>();
	static List<List<Integer>> allFrequentItemSet=new ArrayList<List<Integer>>();
	static List<Integer> confidenceList;

	private static List<List<Integer>> getNextCandidate(List<List<Integer>> frequentItemSet) {  
        List<List<Integer>> nextCandidateItemSet = new ArrayList<List<Integer>>();
        List<List<List<Integer>>> group= new ArrayList<List<List<Integer>>>();
        List<List<Integer>> tmpgroup= new ArrayList<List<Integer>>();
        List<Integer> list = new ArrayList<Integer>();
        //group the last frequent itemset by first n-1 items
        for(int i=0;i<frequentItemSet.size();i++){
        	List<Integer> tmplist = new ArrayList<Integer>();
        	for(int j=0;j<frequentItemSet.get(i).size()-1;j++){
        		tmplist.add(frequentItemSet.get(i).get(j));
        	}
        	if(listsequal(list,tmplist)){
        		tmpgroup.add(frequentItemSet.get(i));
        	}else{
        		group.add(tmpgroup);
        		tmpgroup=new ArrayList<List<Integer>>();
        		tmpgroup.add(frequentItemSet.get(i));
        		list=tmplist;
        	}
        	if(i==frequentItemSet.size()-1)
        		group.add(tmpgroup);
        }  
        //combine every pair of itemsets in the same group and add to the new candidate itemset
        for(int i=0;i<group.size();i++){
        	for(int j=0;j<group.get(i).size();j++){
        		for(int k=j+1;k<group.get(i).size();k++){
        			List<Integer> tmp = new ArrayList<Integer>(group.get(i).get(j));
        			int n=group.get(i).get(k).size()-1;
        			tmp.add(group.get(i).get(k).get(n));
        			nextCandidateItemSet.add(tmp);
        		}
        	}
        }
        return nextCandidateItemSet;  
    } 

    private static boolean listsequal(List<Integer> l1,List<Integer> l2){
    	if(l1.size()!=l2.size())
    		return false;
    	for(int i=0;i<l1.size();i++){
    		if(l1.get(i)!=l2.get(i))
    			return false;
    	}
    	return true;
    }

	private static List<List<Integer>> getSupportItemset(List<List<Integer>> candidateItemSet) {  

        boolean end = true;  
        List<List<Integer>> supportedItemset = new ArrayList<List<Integer>>();  
        int k = 0;  
          
        for (int i = 0; i < candidateItemSet.size(); i++){  
              
            int count = countFrequent(candidateItemSet.get(i));
              
            if (count >= support){     
                supportedItemset.add(candidateItemSet.get(i));  
                end = false;  
            }  
        }  
        endTag = end;  
        if(endTag==true)  
            System.out.println("No more itemset found!");  
        return supportedItemset;  
    } 

    private static List<List<List<Integer>>> getConfidenceItemset(List<List<Integer>> candidateItemSet,BufferedWriter bufw) throws IOException {  

        boolean end = true; 
        List<List<List<Integer>>> res= new ArrayList<List<List<Integer>>>();
        List<List<Integer>> confidenceItemset = new ArrayList<List<Integer>>();  
        List<List<Integer>> complementarySet = new ArrayList<List<Integer>>();
        int k = 0;  
          
        for (int i = 0; i < candidateItemSet.size(); i++){  
            List<Integer> tmp = new ArrayList<Integer>(confidenceList);
            for(int j=0;j<candidateItemSet.get(i).size();j++){
            	tmp.remove(candidateItemSet.get(i).get(j));
            }

            int count1 = countFrequent(confidenceList);
            int count2 = countFrequent(tmp);

            double ratio=(double)count1/count2;
            if (ratio >= confidence){     
                confidenceItemset.add(candidateItemSet.get(i)); 
                complementarySet.add(tmp); 

                System.out.print(tmp+"->");
                System.out.print(candidateItemSet.get(i)+" ");
                System.out.print("Support:"+tmp+"="+count2+","+confidenceList+"="+count1+" ");
            	System.out.print("Confidence:"+ratio+"\n");

            	String str1="";
            	for(int j=0;j<tmp.size();j++){
            		str1=str1+Integer.toString(tmp.get(j))+",";
            	}
            	str1=str1.substring(0,str1.length()-1);

            	String str2="";
            	for(int j=0;j<candidateItemSet.get(i).size();j++){
            		str2=str2+Integer.toString(candidateItemSet.get(i).get(j))+",";
            	}
            	str2=str2.substring(0,str2.length()-1);

                bufw.write(str1+"->"+str2+" Support:"+count2+","+count1+" Confidence:"+ratio+"\r\n");
                end = false;  
            }  
        }  
        endTag = end;  
        if(endTag==true)  
            System.out.println("No more rules found!");
        res.add(confidenceItemset);
        res.add(complementarySet);  
        return res;  
    } 

    private static int countFrequent(List<Integer> list) {   
        int count = 0;  
        for(int i = 0; i<records.size(); i++) {  
              
            boolean itemsetNotFound = false;  
              
            for (int k=0; k < list.size(); k++){
                boolean thisRecordFound = false;  
                for(int j=0; j<records.get(i).size(); j++){  
                    if(list.get(k)==records.get(i).get(j)){
                        thisRecordFound = true;
                        break;  
                    }
                }  
                if(!thisRecordFound){
                    itemsetNotFound = true;  
                    break;  
                }  
            }  
              
            if(itemsetNotFound == false)  
                count++;  
              
        }  
        return count;  
    }  

	public static void main(String[] args) throws IOException {
		System.out.println("Step1--Read file");
		FileReader fr = new FileReader(FILENAME);
		BufferedReader br = new BufferedReader(fr);

		String sCurrentLine;
		int numberOfItems=0;

		while ((sCurrentLine = br.readLine()) != null) {
			List<Integer> tmp=new ArrayList<Integer>();
			String[] data=sCurrentLine.split(",");
			for(int i=1;i<data.length;i++){
				int item=Integer.parseInt(data[i].trim());
				tmp.add(item);
				if(item>numberOfItems)
					numberOfItems=item;
			}
			records.add(tmp);
		}
		br.close();
		fr.close();
		support=(int)(records.size()*supportIndex);

		//support
		System.out.println("\nStep2--Support");
		List<List<Integer>> candidateItemSet = new ArrayList<List<Integer>>();
		for(int i=0;i<=numberOfItems;i++){
			List<Integer> list = new ArrayList<Integer>();
			list.add(i);
			candidateItemSet.add(list);
		}
		List<List<Integer>> frequentItemSet = getSupportItemset(candidateItemSet);


		//***************Iteration**************  
        while(endTag!=true){  
            //**********get new candidate itemset from frequest itemset**************  
            List<List<Integer>> nextCandidateItemSet = getNextCandidate(frequentItemSet);  

            //*************get new frequent itemset from new candidate itemset****************  
            List<List<Integer>> nextFrequentItemSet = getSupportItemset(nextCandidateItemSet);  
               
            for(int i=0;i<nextFrequentItemSet.size();i++){  
                allFrequentItemSet.add(nextFrequentItemSet.get(i)); 
            }  
            //****************for next iteration use********************  
            frequentItemSet = nextFrequentItemSet;  
        }

        System.out.println(allFrequentItemSet);  


        //confidence and write the result to file
        System.out.println("\nStep3--Confidence and write result to file");  
        FileWriter fw = new FileWriter("result.txt"); 
		BufferedWriter bufw = new BufferedWriter(fw);
		bufw.write("Association rules:\r\n");

        for(int i=0;i<allFrequentItemSet.size();i++){  
        	endTag=false;
            confidenceList = allFrequentItemSet.get(i);
            candidateItemSet = new ArrayList<List<Integer>>(); 
            for(int j=0;j<confidenceList.size();j++){
            	List<Integer> tmp = new ArrayList<Integer>();
            	tmp.add(confidenceList.get(j));
            	candidateItemSet.add(tmp);
            } 

            List<List<List<Integer>>> res=getConfidenceItemset(candidateItemSet,bufw);
            List<List<Integer>> confidenceItemSet = res.get(0);
            List<List<Integer>> complementarySet = res.get(1);

            //***************Iteration**************  
        	while(endTag!=true){  
            	//**********get new candidate itemset from confidence itemset**************  
            	List<List<Integer>> nextCandidateItemSet = getNextCandidate(confidenceItemSet);  
            	if(nextCandidateItemSet.size()==1){
            		System.out.println("No more rules found!");
            		break;
            	}

            	//*************get new confidence itemset from new candidate itemset****************
            	res = getConfidenceItemset(nextCandidateItemSet,bufw);
            	List<List<Integer>> nextConfidenceItemSet = res.get(0);  
               	List<List<Integer>> nextComplementarySet = res.get(1);  
 
            	//****************for next iteration use********************  
            	confidenceItemSet = nextConfidenceItemSet;  
        	}
        } 
        System.out.println("\nPls check result.txt to see association rules that meet all the requirements"); 
        bufw.close();
        fw.close();
          
    } 

}

