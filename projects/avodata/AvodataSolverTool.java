import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.commons.math3.analysis.function.Abs;


/* This class reads matrix from csv file, ignores the parmeters and calculate
 * using least squares
 * To build:
 * bazel build //projects/avodata:ls-alg_tool
 * To run:
 * bazel-bin/projects/avodata/ls-alg_tool
 */

public class AvodataSolverTool {
  // Path to csv file
  static final String CSV_PATH = "/projects/avodata/check.csv";

  public static double factorial(int n) {
    return CombinatoricsUtils.factorialDouble(n);
  }

  public static double nChooseK(int n, int k) {
    return CombinatoricsUtils.binomialCoefficientDouble(n, k);
  }

  public static RealMatrix generateSyntheticA(int numOfProfessions) {
    int numOfraws = (int) nChooseK(numOfProfessions, 2);
    RealMatrix matrix = new Array2DRowRealMatrix(numOfraws, numOfProfessions);

    int row = 0;
    for (int i = 0; i < numOfProfessions; i++)
      for (int j = i + 1; j < numOfProfessions; j++) {
        matrix.addToEntry(row, i, 1);
        matrix.addToEntry(row, j, -1);
        row++;
      }
    return matrix;
  }

  public static RealMatrix generateSyntethicC(RealMatrix m, int i, RealMatrix a) {

    return a.multiply(m.getRowMatrix(i).transpose());
  }

  public static RealVector solve(RealMatrix a, RealVector c) {
    SingularValueDecomposition s = new SingularValueDecomposition(a);
    DecompositionSolver d = s.getSolver();
    return d.solve(c);
  }

  public static RealMatrix buildCheck(
      int numOfProfessions, RealMatrix cMatrix, int numOfCharacteristics) {
    RealMatrix a = MatrixUtils.createRealMatrix(generateSyntheticA(numOfProfessions).getData());
    RealMatrix m = new Array2DRowRealMatrix(numOfCharacteristics, numOfProfessions);

    for (int i = 0; i < numOfCharacteristics; i++) {

      RealVector c = cMatrix.getRowVector(i);
      RealVector rowInM = solve(a, c);
      Abs absoluteValue = new Abs();
      double min = absoluteValue.value(rowInM.getMinValue());
      rowInM.mapAddToSelf(min);
      double max = absoluteValue.value(rowInM.getMaxValue());
      if (max > 0) rowInM.mapDivideToSelf(max);

      m.setRowVector(i, rowInM);
    }

    return m;
  }

  // We read the matrix from csv file.
  // We ignore the alphabet chars and build the matrix without them.
  public static RealMatrix readMatrixAndCreate() {
    Scanner scanner = null;
    StringBuilder b = new StringBuilder();
    RealMatrix matrix = new Array2DRowRealMatrix(3, 3);
    // Path currentRelativePath = Paths.get("");
    // String myPath = currentRelativePath.toAbsolutePath().toString();

    try {
      scanner = new Scanner(new File(Paths.get("").toAbsolutePath() + CSV_PATH));
      ;
      scanner.useDelimiter(",");
      // Build one string of all the matrix
      while (scanner.hasNext()) {
        b.append(scanner.next() + " ");
      }
      // Build string array,every string is a vector of the matrix
      String[] s = b.toString().split("\r\n");
      int row = 0;
      for (String string : s) {

        String[] vectorToSet = string.split(" ");
        // Ignorance of alphabet chars vector
        if (vectorToSet[1].charAt(0) >= 'a' && vectorToSet[1].charAt(0) <= 'z') {
          continue;
        } else {

          double[] data = new double[vectorToSet.length];
          int i = 0;
          for (String val : vectorToSet) {
            data[i] = (Double.parseDouble(val));
            i++;
          }
          RealVector v = MatrixUtils.createRealVector(data);
          matrix.setRowVector(row, v);
        }
        row++;
      }

    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // The final matrix after build
    return matrix;
  }

  public static void main(String[] args) {
    // Read matrix from file and build
    RealMatrix r = readMatrixAndCreate();

    double[][] check = buildCheck(r.getColumnDimension(), r, r.getRowDimension()).getData();

    System.out.println("Matrix M:");
    System.out.println("---------");
    for (int i = 0; i < check.length; i++) {
      System.out.println(Arrays.toString(check[i]));
    }
  }
}

