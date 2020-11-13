import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.*;

public class KenKenSolver /*extends JFrame*/ {

	// Common stuff for a puzzle.
	public static int size;
	public static int numCages;
	public static cage[] myCages;
	//Puzzle array
	public static int[][] puzzleArray;
	//Was a solution found
	public static boolean puzzleFinished = false;

	public static void main(String[] args) {
	
	String file = "puzzle_files/9x9_3.txt";
	JFrame frame = new JFrame();
	
		//Scan in puzzle set-up info -----------------------------------------------------
		ArrayList<String> eachLine = new ArrayList<String>();
		try {
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String strLine;
		//Read File Line By Line
		while ((strLine = br.readLine()) != null)   {
		  
		  //Remove Commas
		  strLine = strLine.replace(",","");
		  strLine.trim();
		  eachLine.add(strLine);
		}
		
		eachLine.remove(eachLine.size() - 1);
		br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	   //----------------------------------------------------------------------------------
	   
	   //Break scanned text into separate cages--------------------------------------------
	   //An arraylist of an arraylist of cage data
	   ArrayList<ArrayList<String>> eachCage = new ArrayList<ArrayList<String>>();
	   
	   //Get size and # of cages
		size = Integer.parseInt(eachLine.remove(0));
		numCages = Integer.parseInt(eachLine.remove(0));
		myCages = new cage[numCages];
		puzzleArray = new int[size][size];
		
		Iterator<String> iterateOne = eachLine.iterator();
		ArrayList<String> temp = new ArrayList<String>();
		boolean start = true;
		while(iterateOne.hasNext()) {
		
			String s = iterateOne.next();
			if(s.contains(" ") || s.contains("+") || s.contains("-") || s.contains("*") || s.contains("/")) {
			
				if(start == false) eachCage.add(temp);
				start = false;
				temp = new ArrayList<String>();
				temp.add(s);
			}
			else temp.add(s);
		}
		eachCage.add(temp);
	   //----------------------------------------------------------------------------------
	   
	   //Create Cages----------------------------------------------------------------------   
	   Iterator<ArrayList<String>> iterateTwo = eachCage.iterator();
	   int cageCount = 0;
	   while(iterateTwo.hasNext()) {
	   
			temp = new ArrayList<String>();
			temp = iterateTwo.next();
			myCages[cageCount] = new cage(temp);
			cageCount++;
	   }
	   //----------------------------------------------------------------------------------
	   
	   	//Setup puzzleArray --- All Zeros-----------------
		for(int i = 0; i < size; i++) {
		
			for(int j = 0; j < size; j++) {
			
			puzzleArray[i][j] = 0;
			System.out.print(puzzleArray[i][j] + " ");
			}
			System.out.println("");
		}
		//------------------------------------------------
		System.out.println("Solving...");
		//Test For Solution-------
		puzzleFinished = solveKenKen(0, 0);
		if(puzzleFinished == true) {

			System.out.println("Solution Found");
			//Prints Solution
			for(int i = 0; i < size; i++) {
				for(int j = 0; j < size; j++) {
				
					System.out.print(puzzleArray[i][j] + " ");
				}
				System.out.println("");
			}
		}
		else {
		
			System.out.println("No Solution Found");
			for(int i = 0; i < size; i++) {
				for(int j = 0; j < size; j++) {
				
					System.out.print(puzzleArray[i][j] + " ");
				}
				System.out.println("");
			}
		}
		//----------
		try {
		
			KenKenComponent kc = new KenKenComponent(file, frame);
			kc.setNumber(puzzleArray);
		}
		catch(Exception e) {
		
			e.printStackTrace();
		}
	}

	public static int pos2Cage(int r, int c) {

		String s = "" + r + c;
		for(int i = 0; i < numCages; i++) {
		
			if(myCages[i].boxSpots.contains(s)) return i;
		}
		return 0;
	}	

	public static boolean solveKenKen(int r, int c) {
		
		if(c == size) {
			c = 0;
			r = r + 1;
		}
		if(r == size) return true;
		else {
			
			for(int i = 1; i <= size; i++) {
			
				puzzleArray[r][c] = i;
				myCages[pos2Cage(r,c)].addNum(i);
				boolean p = isLegal(r, c);
				
				if(!p) myCages[pos2Cage(r,c)].removeNum();
				if(p) {
					
					boolean q = solveKenKen(r, c + 1);
						
						if(!q) myCages[pos2Cage(r,c)].removeNum();
						if(q) return true;
				}
			}
		return false;
			}
	}
	
	public static boolean isLegal(int r,int c) {
	
	boolean checkR = false;
	boolean checkC = false;
	boolean cageValue = false;
	
		checkR = checkRow(r, c);
			if(checkR == false && puzzleArray[r][c] == size) {
			
			//if puzzleArray[r][c] == size --- check is previous spot = size --- if yes set to 0
			boolean stop = false;
				for(int i = r; i >= 0; i--) {
					
					for(int j = c; j >= 0; j--) {
						
						if(puzzleArray[i][j] == size) puzzleArray[i][j] = 0;
						else {
							stop = true;
							break;
						}
					}
					if(stop == true) break;
					c = size - 1;
				}
				return false;
			}
			else if(checkR == false) return false;
			
		checkC = checkCol(r, c);
		
			//If value at column 0 = size --> check if previous spot = size i.e row(r - 1) & col(size)
			if(checkC == false && puzzleArray[r][c] == size && c == 0){
				boolean stop = false;
				for(int i = r; i >= 0; i--) {
					
					for(int j = c; j >= 0; j--) {
						
						if(puzzleArray[i][j] == size) puzzleArray[i][j] = 0;
						else {
							stop = true;
							break;
						}
					}
					if(stop == true) break;
					c = size - 1;
				}
				return false;
			}
			else if(checkC == false && puzzleArray[r][c] == size) {
			
				puzzleArray[r][c] = 0;
				return false;
			}
			else if(checkC == false) return false;
		
		cageValue = checkCage(r,c);
			if(cageValue == false) {
				
				boolean stop = false;
				for(int i = r; i >= 0; i--) {
					
					for(int j = c; j >= 0; j--) {
						
						if(puzzleArray[i][j] == size) puzzleArray[i][j] = 0;
						else {
							stop = true;
							break;
						}
					}
					if(stop == true) break;
					c = size - 1;
				}
				return false;
			}
		
		if(checkR == true && checkC == true && cageValue == true) {
		
			return true;
		}
		else return false;
	}
	
	public static boolean checkRow(int r, int c) {
	
		//True means no duplicate		
		boolean dupe = true;
		for(int i = 0; i < size; i++) {
		
			for(int j = i + 1; j < size; j++) {
			
				if(j != i && puzzleArray[r][j] == puzzleArray[r][i] && (puzzleArray[r][j] != 0 && puzzleArray[r][i] != 0)) dupe = false;
			}
		}
		return dupe;
	}
	
	public static boolean checkCol(int r, int c) {
	
		//True means no duplicate	
		boolean dupe = true;
		for(int i = 0; i < size; i++) {
		
			for(int j = i + 1; j < size; j++) {
			
				if(j != i && puzzleArray[j][c] == puzzleArray[i][c] && (puzzleArray[j][c] != 0 && puzzleArray[i][c] != 0)) dupe = false;
			}
		}
		return dupe;
	}
	
	public static boolean checkCage(int r, int c) {
	
		//Current Cage
		int cc = pos2Cage(r,c);
		//Operator
		char a = myCages[cc].getOp();
		//Target
		int t = myCages[cc].getValue();
		//# of #'s in Cage
		int n = myCages[cc].numbers.size();
		//Cage Full
		boolean f = myCages[cc].isFull();
		if(a == ' ') {
			if(myCages[cc].numbers.get(0) != t) return false;
		}
		
		if(a == '+') {
		
			int tempAdd = 0;
			for(int i = 0; i < n; i++) {
			
				tempAdd = tempAdd + myCages[cc].numbers.get(i);
			}
			if(f) {
			
				if(tempAdd != t) return false;
			}
			//else if(tempAdd >= t) return false;
		}
		
		if(a == '*') {
		
			int tempMult = 1;
			for(int i = 0; i < n; i++) {
			
				tempMult = tempMult * myCages[cc].numbers.get(i);
			}
			if(f) if(tempMult != t) return false;
		}
		
		if(a == '-') {
			
			int oneSub = 0;
			int twoSub = 0;
			if(f) {
			
				oneSub = myCages[cc].numbers.get(0);
				twoSub = myCages[cc].numbers.get(1);
				if(Math.abs(oneSub - twoSub) != t) return false;
			}
			/*else {
			
				oneSub = myCages[cc].numbers.get(0);
				if(oneSub == t) return false;
			}*/
		}
		
		if(a == '/') {
		
			int oneDiv = 0;
			int twoDiv = 0;
			if(f) {
			
				oneDiv = myCages[cc].numbers.get(0);
				twoDiv = myCages[cc].numbers.get(1);
				if((Math.max(oneDiv,twoDiv)%Math.min(oneDiv,twoDiv)) != 0) return false;
				if(Math.max(oneDiv,twoDiv)/Math.min(oneDiv,twoDiv) != t) return false;
			}
			/*else {
				return true;
				//oneDiv = myCages[cc].numbers.get(0);
				//if((t % oneDiv) != 0) return false;
			}*/
		}
		return true;
	}
}

class cage {

