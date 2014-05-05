package org.linesOfAction.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.game_api.GameApi.Operation;
import org.game_api.GameApi.Set;

public class Heuristic {
	public Iterable<List<Operation>> getOrderedMoves(Map<String,Object> state){
		ArrayList<List<Operation>> ret = new ArrayList<List<Operation>>();
		int board[][] = new int[8][8];
		for (int i=0;i<8;i++)
			for (int j=0;j<8;j++){
				switch ((String)state.get(Character.toString((char)('1'+i)) + Character.toString((char)('A'+j)))) {
				case "0":	board[i][j] = 0;
							break;
				case "B":	board[i][j] = 1;
							break;
				case "W":	board[i][j] = 2;
							break;
				default:	board[i][j] = -1;
							break;
				}
			}
		
		int color = state.get("turn").equals("W") ? 2 : 1;
		

		for (int row=0;row<8;row++)
			for (int col=0;col<8;col++){
				StringBuilder ori = new StringBuilder(Character.toString((char)('1'+row)));
				ori.append((char)('A'+col));
				String origin = ori.toString();
				if (board[row][col] == color) {
					// horizental
					int hori_count=0;
					for (int i=0;i<8;i++){
						if (board[row][i] == 1 || board[row][i] == 2) hori_count++;
					}
					for (int i=col-1;i>=0;i--){
						if (col - i <= hori_count && board[row][i] != color){
							StringBuilder str = new StringBuilder(Character.toString((char)('1'+row)));
							str.append((char)('A'+i));
							String destination = str.toString();
							ret.add(getMove(origin,destination,color));
						}
						if (board[row][i] == 3-color) break;
					}
					for (int i=col+1;i<8;i++){
						if (i - col <= hori_count && board[row][i] != color){
							StringBuilder str = new StringBuilder(Character.toString((char)('1'+row)));
							str.append((char)('A'+i));
							String destination = str.toString();
							ret.add(getMove(origin,destination,color));
						}
						if (board[row][i] == 3-color) break;
					}
					
					// vertical
					int verti_count=0;
					for (int i=0;i<8;i++){
						if (board[i][col] == 1 || board[i][col] == 2) verti_count++;
					}
					for (int i=row-1;i>=0;i--){
						if (row - i <= verti_count && board[i][col] != color){
							StringBuilder str = new StringBuilder(Character.toString((char)('1'+i)));
							str.append((char)('A'+col));
							String destination = str.toString();
							ret.add(getMove(origin,destination,color));
						}
						if (board[i][col] == 3-color) break;
					}
					for (int i=row+1;i<8;i++){
						if (i - row <= verti_count && board[i][col] != color){
							StringBuilder str = new StringBuilder(Character.toString((char)('1'+i)));
							str.append((char)('A'+col));
							String destination = str.toString();
							ret.add(getMove(origin,destination,color));
						}
						if (board[i][col] == 3-color) break;
					}
					
					// diagonal /
					int diag_count=0;
					for (int i=0;col+i<8 && row+i<8;i++){
						if (board[row+i][col+i] == 1 || board[row+i][col+i] == 2) diag_count++;
					}
					for (int i=1;col-i>=0 && row-i>=0;i++){
						if (board[row-i][col-i] == 1 || board[row-i][col-i] == 2) diag_count++;
					}
					for (int i=1;col+i<8 && row+i<8;i++){
						if (i<=diag_count && board[row+i][col+i] != color){
							StringBuilder str = new StringBuilder(Character.toString((char)('1'+row+i)));
							str.append((char)('A'+col+i));
							String destination = str.toString();
							ret.add(getMove(origin,destination,color));
						}
						if (board[row+i][col+i] == 3 - color) break;
					}
					for (int i=1;col-i>=0 && row-i>=0;i++){
						if (i<=diag_count && board[row-i][col-i] != color){
							StringBuilder str = new StringBuilder(Character.toString((char)('1'+row-i)));
							str.append((char)('A'+col-i));
							String destination = str.toString();
							ret.add(getMove(origin,destination,color));
						}
						if (board[row-i][col-i] == 3 - color) break;
					}
					
					// diagonal \
					diag_count=0;
					for (int i=0;col-i>=0 && row+i<8;i++){
						if (board[row+i][col-i] == 1 || board[row+i][col-i] == 2) diag_count++;
					}
					for (int i=1;col+i<8 && row-i>=0;i++){
						if (board[row-i][col+i] == 1 || board[row-i][col+i] == 2) diag_count++;
					}
					for (int i=1;col-i>=0 && row+i<8;i++){
						if (i<=diag_count && board[row+i][col-i] != color){
							StringBuilder str = new StringBuilder(Character.toString((char)('1'+row+i)));
							str.append((char)('A'+col-i));
							String destination = str.toString();
							ret.add(getMove(origin,destination,color));
						}
						if (board[row+i][col-i] == 3 - color) break;
					}
					for (int i=1;col+i<8 && row-i>=0;i++){
						if (i<=diag_count && board[row-i][col+i] != color){
							StringBuilder str = new StringBuilder(Character.toString((char)('1'+row-i)));
							str.append((char)('A'+col+i));
							String destination = str.toString();
							ret.add(getMove(origin,destination,color));
						}
						if (board[row-i][col+i] == 3 - color) break;
					}
				}
			}
		return ret;
	}
	
	private List<Operation> getMove(String origin, String destination, int color){
		ArrayList<Operation> ret = new ArrayList<Operation>();
		String side = color == 1 ? "B" : "W";
		ret.add(new Set(origin,"0"));
		ret.add(new Set(destination,side));
		return ret;
	}
	
	public int getStateValue(Map<String,Object> state){
		int board[][] = new int[8][8];
		for (int i=0;i<8;i++)
			for (int j=0;j<8;j++){
				switch ((String)state.get(Character.toString((char)('1'+i)) + Character.toString((char)('A'+j)))) {
				case "0":	board[i][j] = 0;
							break;
				case "B":	board[i][j] = 1;
							break;
				case "W":	board[i][j] = 2;
							break;
				default:	board[i][j] = -1;
							break;
				}
			}
		
		// the less pieces one has, the more chance he will win 
		int num_diff = 0; //(-12 ~ 12)
		for (int i=0;i<8;i++)
			for (int j=0;j<8;j++){
				num_diff += (board[i][j] == 0 ? 0 : (board[i][j] == 2 ? -1 : 1));
			}
		
		int w_connect = connect_count(board,2);
		int b_connect = connect_count(board,1);
		
		// the less connective components one has, the more chance he will win
		int connect_diff = b_connect - w_connect; //(-12 ~ 12)
		
		if (w_connect == 1 && b_connect == 1) return 0;
		if (w_connect == 1) return 24;
		if (b_connect == 1) return -24;
		return num_diff + connect_diff;
			
	}
	
	private int connect_count(int[][] board, int color){
		int count = 0;
		int[][] temp_board = new int[8][8];
		for (int i=0;i<8;i++)
			for (int j=0;j<8;j++){
				temp_board[i][j] = board[i][j];
			}
		while (true){
			int row=-1;
			int col=-1;
			for (int i=0;i<8;i++)
				for (int j=0;j<8;j++)
					if (color == board[i][j]) {row=i;col=j;break;}
			if (col == -1) break;
			else traverse(board,color,row,col);
			count++;
		}
		return count;
	}
	
	private void traverse(int[][] a,int side,int row,int col){
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
