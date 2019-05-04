package main;


import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.math3.analysis.function.Abs;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.commons.math3.util.CombinatoricsUtils;





/**
 * To build:
 * bazel build //ls-alg/main:ls-alg_tool
 * To run:
 * bazel-bin/ls-alg/main/ls-alg_tool
*/




public class Main {

	
	public static double factorial(int n) {
		return CombinatoricsUtils.factorialDouble(n);
	}
	
	public static double nChooseK(int n,int k) {
		return CombinatoricsUtils.binomialCoefficientDouble(n, k);
	}
	

	public static double[][] generateSyntheticA(int numOfProfessions){
		
		int numOfraws = (int) nChooseK(numOfProfessions, 2);
		RealMatrix matrix = new Array2DRowRealMatrix(numOfraws,numOfProfessions);
		
		int row = 0;
		for(int i =0;i<numOfProfessions;i++)
			for(int j =i+1;j<numOfProfessions;j++) {
				matrix.addToEntry(row, i, 1);
				matrix.addToEntry(row, j, -1);
				row++;
			}
		return matrix.getData();
		
		
	}
	
	public static double[][] generateSyntethicC(RealMatrix m ,int i, RealMatrix a){
		RealMatrix c = a.multiply(m.getRowMatrix(i).transpose());
		return c.getData();
	}
	
	public static RealVector solve(RealMatrix a , RealVector c) {
		
		SingularValueDecomposition s = new SingularValueDecomposition(a);
		DecompositionSolver d = s.getSolver();
		return d.solve(c);
		}
	
	public static double[][] buildCheck(int numOfProfessions,RealMatrix cMatrix,int numOfCharacteristics) {
		RealMatrix a = new Array2DRowRealMatrix();
		a = MatrixUtils.createRealMatrix(generateSyntheticA(numOfProfessions));
		RealMatrix m = new Array2DRowRealMatrix(numOfCharacteristics,numOfProfessions);
		
		for(int i =0;i<numOfCharacteristics;i++) {
			
			RealVector c = cMatrix.getRowVector(i);
			RealVector rowInM = solve(a,c);
			Abs av = new Abs();
			double min = av.value(rowInM.getMinValue());
			rowInM.mapAddToSelf(min);
			double max = av.value(rowInM.getMaxValue());
			rowInM.mapDivideToSelf(max);
			m.setRowVector(i, rowInM);
		}
		
		return m.getData();
	}
	
	//we read the matrix from csv file.
	//we ignore the alphabet chars and build the matrix without them.
	public static RealMatrix readMatrixAndCreate() {
		
		 Scanner scanner;
		 StringBuilder b = new StringBuilder();
		 RealMatrix rMatrix = new Array2DRowRealMatrix(3,3);
		 Path currentRelativePath = Paths.get("");
		 String myPath = currentRelativePath.toAbsolutePath().toString();
		 
		try {
			scanner = new Scanner(new File(myPath +"/ls-alg/check.csv"));
			scanner.useDelimiter(",");
			//build one string of all the matrix
			while(scanner.hasNext()) {
				b.append(scanner.next() +" ");
			}
			//build string array,every string is a vector of the matrix
			String[] s = b.toString().split("\r\n");
	        int row = 0;
	        for (String string : s) {
	        	
				String[] vectorToSet = string.split(" ");
				//ignorance of alphabet chars vector
				if(vectorToSet[1].charAt(0) >= 'a'
						&& vectorToSet[1].charAt(0) <= 'z' ) {
					continue;
				}else {
					
					double[] data = new double[vectorToSet.length];
					int i = 0;
					for (String val : vectorToSet) {	
						data[i]=(Double.parseDouble(val));
						i++;
					}
					RealVector v = MatrixUtils.createRealVector(data);
					rMatrix.setRowVector(row, v);	
					
				}
				row++;	
			}
	        
	 	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//the final matrix after build
		return rMatrix;
	}
	
	
	
	public static void main(String[] args) {
		
		
		//read matrix from file and build
		RealMatrix r = readMatrixAndCreate();

		double[][] check = buildCheck(r.getColumnDimension(), r, r.getRowDimension());
		
		System.out.println("Matrix M:");
		System.out.println("---------");
		for(int i=0;i<check.length;i++) {
			System.out.println(Arrays.toString(check[i]));
		}
	}

}
