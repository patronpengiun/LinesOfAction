package client;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import client.GameApi.EndGame;
import client.GameApi.Operation;
import client.GameApi.Set;
import client.GameApi.VerifyMove;
import client.GameApi.VerifyMoveDone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.ArrayList;


@RunWith(JUnit4.class)

public class CheatLogicTest {
	private static final String PLAYER_ID = "playerId";
	private final int wId = 98;
	private final int bId = 99;
	private final Map<String, Object> emptyState = new HashMap<String,Object>();


	private CheatLogic cheatLogic = new CheatLogic();
	
	// helper function to get an VerifyMove instance
	private VerifyMove move(int lastMovePlayerId, Map<String, Object> lastState, List<Operation> lastMove) {
		Map<String,Object> winfo = new HashMap<String,Object>(); winfo.put(PLAYER_ID,wId);
		Map<String,Object> binfo = new HashMap<String,Object>(); winfo.put(PLAYER_ID,bId);	
		ArrayList<Map<String,Object>> playersInfo = new ArrayList<Map<String,Object>>(); 
		playersInfo.add(winfo); playersInfo.add(binfo); 
		return new VerifyMove(wId, playersInfo,emptyState,lastState, lastMove, lastMovePlayerId);
	}
	
	// helper function to assert the move is legal
	private void assertLegalMove(VerifyMove verifyMove) {
		VerifyMoveDone verifyDone = cheatLogic.verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	// helper function to assert the move is illegal
	private void assertIllegalMove(VerifyMove verifyMove){
		VerifyMoveDone verifyDone = cheatLogic.verify(verifyMove);
	    assertEquals(verifyMove.getLastMovePlayerId(), verifyDone.getHackerPlayerId());
	}
	
	// helper function to provide preset state
	private Map<String, Object> verticalMoveState(){
		HashMap<String, Object> state = new HashMap<String, Object>();
		state.put("1A","W");state.put("1B","0");state.put("1C","0");state.put("1D","0");state.put("1E","0");state.put("1F","0");state.put("1G","0");state.put("1H","B");
		state.put("2A","0");state.put("2B","0");state.put("2C","0");state.put("2D","0");state.put("2E","0");state.put("2F","0");state.put("2G","0");state.put("2H","0");
		state.put("3A","0");state.put("3B","0");state.put("3C","0");state.put("3D","B");state.put("3E","0");state.put("3F","0");state.put("3G","0");state.put("3H","0");
		state.put("4A","0");state.put("4B","0");state.put("4C","0");state.put("4D","0");state.put("4E","0");state.put("4F","0");state.put("4G","0");state.put("4H","0");
		state.put("5A","0");state.put("5B","0");state.put("5C","0");state.put("5D","0");state.put("5E","0");state.put("5F","0");state.put("5G","0");state.put("5H","0");
		state.put("6A","0");state.put("6B","0");state.put("6C","0");state.put("6D","B");state.put("6E","0");state.put("6F","0");state.put("6G","0");state.put("6H","0");
		state.put("7A","0");state.put("7B","0");state.put("7C","0");state.put("7D","0");state.put("7E","0");state.put("7F","0");state.put("7G","0");state.put("7H","0");
		state.put("8A","0");state.put("8B","0");state.put("8C","0");state.put("8D","W");state.put("8E","0");state.put("8F","0");state.put("8G","0");state.put("8H","0");
		state.put("turn","B");
		return state;
		/*
		 * The game board is like this: (O:empty, B:black, W:white)
		 * 		A	B	C	D	E	F	G	H
		 * 	8	O	O	O	W	O	O	O	O
		 * 	7	O	O	O	O	O	O	O	O
		 * 	6	O	O	O	B	O	O	O	O
		 * 	5	O	O	O	O	O	O	O	O
		 * 	4	O	O	O	O	O	O	O	O
		 * 	3	O	O	O	B	O	O	O	O
		 * 	2	O	O	O	O	O	O	O	O
		 * 	1	W	O	O	O	O	O	O	B
		 */
	}

	@Test
	public void testLegalVerticalMove() {
		Map<String, Object> state = verticalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("6D","0")); lastMove.add(new Set("7D","B")); lastMove.add(new Set("turn","W")); 
		
		assertLegalMove(move(bId,state,lastMove));
	}
	
	@Test
	public void testIllegalVerticalMoveOverLimit() {
		// can not move 4 spaces(because there are only 3 pieces in the vertical line)
		Map<String, Object> state = verticalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("6D","0")); lastMove.add(new Set("2D","B")); lastMove.add(new Set("turn","W")); 
		
