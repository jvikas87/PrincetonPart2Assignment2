
public class Util {

	public static void extract(int[][] ans) {
		for (int rowIndex = 0; rowIndex < ans.length; rowIndex++) {
			System.out.println();
			for (int colIndex = 0; colIndex < ans[0].length; colIndex++) {
				System.out.print(ans[rowIndex][colIndex] + " ");
			}
		}
	}

	public static void extractDouble(double[][] ans) {
		for (int rowIndex = 0; rowIndex < ans.length; rowIndex++) {
			System.out.println();
			for (int colIndex = 0; colIndex < ans[0].length; colIndex++) {
				System.out.print(ans[rowIndex][colIndex] + " ");
			}
		}
	}

}
