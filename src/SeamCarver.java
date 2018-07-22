import java.awt.Color;

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

	private Picture picture;

	public SeamCarver(Picture picture) {
		this.picture = new Picture(picture);
		// create a seam carver object based on the given picture
	}

	public Picture picture() {
		return new Picture(picture); // current picture
	}

	public int width() {
		return picture.width();
		// width of current picture
	}

	public int height() {
		return picture.height();
		// height of current picture
	}

	public double energy(int x, int y) {
		if (x < 0 || x > width() - 1 || y < 0 || y > height() - 1) {
			throw new IllegalArgumentException();
		}
		if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1) {
			return 1000;
		}
		Color adjColorPrevX = picture.get(x - 1, y);
		Color adjColorNextX = picture.get(x + 1, y);

		int redDeltaX = (adjColorNextX.getRed() - adjColorPrevX.getRed());
		int greenDeltaX = (adjColorNextX.getGreen() - adjColorPrevX.getGreen());
		int blueDeltaX = (adjColorNextX.getBlue() - adjColorPrevX.getBlue());

		int resultX = redDeltaX * redDeltaX + greenDeltaX * greenDeltaX + blueDeltaX * blueDeltaX;

		Color adjColorPrevY = picture.get(x, y - 1);
		Color adjColorNextY = picture.get(x, y + 1);

		int redDeltaY = (adjColorNextY.getRed() - adjColorPrevY.getRed());
		int greenDeltaY = (adjColorNextY.getGreen() - adjColorPrevY.getGreen());
		int blueDeltaY = (adjColorNextY.getBlue() - adjColorPrevY.getBlue());

		int resultY = redDeltaY * redDeltaY + greenDeltaY * greenDeltaY + blueDeltaY * blueDeltaY;

		return Math.sqrt(resultX + resultY);
		// energy of pixel at column x and row y
	}

	public int[] findHorizontalSeam() {
		double[][] energyMatrix = computeEnergyMatrix();
		return computeMatrixDP(energyMatrix);
		// sequence of indices for horizontal seam
	}

	private int[] computeMatrixDP(double[][] energyMatrix) {
		double[][] weightDP = new double[energyMatrix.length][energyMatrix[0].length];
		int[][] ans = new int[energyMatrix.length][energyMatrix[0].length];
		int rowCount = energyMatrix.length;
		int colCount = energyMatrix[0].length;
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			weightDP[rowIndex][0] = 1000;
		}
		for (int colIndex = 1; colIndex < colCount; colIndex++) {
			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				double sameCol = Double.POSITIVE_INFINITY;
				double prevCol = Double.POSITIVE_INFINITY;
				double nextCol = Double.POSITIVE_INFINITY;
				int prevColIndex = colIndex - 1;
				sameCol = weightDP[rowIndex][prevColIndex];
				if (rowIndex >= 1) {
					prevCol = weightDP[rowIndex - 1][prevColIndex];
				}
				if (rowIndex < rowCount - 1) {
					nextCol = weightDP[rowIndex + 1][prevColIndex];
				}
				double min = Math.min(Math.min(sameCol, prevCol), nextCol);
				weightDP[rowIndex][colIndex] = min + energyMatrix[rowIndex][colIndex];
				if (min == sameCol) {
					ans[rowIndex][colIndex] = rowIndex;
				} else if (min == prevCol) {
					ans[rowIndex][colIndex] = rowIndex - 1;
				} else if (min == nextCol) {
					ans[rowIndex][colIndex] = rowIndex + 1;
				}
			}
		}
		double weight = Double.POSITIVE_INFINITY;
		int targetRowIndex = -1;
		for (int rowIndex = 0; rowIndex < weightDP.length; rowIndex++) {
			if (weightDP[rowIndex][colCount - 1] < weight) {
				weight = weightDP[rowIndex][colCount - 1];
				targetRowIndex = rowIndex;
			}
		}
		return computePath(ans, targetRowIndex);
	}

	private int[] computePath(int[][] ans, int targetRowIndex) {
		int[] sequence = new int[ans[0].length];
		for (int colIndex = ans[0].length - 1; colIndex >= 1; colIndex--) {
			sequence[colIndex] = targetRowIndex;
			targetRowIndex = ans[targetRowIndex][colIndex];
		}
		sequence[0] = targetRowIndex;
		return sequence;
	}

	private double[][] computeEnergyMatrix() {
		double[][] energyMatrix = new double[height()][width()];

		for (int rowIndex = 0; rowIndex < energyMatrix.length; rowIndex++) {
			for (int colIndex = 0; colIndex < energyMatrix[0].length; colIndex++) {
				energyMatrix[rowIndex][colIndex] = energy(colIndex, rowIndex);
			}
		}
		return energyMatrix;
	}

	public int[] findVerticalSeam() {
		double[][] energyMatrix = computeEnergyMatrix();
		double[][] transposeMatrix = transposeMatrix(energyMatrix);
		return computeMatrixDP(transposeMatrix);
	}

	private double[][] transposeMatrix(double[][] matrix) {
		double[][] transposeMatrix = new double[matrix[0].length][matrix.length];
		for (int rowIndex = 0; rowIndex < matrix.length; rowIndex++) {
			for (int colIndex = 0; colIndex < matrix[0].length; colIndex++) {
				transposeMatrix[colIndex][rowIndex] = matrix[rowIndex][colIndex];
			}
		}
		return transposeMatrix;
	}

	public void removeHorizontalSeam(int[] seam) {
		validateHorizontalSeam(seam);
		int index = 0;
		Picture pictureTemp = new Picture(picture.width(), picture.height() - 1);
		int newHeight = pictureTemp.height();
		int width = picture.width();
		int height = picture.height();
		for (int colIndex = 0; colIndex < width; colIndex++) {
			for (int rowIndex = 0; rowIndex < height; rowIndex++) {
				if (seam[colIndex] != rowIndex) {
					int tempRowIndex = index % newHeight;
					int tempColIndex = index / newHeight;
					pictureTemp.set(tempColIndex, tempRowIndex, picture.get(colIndex, rowIndex));
					index++;
				}
			}
		}
		this.picture = pictureTemp;
	}

	public void removeVerticalSeam(int[] seam) {
		validateVerticalSeam(seam);
		int index = 0;
		Picture pictureTemp = new Picture(picture.width() - 1, picture.height());
		int width = picture.width();
		int height = picture.height();
		int newWidth = pictureTemp.width();
		for (int rowIndex = 0; rowIndex < height; rowIndex++) {
			for (int colIndex = 0; colIndex < width; colIndex++) {
				if (seam[rowIndex] != colIndex) {
					int tempRowIndex = index / newWidth;
					int tempColIndex = index % newWidth;
					pictureTemp.set(tempColIndex, tempRowIndex, picture.get(colIndex, rowIndex));
					index++;
				}
			}
		}
		this.picture = pictureTemp;
	}

	private void validateVerticalSeam(int[] seam) {
		if (seam == null || seam.length != picture.height() || picture.width() <= 1) {
			throw new IllegalArgumentException();
		}
		int prev = seam[0];
		for (int index = 1; index < seam.length; index++) {
			int current = seam[index];
			if (current < 0 || current >= picture.width()) {
				throw new IllegalArgumentException();
			}
			if (Math.abs(current - prev) > 1) {
				throw new IllegalArgumentException();
			}
			prev = current;
		}
	}

	private void validateHorizontalSeam(int[] seam) {
		if (seam == null || seam.length != picture.width() || picture.height() <= 1) {
			throw new IllegalArgumentException();
		}
		int prev = seam[0];
		for (int index = 1; index < seam.length; index++) {
			int current = seam[index];
			if (current < 0 || current >= picture.height()) {
				throw new IllegalArgumentException();
			}
			if (Math.abs(current - prev) > 1) {
				throw new IllegalArgumentException();
			}
			prev = current;
		}
	}

}