		assertIllegalMove(move(bId,state,lastMove));	
	}
	
	@Test
	public void testIllegalVerticalMoveNotEmpty() { 
		// the original space should be empty after move
		Map<String, Object> state = verticalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("6D","B")); lastMove.add(new Set("2D","B")); lastMove.add(new Set("turn","W")); 
		
		assertIllegalMove(move(bId,state,lastMove));	
	}
	
	@Test
	public void testLegalVerticalMoveCapture(){
		Map<String, Object> state = verticalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("6D","0")); lastMove.add(new Set("8D","B")); lastMove.add(new Set("turn","W")); 
		
		assertLegalMove(move(bId,state,lastMove));
		
	}
	
	
	@Test
	public void testIllegalVerticalMoveCaptureSelf(){  // can not capture one's own piece 
		Map<String, Object> state = verticalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("6D","0")); lastMove.add(new Set("3D","B")); lastMove.add(new Set("turn","W")); 
		
		assertIllegalMove(move(bId,state,lastMove));
	}
	
	@Test
	public void testIllegalVerticalMoveNotAllowed(){  // piece can only move horizontally, vertically, or diagonally
		Map<String, Object> state = verticalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("6D","0")); lastMove.add(new Set("7A","B")); lastMove.add(new Set("turn","W")); 
		
		assertIllegalMove(move(bId,state,lastMove));
	}
	
	// helper function to provide preset state
	private Map<String, Object> horizontalMoveState(){
		HashMap<String, Object> state = new HashMap<String, Object>();
		state.put("1A","B");state.put("1B","0");state.put("1C","0");state.put("1D","0");state.put("1E","0");state.put("1F","0");state.put("1G","0");state.put("1H","0");
		state.put("2A","0");state.put("2B","0");state.put("2C","0");state.put("2D","0");state.put("2E","0");state.put("2F","0");state.put("2G","0");state.put("2H","0");
		state.put("3A","0");state.put("3B","0");state.put("3C","0");state.put("3D","0");state.put("3E","0");state.put("3F","0");state.put("3G","0");state.put("3H","0");
		state.put("4A","0");state.put("4B","0");state.put("4C","0");state.put("4D","W");state.put("4E","B");state.put("4F","0");state.put("4G","0");state.put("4H","0");
		state.put("5A","0");state.put("5B","0");state.put("5C","0");state.put("5D","0");state.put("5E","0");state.put("5F","0");state.put("5G","0");state.put("5H","0");
		state.put("6A","0");state.put("6B","0");state.put("6C","0");state.put("6D","0");state.put("6E","0");state.put("6F","0");state.put("6G","0");state.put("6H","0");
		state.put("7A","0");state.put("7B","0");state.put("7C","0");state.put("7D","0");state.put("7E","0");state.put("7F","0");state.put("7G","0");state.put("7H","0");
		state.put("8A","W");state.put("8B","0");state.put("8C","0");state.put("8D","0");state.put("8E","0");state.put("8F","0");state.put("8G","0");state.put("8H","0");
		state.put("turn","B");
		return state;
		/*
		 * The game board is like this: (O:empty, B:black, W:white)
		 * 		A	B	C	D	E	F	G	H
		 * 	8	W	O	O	O	O	O	O	O
		 * 	7	O	O	O	O	O	O	O	O
		 * 	6	O	O	O	O	O	O	O	O
		 * 	5	O	O	O	O	O	O	O	O
		 * 	4	O	O	O	W	B	O	O	O
		 * 	3	O	O	O	O	O	O	O	O
		 * 	2	O	O	O	O	O	O	O	O
		 * 	1	B	O	O	O	O	O	O	O
		 */
	}
	
	@Test
	public void testLegalHorizontalMove(){  
		Map<String, Object> state = horizontalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("4E","0")); lastMove.add(new Set("4G","B")); lastMove.add(new Set("turn","W")); 
		
		assertLegalMove(move(bId,state,lastMove));
	}
	
	@Test
	public void testIllegalHorizontalMove(){   //can not move 3 spaces(there is only 2 pieces in the horizontal line)
		Map<String, Object> state = horizontalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("4E","0")); lastMove.add(new Set("4H","B")); lastMove.add(new Set("turn","W")); 
		
		assertIllegalMove(move(bId,state,lastMove));
	}
	
	@Test
	public void testIllegalHorizontalMoveJumpOverEnemy(){   //can not jump over enemy piece
		Map<String, Object> state = horizontalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("4E","0")); lastMove.add(new Set("4C","B")); lastMove.add(new Set("turn","W")); 
		
		assertIllegalMove(move(bId,state,lastMove));
	}
	
	@Test
	public void testIllegalHorizontalMoveNotAllowed(){  // piece can only move horizontally, vertically, or diagonally
		Map<String, Object> state = horizontalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("4E","0")); lastMove.add(new Set("7A","B")); lastMove.add(new Set("turn","W")); 
		
		assertIllegalMove(move(bId,state,lastMove));
	}
	
	// helper function to provide preset state
	private Map<String, Object> diagonalMoveState(){
		HashMap<String, Object> state = new HashMap<String, Object>();
		state.put("1A","0");state.put("1B","0");state.put("1C","0");state.put("1D","0");state.put("1E","0");state.put("1F","0");state.put("1G","0");state.put("1H","B");
		state.put("2A","0");state.put("2B","0");state.put("2C","0");state.put("2D","0");state.put("2E","W");state.put("2F","0");state.put("2G","0");state.put("2H","0");
		state.put("3A","0");state.put("3B","0");state.put("3C","0");state.put("3D","B");state.put("3E","0");state.put("3F","0");state.put("3G","0");state.put("3H","0");
		state.put("4A","0");state.put("4B","0");state.put("4C","0");state.put("4D","0");state.put("4E","0");state.put("4F","0");state.put("4G","0");state.put("4H","0");
		state.put("5A","0");state.put("5B","B");state.put("5C","0");state.put("5D","0");state.put("5E","0");state.put("5F","0");state.put("5G","0");state.put("5H","0");
		state.put("6A","0");state.put("6B","0");state.put("6C","0");state.put("6D","0");state.put("6E","0");state.put("6F","0");state.put("6G","0");state.put("6H","0");
		state.put("7A","0");state.put("7B","0");state.put("7C","0");state.put("7D","0");state.put("7E","0");state.put("7F","0");state.put("7G","0");state.put("7H","0");
		state.put("8A","0");state.put("8B","0");state.put("8C","0");state.put("8D","0");state.put("8E","0");state.put("8F","0");state.put("8G","0");state.put("8H","W");
		state.put("turn","B");
		return state;
		/*
		 * The game board is like this: (O:empty, B:black, W:white)
		 * 		A	B	C	D	E	F	G	H
		 * 	8	O	O	O	O	O	O	O	W
		 * 	7	O	O	O	O	O	O	O	O
		 * 	6	O	O	O	O	O	O	O	O
		 * 	5	O	B	O	O	O	O	O	O
		 * 	4	O	O	O	O	O	O	O	O
		 * 	3	O	O	O	B	O	O	O	O
		 * 	2	O	O	O	O	W	O	O	O
		 * 	1	O	O	O	O	O	O	O	B
		 */
	}
	
	@Test
	public void testLegalDiagonalMove(){  
		Map<String, Object> state = diagonalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("3D","0")); lastMove.add(new Set("4C","B")); lastMove.add(new Set("turn","W")); 
		
		assertLegalMove(move(bId,state,lastMove));
	}

	@Test
	public void testIllegalDiagonalMoveNotEmpty(){ // the original space should be empty after move  
		Map<String, Object> state = diagonalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("3D","B")); lastMove.add(new Set("4C","B")); lastMove.add(new Set("turn","W")); 
		
		assertIllegalMove(move(bId,state,lastMove));
	}
	
	@Test
	public void testIllegalDiagonalMoveNotTurned(){ // the turn of the game should be switched after move  
		Map<String, Object> state = diagonalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("3D","0")); lastMove.add(new Set("4C","B")); lastMove.add(new Set("turn","B")); 
		
		assertIllegalMove(move(bId,state,lastMove));
	}
	
	@Test
	public void testLegalDiagonalMoveJumpOver(){  //jump over friendly piece is allowed
		Map<String, Object> state = diagonalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("3D","0")); lastMove.add(new Set("6A","B")); lastMove.add(new Set("turn","W")); 
		
		assertLegalMove(move(bId,state,lastMove));
	}
	
	@Test
	public void testIllegalDiagonalMoveJumpOverEnemy(){ //jump over enemy piece is not allowed  
		Map<String, Object> state = diagonalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("3D","0")); lastMove.add(new Set("1F","B")); lastMove.add(new Set("turn","W")); 
		
		assertIllegalMove(move(bId,state,lastMove));
	}
	
	@Test
	public void testIllegalDiagonalMoveCaptureSelf(){ //can not capture one's own piece 
		Map<String, Object> state = diagonalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("3D","0")); lastMove.add(new Set("5B","B")); lastMove.add(new Set("turn","W")); 
		
		assertIllegalMove(move(bId,state,lastMove));
	}
	
	@Test
	public void testLegalDiagonalMoveCapture(){
		Map<String, Object> state = diagonalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("3D","0")); lastMove.add(new Set("2E","B")); lastMove.add(new Set("turn","W")); 
		
		assertLegalMove(move(bId,state,lastMove));
	}
	
	@Test
	public void testIllegalDiagonalMoveNoMove(){ //can not stay at the original space after move 
		Map<String, Object> state = diagonalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("3D","0")); lastMove.add(new Set("3D","B")); lastMove.add(new Set("turn","W")); 
		
		assertIllegalMove(move(bId,state,lastMove));
	}
	
	@Test
	public void testIllegalDiagonalMoveOriginalSpaceTurned(){
		// the original space should be empty after move, not "W"
		Map<String, Object> state = diagonalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("3D","W")); lastMove.add(new Set("4C","B")); lastMove.add(new Set("turn","W")); 
		
		assertIllegalMove(move(bId,state,lastMove));
	}
	
	@Test
	public void testLegalDiagonalMoveTheOtherDiagonal(){
		Map<String, Object> state = diagonalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("5B","0")); lastMove.add(new Set("6C","B")); lastMove.add(new Set("turn","W")); 
		
		assertLegalMove(move(bId,state,lastMove));
	}
	
	@Test
	public void testIllegalDiagonalMoveEmptyOrigin(){
		// there was not a Black piece in the implied origin "4C"
		Map<String, Object> state = diagonalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("4C","0")); lastMove.add(new Set("6A","B")); lastMove.add(new Set("turn","W")); 
		
		assertIllegalMove(move(bId,state,lastMove));
	}
	
	@Test
	public void testIllegalDiagonalMoveWhiteOrigin(){
		// there was a White piece(not a Black piece) in the implied origin "2E"
		Map<String, Object> state = diagonalMoveState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("2E","0")); lastMove.add(new Set("4C","B")); lastMove.add(new Set("turn","W")); 
		
		assertIllegalMove(move(bId,state,lastMove));
	}
	
	// helper function to provide preset state
	private Map<String, Object> beforeEndGameState(){
		HashMap<String, Object> state = new HashMap<String, Object>();
		state.put("1A","0");state.put("1B","0");state.put("1C","0");state.put("1D","0");state.put("1E","0");state.put("1F","0");state.put("1G","0");state.put("1H","0");
		state.put("2A","0");state.put("2B","0");state.put("2C","0");state.put("2D","0");state.put("2E","0");state.put("2F","0");state.put("2G","0");state.put("2H","0");
		state.put("3A","0");state.put("3B","0");state.put("3C","0");state.put("3D","0");state.put("3E","0");state.put("3F","0");state.put("3G","0");state.put("3H","0");
		state.put("4A","0");state.put("4B","0");state.put("4C","0");state.put("4D","0");state.put("4E","0");state.put("4F","0");state.put("4G","0");state.put("4H","0");
		state.put("5A","0");state.put("5B","0");state.put("5C","0");state.put("5D","W");state.put("5E","0");state.put("5F","0");state.put("5G","0");state.put("5H","0");
		state.put("6A","0");state.put("6B","0");state.put("6C","0");state.put("6D","0");state.put("6E","0");state.put("6F","0");state.put("6G","B");state.put("6H","0");
		state.put("7A","0");state.put("7B","0");state.put("7C","W");state.put("7D","0");state.put("7E","B");state.put("7F","0");state.put("7G","0");state.put("7H","0");
		state.put("8A","0");state.put("8B","0");state.put("8C","0");state.put("8D","0");state.put("8E","0");state.put("8F","0");state.put("8G","0");state.put("8H","0");
		state.put("turn","B");
		return state;
		/*
		 * The game board is like this: (O:empty, B:black, W:white)
		 * 		A	B	C	D	E	F	G	H
		 * 	8	O	O	O	O	O	O	O	O
		 * 	7	O	O	W	O	B	O	O	O
		 * 	6	O	O	O	O	O	O	B	O
		 * 	5	O	O	O	W	O	O	O	O
		 * 	4	O	O	O	O	O	O	O	O
		 * 	3	O	O	O	O	O	O	O	O
		 * 	2	O	O	O	O	O	O	O	O
		 * 	1	O	O	O	O	O	O	O	O
		 */
	}
	
	@Test
	public void testLegalEndGame(){
		Map<String, Object> state = beforeEndGameState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("7E","0")); lastMove.add(new Set("7G","B")); lastMove.add(new Set("turn","W"));
		lastMove.add(new EndGame(bId));
		
		assertLegalMove(move(bId,state,lastMove));
	}
	
	@Test
	public void testIllegalEndGame(){
		Map<String, Object> state = beforeEndGameState();
		
		ArrayList<Operation> lastMove = new ArrayList<Operation>();
		lastMove.add(new Set("7E","0")); lastMove.add(new Set("7D","B")); lastMove.add(new Set("turn","W"));
		lastMove.add(new EndGame(bId));
		
		assertIllegalMove(move(bId,state,lastMove));
	}
}