	//Cage Info
	ArrayList<String> myCage = new ArrayList<String>();
	int currentValue = 0;
	//Box Positions
	ArrayList<String> boxSpots = new ArrayList<String>();
	//All the numbers within cage - from first number added to last
	ArrayList<Integer> numbers = new ArrayList<Integer>();
	//Operator - Target Value - # of Boxes
	int myValue, mySize;
	char myOp;
	
	// Everything we keep track of in a cage.
	public cage(ArrayList<String> cg) {
	
		myCage = cg;
		//Gets Operator - Target Value - # of Boxes
		getCage(myCage);
		getPositions(myCage);
	}
	
	public int getValue() {
	
		return myValue;
	}
	public char getOp() {
	
		return myOp;
	}
	public int getSize() {
	
		return mySize;
	}
	
	public void getCage(ArrayList<String> c) {
	
		String s = c.get(0);
		String t = "";
		for(int i = 0; i < s.length(); i++) {
		
			if(s.charAt(i) == ' ' || s.charAt(i) == '+' || s.charAt(i) == '-' || s.charAt(i) == '*' || s.charAt(i) == '/') {
			
				myOp = s.charAt(i);
				myValue = Integer.parseInt(t);
				t = "";
			}
			else t = t + s.charAt(i);
		}
		mySize = Integer.parseInt(t);
	}
	
	public void getPositions(ArrayList<String> pos) {
	
		//Creates arraylist of positions
		pos.remove(0);
		boxSpots = pos;
	}
	
	public void addNum(int n) {
	
		numbers.add(n);
	}
	
	public void removeNum() {
	
		numbers.remove(numbers.size() - 1);
	}
	
	public boolean isFull() {
	
		if(numbers.size() == this.getSize()) return true;
		else return false;
	}
	
}