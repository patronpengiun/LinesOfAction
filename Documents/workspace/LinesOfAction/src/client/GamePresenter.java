package client;

import client.GameApi.UpdateUI;
import client.GameApi.Container;
import client.GameApi.Operation;
import client.GameApi.EndGame;
import client.GameApi.Set;
import client.GameApi.SetTurn;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class GamePresenter {

	interface View {
		void setPresenter(GamePresenter gamepresenter);
		
		// present the animation of last move to the player 
		void setState(String moveFrom, String moveTo);
		
		// present the initial state of the board to the player, and set the player's color
		void setInitialState(String color);
		
		/** ask the player to select a position on the board
		 *  if origin and destination are both empty, the position player selects will be an origin
		 *  if origin is not empty, player can either cancel the origin(by selecting the same position) or
		 *  select a destination(by selecting a different position)
		 */
		void choosePosition(String origin, String destination);
	}
	
	private final View view;
	private final Container container;
	
	private int[][] board = new int[8][8];
	private int yourPlayerId;
	private int opponentPlayerId;
	private int turnId;
	private String yourColor;
	
	
	private String origin;
	private String destination;
	
	public GamePresenter(View view, Container container) {
		this.view = view;
		this.container = container;
		view.setPresenter(this);
	}

	
	public void updateUI(UpdateUI updateUI) {
		origin = "";
		destination = "";
		
		Map<String,Object> newState = updateUI.getState();
		yourPlayerId = updateUI.getYourPlayerId();
		
		int yourPlayerIndex = updateUI.getPlayerIndex(yourPlayerId);
		int opponentPlayerIndex = 1 - yourPlayerIndex;
		yourColor = yourPlayerIndex == 0 ? "W"
		        : yourPlayerIndex == 1 ? "B" : "";
		
		if (-1 != yourPlayerIndex){
			opponentPlayerId = updateUI.getPlayerIds().get(opponentPlayerIndex);
			turnId = newState.get("turn") == yourColor ? yourPlayerId : opponentPlayerId;
		}
		
		if (1 == newState.size()){ //initial state, contains only one record("turn": "W/B")
			view.setInitialState(yourColor);
		}
		else{
			for (int i=0;i<8;i++)
				for (int j=0;j<8;j++){
					switch ((String)newState.get(Character.toString((char)('1'+i)) + Character.toString((char)('A'+j)))) {
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
			
			String moveFrom = ((Set)updateUI.getLastMove().get(0)).getKey();
			String moveTo = ((Set)updateUI.getLastMove().get(1)).getKey();
	
			if (updateUI.isViewer()) {
			      view.setState(moveFrom,moveTo);
			      return;
			    }
			if (updateUI.isAiPlayer()) {
				// TODO: implement AI in a later HW!
				//container.sendMakeMove(..);
				return;
			}
			
			view.setState(moveFrom,moveTo);
		}
		
		if (isMyTurn()) 
			choosePosition();
	}
	 
	private void choosePosition(){
		view.choosePosition(origin,destination);
	}
	
	// The view can only call this method if the presenter called {@link View#choosePosition}
	void positionSelected(String position){
		check(isMyTurn());
		if ("" == origin) {origin = position;choosePosition();} // origin selected, continue;
		else{
			if (position == origin) origin=""; // origin canceled, continue;
			else {
				destination = position;
				int winnerId = checkWin(board,origin,destination);
				if (winnerId>=0) makeMoveWin(winnerId); // move made and someone wins the game
				else makeMoveContinue(); // move made and the game continue
			}
		}
	}
	
	private void makeMoveWin(int winnerId){
		List<Operation> theMove = new ArrayList<Operation>();
		theMove.add(new Set(origin,"0"));
		theMove.add(new Set(destination,yourColor));
		theMove.add(new SetTurn(opponentPlayerId));
		theMove.add(new EndGame(winnerId));
		container.sendMakeMove(theMove);
	}
	
	private void makeMoveContinue(){
		List<Operation> theMove = new ArrayList<Operation>();
		theMove.add(new Set(origin,"0"));
		theMove.add(new Set(destination,yourColor));
		theMove.add(new SetTurn(opponentPlayerId));
		container.sendMakeMove(theMove);
	}
	
	// helper function, return the ID of the winner, -100 if no winner
	int checkWin(int[][] board,String origin, String destination){
		boolean youWin = connect(board,yourColor=="W"?2:1);
		boolean otherWin = connect(board,yourColor=="W"?1:2);
		if (!(youWin || otherWin)) return -100;
		else{
			if (youWin) return yourPlayerId;
			else return opponentPlayerId;
		}
	}
	
	// helper function, check whether one's pieces are all connected
	boolean connect(int[][] board, int side){
		int[][] temp_board = new int[8][8];
		for (int i=0;i<8;i++)
			for (int j=0;j<8;j++)
				temp_board[i][j] = board[i][j];
		temp_board[origin.charAt(0)-'1'][origin.charAt(1)-'A'] = 0;
		temp_board[destination.charAt(0)-'1'][destination.charAt(1)-'A'] = "B" == yourColor ? 1 : 2;
		int row=-1;
		int col=-1;
		for (int i=0;i<8;i++)
			for (int j=0;j<8;j++)
				if (side == temp_board[i][j]) {row=i;col=j;break;}
		if (col == -1) return false;
		else traverse(temp_board,side,row,col);
		col = -1; 
		row = -1;
		for (int i=0;i<8;i++)
			for (int j=0;j<8;j++)
				if (side == temp_board[i][j]) {row=i;col=j;break;}
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
	
	boolean isMyTurn(){
		if (yourPlayerId == GameApi.VIEWER_ID || yourPlayerId != turnId) return false;
		else return true;
	}
	
	private void check(boolean val) {
		if (!val) {
	    	throw new IllegalArgumentException();
	    }
	}

}
