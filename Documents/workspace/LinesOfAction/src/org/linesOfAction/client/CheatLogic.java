package org.linesOfAction.client;

import org.game_api.GameApi.VerifyMove;
import org.game_api.GameApi.VerifyMoveDone;
import org.game_api.GameApi.EndGame;
import org.game_api.GameApi.Operation;
import org.game_api.GameApi.Set;
import org.game_api.GameApi.SetTurn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheatLogic {
	public VerifyMoveDone verify(VerifyMove verifyMove) {
	    try{
	    	checkMoveIsLegal(verifyMove);
	      	return new VerifyMoveDone();
	    } 
	    catch (Exception e) {
	    	return new VerifyMoveDone(verifyMove.getLastMovePlayerId(), e.getMessage());
	    }
	}
	
	void checkMoveIsLegal(VerifyMove verifyMove) {
	    List<Operation> lastMove = verifyMove.getLastMove();
	    Map<String, Object> lastState = verifyMove.getLastState();
	    String lastMovePlayerId = verifyMove.getLastMovePlayerId();
	    List<Map<String, Object>> playersInfo = verifyMove.getPlayersInfo();
	    check(lastState, lastMove, lastMovePlayerId, playersInfo);
	  }
	
	void check(Map<String, Object> lastState, List<Operation> lastMove, String lastMovePlayerId, List<Map<String, Object>> playersInfo){
		if (lastState.size() == 0){
			String[][] initialBoard = new String[][]{
					  { "0", "B", "B", "B", "B", "B", "B", "0" },
		    		  { "W", "0", "0", "0", "0", "0", "0", "W" },
		    		  { "W", "0", "0", "0", "0", "0", "0", "W" },
		    		  { "W", "0", "0", "0", "0", "0", "0", "W" },
		    		  { "W", "0", "0", "0", "0", "0", "0", "W" },
		    		  { "W", "0", "0", "0", "0", "0", "0", "W" },
		    		  { "W", "0", "0", "0", "0", "0", "0", "W" },
		    		  { "0", "B", "B", "B", "B", "B", "B", "0" },
		    		};
			lastState = new HashMap<String,Object>();
			for (int i=0;i<8;i++)
				for (int j=0;j<8;j++)
					lastState.put(String.valueOf(i+1)+Character.toString((char)('A'+j)), initialBoard[i][j]);
			lastState.put("turn","W");
		}
		for (;;){
			if (!checkOriginLegal(lastState,lastMove)) {System.out.println(1);break;}	// check whether origin is legal
			if (!checkDifference(lastMove)) {System.out.println(2);break;} 				// check whether there is a move
			if (!checkNotCaptureSelf(lastState,lastMove)) {System.out.println(3);break;}// check whether capture friendly piece 
			if (!checkDirection(lastMove)) {System.out.println(4);break;}				// check whether horizontal,vertical or diagonal 
			if (!checkMoveNum(lastState,lastMove)) {System.out.println(5);break;}		// check the number limit
			if (!checkNotJumpOverEnemy(lastState,lastMove)) {System.out.println(6);break;}	// check whether jump over enemy piece
			if (!checkDestinationColor(lastState,lastMove)) {System.out.println(7);break;}  // check whether destination is set to the right color
			if (!checkOriginEmpty(lastMove)) {System.out.println(8);break;}					// check whether origin is empty after move
			if (!checkSetTurn(lastState,lastMove, playersInfo)) {System.out.println(9);break;}			// check whether turn is correctly set after move
			if ((lastMove.size()==6) && (!checkEndGame(lastState,lastMove,lastMovePlayerId,playersInfo))) {System.out.println(10);break;}
			//check end game scenario
			return;
		}
		throw new RuntimeException("We have a hacker!");
	}
	
	
	boolean checkDirection(List<Operation> lastMove){
		Set moveFrom = (Set)lastMove.get(0);
		Set moveTo = (Set)lastMove.get(1);
		if (moveFrom.getKey().charAt(0) == moveTo.getKey().charAt(0)) return true;
		if (moveFrom.getKey().charAt(1) == moveTo.getKey().charAt(1)) return true;
		if (Math.abs(moveFrom.getKey().charAt(0)-moveTo.getKey().charAt(0)) == Math.abs(moveFrom.getKey().charAt(1)-moveTo.getKey().charAt(1))) return true;
		return false;
	} 
	
	boolean checkDestinationColor(Map<String, Object> lastState, List<Operation> lastMove){
		Set moveTo = (Set)lastMove.get(1);
		if (moveTo.getValue().equals(lastState.get("turn"))) return true;
		else return false;
	}
	
	boolean checkSetTurn(Map<String, Object> lastState, List<Operation> lastMove, List<Map<String, Object>> playersInfo){
		SetTurn setTurn;
		if (lastMove.size()>10)
			setTurn = (SetTurn)lastMove.get(66);
		else
			setTurn = (SetTurn)lastMove.get(4); 
		Object wId = playersInfo.get(0).get("playerId");
		String w_id = (String)wId;
		String turn = setTurn.getPlayerId().equals(w_id) ? "W" : "B";
		if (!turn.equals(lastState.get("turn"))) return true;
		else return false;
	}
	
	boolean checkOriginEmpty(List<Operation> lastMove){
		Set moveFrom = (Set)lastMove.get(0);
		if (moveFrom.getValue() == "0") return true;
		else return false;
	}
	
	boolean checkOriginLegal(Map<String, Object> lastState, List<Operation> lastMove){
		Set moveFrom = (Set)lastMove.get(0);
		if (lastState.get(moveFrom.getKey()) == lastState.get("turn")) return true;
		else return false;
	}
	
	boolean checkDifference(List<Operation> lastMove){
		Set moveFrom = (Set)lastMove.get(0);
		Set moveTo = (Set)lastMove.get(1);
		if (moveFrom.getKey() != moveTo.getKey()) return true;
		else return false;
	}
	
	// helper function to get the number of pieces on a particular line
	int getNumberOnLine(Map<String, Object> lastState, List<Operation> lastMove){
		int count = 0;
		Set moveFrom = (Set)lastMove.get(0);
		Set moveTo = (Set)lastMove.get(1);
		String origin = moveFrom.getKey();
		String dest = moveTo.getKey();
		if (origin.charAt(0) == dest.charAt(0)){
			char row = origin.charAt(0);
			for (char col = 'A';col <= 'H';col++){
				String coord = Character.toString(row) + Character.toString(col);
				if (lastState.get(coord)!="0") count++;
			}
		}
		else if (origin.charAt(1) == dest.charAt(1)){
			char col = origin.charAt(1);
			for (char row = '1';row <= '8';row++){
				String coord = Character.toString(row) + Character.toString(col);
				if (lastState.get(coord)!="0") count++;
			}
		}
		else{
			if ((origin.charAt(1)-dest.charAt(1))/(origin.charAt(0)-dest.charAt(0)) == 1){
				char _row = origin.charAt(0);
				char _col = origin.charAt(1);
				for (int i=0;_row+i<='8';i++){
					String coord = Character.toString((char)(_row+i)) + Character.toString((char)(_col+i));
					if (lastState.get(coord)!="0") count++;
				}
				for (int i=1;_row-i>='0';i++){
					String coord = Character.toString((char)(_row-i)) + Character.toString((char)(_col-i));
					if (lastState.get(coord)!="0") count++;
				}
			}
			else{
				char _row = origin.charAt(0);
				char _col = origin.charAt(1);
				for (int i=0;_row+i<='8';i++){
					String coord = Character.toString((char)(_row+i)) + Character.toString((char)(_col-i));
					if (lastState.get(coord)!="0") count++;
				}
				for (int i=1;_row-i>='0';i++){
					String coord = Character.toString((char)(_row-i)) + Character.toString((char)(_col+i));
					if (lastState.get(coord)!="0") count++;
				}
			}
		}
		return count;
	}
	
	boolean checkMoveNum(Map<String, Object> lastState, List<Operation> lastMove){
		int limit = getNumberOnLine(lastState,lastMove);
		int num=0;
		Set moveFrom = (Set)lastMove.get(0);
		Set moveTo = (Set)lastMove.get(1);
		String origin = moveFrom.getKey();
		String dest = moveTo.getKey();
		if (origin.charAt(0) == dest.charAt(0)) num = Math.abs(origin.charAt(1) - dest.charAt(1));
		else num = Math.abs(origin.charAt(0) - dest.charAt(0));
		if (num <= limit) return true; else return false;
	}
	
	boolean checkNotCaptureSelf(Map<String, Object> lastState, List<Operation> lastMove){
		Set moveTo = (Set)lastMove.get(1);
		if (lastState.get(moveTo.getKey()) != lastState.get("turn")) return true;
		else return false;
	}
	
	boolean checkNotJumpOverEnemy(Map<String, Object> lastState, List<Operation> lastMove){
		String enemy;
		if (lastState.get("turn") == "W") enemy="B"; else enemy="W";
		Set moveFrom = (Set)lastMove.get(0);
		Set moveTo = (Set)lastMove.get(1);
		String origin = moveFrom.getKey();
		String dest = moveTo.getKey();
		if (origin.charAt(0) == dest.charAt(0)){
			if (origin.charAt(1) < dest.charAt(1)){
				for (int i=1;origin.charAt(1)+i<dest.charAt(1);i++){
					String coord = Character.toString(origin.charAt(0)) + Character.toString((char)(origin.charAt(1)+i));
					if (lastState.get(coord) == enemy) return false;
				}
			}
			else{
				for (int i=1;origin.charAt(1)-i>dest.charAt(1);i++){
					String coord = Character.toString(origin.charAt(0)) + Character.toString((char)(origin.charAt(1)-i));
					if (lastState.get(coord) == enemy) return false;
				}
			}
		}
		else if (origin.charAt(1) == dest.charAt(1)){
			if (origin.charAt(0) < dest.charAt(0)){
				for (int i=1;origin.charAt(0)+i<dest.charAt(0);i++){
					String coord = Character.toString((char)(origin.charAt(0)+i)) + Character.toString(origin.charAt(1));
					if (lastState.get(coord) == enemy) return false;
				}
			}
			else{
				for (int i=1;origin.charAt(0)-i>dest.charAt(0);i++){
					String coord = Character.toString((char)(origin.charAt(0)-i)) + Character.toString(origin.charAt(1));
					if (lastState.get(coord) == enemy) return false;
				}
			}
		}
		else if ((origin.charAt(1)-dest.charAt(1))/(origin.charAt(0)-dest.charAt(0)) == 1){
			if (origin.charAt(0) < dest.charAt(0)){
				for (int i=1;origin.charAt(0)+i<dest.charAt(0);i++){
					String coord = Character.toString((char)(origin.charAt(0)+i)) + Character.toString((char)(origin.charAt(1)+i));
					if (lastState.get(coord) == enemy) return false;
				}
			}
			else{
				for (int i=1;origin.charAt(0)-i>dest.charAt(0);i++){
					String coord = Character.toString((char)(origin.charAt(0)-i)) + Character.toString((char)(origin.charAt(1)-i));
					if (lastState.get(coord) == enemy) return false;
				}
			}
		}
		else{
			if (origin.charAt(0) < dest.charAt(0)){
				for (int i=1;origin.charAt(0)+i<dest.charAt(0);i++){
					String coord = Character.toString((char)(origin.charAt(0)+i)) + Character.toString((char)(origin.charAt(1)-i));
					if (lastState.get(coord) == enemy) return false;
				}
			}
			else{
				for (int i=1;origin.charAt(0)-i>dest.charAt(0);i++){
					String coord = Character.toString((char)(origin.charAt(0)-i)) + Character.toString((char)(origin.charAt(1)+i));
					if (lastState.get(coord) == enemy) return false;
				}
			}
		}	
		return true;
	}

	
	boolean checkEndGame(Map<String, Object> lastState, List<Operation> lastMove,String lastMovePlayerId, List<Map<String, Object>> playersInfo){
		EndGame endGame;
		if (lastMove.size()>10)
			endGame= (EndGame)lastMove.get(67);
		else
			endGame= (EndGame)lastMove.get(5);
		Object WID = playersInfo.get(0).get("playerId");
		Object BID = playersInfo.get(1).get("playerId");
		String w_id = (String)WID;
		String b_id = (String)BID;
		String winner;
		if (endGame.getPlayerIdToScore().get(w_id) != null)  winner = "W";
		else winner = "B";
		boolean white_win = checkWin(lastState,lastMove,"W");
		boolean black_win = checkWin(lastState,lastMove,"B");
	
		if (!(white_win || black_win)) return false;
		else if (white_win && (!black_win) && winner=="W") return true;
		else if (black_win && (!white_win) && winner=="B") return true;
		else if (black_win && white_win){
			if (winner == "B" && lastMovePlayerId.equals(b_id)) return true;
			if (winner == "W" && lastMovePlayerId.equals(w_id)) return true;
			return false;
		}
		else return false;
	}
	
	// helper function to check whether a player has all his pieces connected
	boolean checkWin(Map<String, Object> lastState, List<Operation> lastMove, String winner){
		int winner_int = 0;
		if ("B" == winner) winner_int=1; else winner_int=2;
		int board[][] = new int[8][8];
		for (int i=0;i<8;i++)
			for (int j=0;j<8;j++){
				switch ((String)lastState.get(Character.toString((char)('1'+i)) + Character.toString((char)('A'+j)))) {
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
		Set moveFrom = (Set)lastMove.get(0);
		Set moveTo = (Set)lastMove.get(1);
		board[moveFrom.getKey().charAt(0) - '1'][moveFrom.getKey().charAt(1)-'A'] = 0;
		if (moveTo.getValue()=="B")
			board[moveTo.getKey().charAt(0) - '1'][moveTo.getKey().charAt(1)-'A'] = 1;
		else 
			board[moveTo.getKey().charAt(0) - '1'][moveTo.getKey().charAt(1)-'A'] = 2;
		
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
	
	// helper function, DFS
	void traverse(int[][] a,int side,int row,int col){
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























