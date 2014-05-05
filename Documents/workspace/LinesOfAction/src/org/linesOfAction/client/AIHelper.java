package org.linesOfAction.client;

import java.util.Map;

public class AIHelper {
	public static boolean checkOver(Map<String,Object> state){
		if (checkOverPlayer(state,1) || checkOverPlayer(state,2))
			return true;
		else 
			return false;
	}
	
	private static boolean checkOverPlayer(Map<String,Object> state, int winner_int){
		int board[][] = new int[8][8];
		for (int i=0;i<8;i++)
			for (int j=0;j<8;j++){
				switch ((String)state.get(Character.toString((char)('1'+i)) + Character.toString((char)('A'+j)))) {
				case "O":	board[i][j] = 0;
							break;
				case "B":	board[i][j] = 1;
							break;
				case "W":	board[i][j] = 2;
							break;
				default:	board[i][j] = -1;
							break;
				}
			}
		int row=-1;
		int col=-1;
		for (int i=0;i<8;i++)
			for (int j=0;j<8;j++)
				if (winner_int == board[i][j]) {row=i;col=j;break;}
		if (col == -1) return false;
		else traverse(board,winner_int,row,col);
		col = -1; 
		row = -1;
		for (int i=0;i<8;i++)
			for (int j=0;j<8;j++)
				if (winner_int == board[i][j]) {row=i;col=j;break;}
		if (col == -1) return true;
		else return false;
	}
	
	private static void traverse(int[][] a,int side,int row,int col){
		if (row<0 || row>7 || col<0 || col>7) return;
		else if (a[row][col] != side) return;
		else {
			a[row][col] = -1;
			traverse(a,side,row+1,col-1);
			traverse(a,side,row+1,col);
			traverse(a,side,row+1,col+1);
			traverse(a,side,row,col-1);
			traverse(a,side,row,col+1);
			traverse(a,side,row-1,col-1);
			traverse(a,side,row-1,col);
			traverse(a,side,row-1,col+1);
		}
	}
}
