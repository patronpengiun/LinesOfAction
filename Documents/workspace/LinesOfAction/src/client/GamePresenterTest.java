package client;

import org.mockito.Mockito;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import client.GameApi.UpdateUI;
import client.GameApi.Operation;
import client.GameApi.Set;
import client.GameApi.SetTurn;
import client.GameApi.EndGame;
import client.GameApi.Container;
import client.GamePresenter.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RunWith(JUnit4.class)
public class GamePresenterTest {
	 private GamePresenter gamePresenter;
	 private View mockView;
	 private Container mockContainer;
	 
	 private static final String PLAYER_ID = "playerId";

	 private final int viewerId = GameApi.VIEWER_ID;
	 private final int wId = 98;
	 private final int bId = 99;

	 // helper function to generate an empty state
	 private  Map<String, Object> emptyState(){
		 return new HashMap<String,Object>();
	 }
	 
	 // helper function to generate an UpdateUI instance
	 private UpdateUI createUpdateUI(int yourPlayerId,int lastMovePlayerId,Map<String, Object> state,List<Operation> operations) {
		 // we ignore: lastState, playerIdToNumberOfTokensInPot
		 List<Integer> playerIds = new ArrayList<Integer>(); playerIds.add(wId); playerIds.add(bId);
		 Map<String,Object> wInfo = new HashMap<String,Object>(); wInfo.put(PLAYER_ID,wId);
		 Map<String,Object> bInfo = new HashMap<String,Object>(); bInfo.put(PLAYER_ID,bId);
		 List<Map<String,Object>> playersInfo = new ArrayList<Map<String,Object>>();
		 playersInfo.add(wInfo); playersInfo.add(bInfo);
		 Map<Integer,Integer> idToToken = new HashMap<Integer,Integer>();
		 return new UpdateUI(yourPlayerId, playersInfo, state,
				emptyState(),
		        operations,
		        lastMovePlayerId,
		        idToToken);
	 }
	 
	 @Before
	  public void runBefore() {
	    mockView = Mockito.mock(View.class);
	    mockContainer = Mockito.mock(Container.class);
	    gamePresenter = new GamePresenter(mockView, mockContainer);
	    verify(mockView).setPresenter(gamePresenter);
	  }

	  @After
	  public void runAfter() {
	    // This will ensure I didn't forget to declare any extra interaction the mocks have.
	    verifyNoMoreInteractions(mockContainer);
	    verifyNoMoreInteractions(mockView);
	  }
	  
	  // helper function to provide initial state
	  private  Map<String, Object> initialState(){
		  HashMap<String, Object> state = new HashMap<String, Object>();
		  state.put("turn","W"); // initial state only contains turn information.
		  return state;
	  }
	
	  // helper function to provide initial move for the following test cases
	  private List<Operation> initialMove(){
		  List<Operation> operations = new ArrayList<Operation>();
		  operations.add(new SetTurn(wId)); 
		  return operations;
	  }

	  /*
	   * The following test cases are to test whether View.setInitialState is correctly called 
	   * given initial state in the updateUI. Also test choosePosition is only called when it 
	   * is the player's turn to make move
	   */
	  @Test  // ignore lastMovePlayerId, as -100 in all initialStateTest
	  public void testInitialStateForW() {
		  gamePresenter.updateUI(createUpdateUI(wId, -100, initialState(), initialMove()));
		  verify(mockView).setInitialState("W");
		  verify(mockView).choosePosition("", "");
	  }
	  
	  @Test
	  public void testInitialStateForB() {
		  gamePresenter.updateUI(createUpdateUI(bId, -100, initialState(), initialMove()));
		  verify(mockView).setInitialState("B");
	  }
	  
	  @Test
	  public void testInitialStateForViewer() {
		  gamePresenter.updateUI(createUpdateUI(viewerId, -100, initialState(), initialMove()));
		  verify(mockView).setInitialState("");
	  }
	  // end of initial state test cases
	  
	  // helper function to provide normal test state
	  private  Map<String, Object> normalState(String turn){
		  HashMap<String, Object> state = new HashMap<String, Object>();
		  state.put("1A","0");state.put("1B","B");state.put("1C","B");state.put("1D","B");state.put("1E","B");state.put("1F","B");state.put("1G","B");state.put("1H","0");
		  state.put("2A","W");state.put("2B","0");state.put("2C","0");state.put("2D","0");state.put("2E","0");state.put("2F","0");state.put("2G","0");state.put("2H","W");
		  state.put("3A","W");state.put("3B","0");state.put("3C","0");state.put("3D","0");state.put("3E","0");state.put("3F","0");state.put("3G","0");state.put("3H","W");
		  state.put("4A","0");state.put("4B","W");state.put("4C","0");state.put("4D","0");state.put("4E","0");state.put("4F","0");state.put("4G","0");state.put("4H","W");
		  state.put("5A","W");state.put("5B","0");state.put("5C","0");state.put("5D","0");state.put("5E","0");state.put("5F","0");state.put("5G","0");state.put("5H","W");
		  state.put("6A","W");state.put("6B","0");state.put("6C","0");state.put("6D","0");state.put("6E","0");state.put("6F","B");state.put("6G","0");state.put("6H","W");
		  state.put("7A","0");state.put("7B","B");state.put("7C","W");state.put("7D","0");state.put("7E","0");state.put("7F","0");state.put("7G","0");state.put("7H","W");
		  state.put("8A","0");state.put("8B","0");state.put("8C","B");state.put("8D","B");state.put("8E","B");state.put("8F","0");state.put("8G","B");state.put("8H","0");
		  state.put("turn",turn);
		  return state;
	  }
		
	  // helper function to provide last move for following test cases
	  private List<Operation> lastMove(String turn){
		  List<Operation> operations = new ArrayList<Operation>();
		  if ("W" == turn){
			  operations.add(new Set("8B","0"));
			  operations.add(new Set("7B","B"));
			  operations.add(new SetTurn(wId)); 
		  }
		  else{
			  operations.add(new Set("7A","0"));
			  operations.add(new Set("7C","W"));
			  operations.add(new SetTurn(bId)); 
		  }
		  return operations;
	  }
	  
	  
	 /* The following test cases are to test whether view.setState() is called correctly given 
	  * some normal in-the-middle states. Also test whether choosePosition in only called when
	  * it it the player's turn to make move.
	  * 
	  */
	  @Test
	  public void testNormalStateTurnOfW_ForW(){
		  gamePresenter.updateUI(createUpdateUI(wId, bId, normalState("W"), lastMove("W")));
		  verify(mockView).setState("8B","7B");
		  verify(mockView).choosePosition("","");
	  }
	  
	  @Test
	  public void testNormalStateTurnOfW_ForB(){
		  gamePresenter.updateUI(createUpdateUI(bId, bId, normalState("W"), lastMove("W")));
		  verify(mockView).setState("8B","7B");  
	  }
	  
	  @Test
	  public void testNormalStateTurnOfW_ForViewer(){
		  gamePresenter.updateUI(createUpdateUI(viewerId, bId, normalState("W"), lastMove("W")));
		  verify(mockView).setState("8B","7B");  
	  }
	  
	  @Test
	  public void testNormalStateTurnOfB_ForW(){
		  gamePresenter.updateUI(createUpdateUI(wId, wId, normalState("B"), lastMove("B")));
		  verify(mockView).setState("7A","7C"); 	  
	  }
	  
	  @Test
	  public void testNormalStateTurnOfB_ForB(){
		  gamePresenter.updateUI(createUpdateUI(bId, wId, normalState("B"), lastMove("B")));
		  verify(mockView).setState("7A","7C"); 
		  verify(mockView).choosePosition("","");
	  }
	  
	  @Test
	  public void testNormalStateTurnOfB_ForViewer(){
		  gamePresenter.updateUI(createUpdateUI(viewerId, wId, normalState("B"), lastMove("B")));
		  verify(mockView).setState("7A","7C"); 			  
	  }
	  // End of normal state test cases
	  
	  // helper function to provide nearly ending test state ("W" to move)
	  private  Map<String, Object> nearlyEndingState(){
		  HashMap<String, Object> state = new HashMap<String, Object>();
		  state.put("1A","0");state.put("1B","0");state.put("1C","0");state.put("1D","B");state.put("1E","0");state.put("1F","0");state.put("1G","0");state.put("1H","0");
		  state.put("2A","0");state.put("2B","0");state.put("2C","0");state.put("2D","0");state.put("2E","0");state.put("2F","0");state.put("2G","0");state.put("2H","0");
		  state.put("3A","0");state.put("3B","B");state.put("3C","B");state.put("3D","0");state.put("3E","0");state.put("3F","0");state.put("3G","0");state.put("3H","0");
		  state.put("4A","0");state.put("4B","0");state.put("4C","0");state.put("4D","W");state.put("4E","0");state.put("4F","0");state.put("4G","0");state.put("4H","0");
		  state.put("5A","0");state.put("5B","0");state.put("5C","0");state.put("5D","W");state.put("5E","0");state.put("5F","0");state.put("5G","0");state.put("5H","0");
		  state.put("6A","0");state.put("6B","0");state.put("6C","0");state.put("6D","W");state.put("6E","0");state.put("6F","0");state.put("6G","0");state.put("6H","0");
		  state.put("7A","0");state.put("7B","0");state.put("7C","0");state.put("7D","0");state.put("7E","0");state.put("7F","0");state.put("7G","0");state.put("7H","0");
		  state.put("8A","0");state.put("8B","0");state.put("8C","W");state.put("8D","0");state.put("8E","0");state.put("8F","0");state.put("8G","0");state.put("8H","0");
		  state.put("turn","W");
		  return state;
	  }
			
	  // helper function to provide last move in following test cases
	  private List<Operation> theLastMove(){
		  List<Operation> operations = new ArrayList<Operation>();
		  operations.add(new Set("4B","0"));
		  operations.add(new Set("3B","B"));
		  operations.add(new SetTurn(wId)); 

		  return operations;
	  }
	  
	  /* The following test cases are to test the interaction between presenter and view
	   * (e.g. choosePosition & positionSelected). Also test whether container.sendMakeMove 
	   * is correctly called when origin and destination are both selected.(Both normal makeMove
	   * and makeMove that includes the EndGame operation)
	   */
	  @Test
	  public void testNearlyEndingStateTurnOfW_ForW_SelectedOrigin(){
		  gamePresenter.updateUI(createUpdateUI(wId, bId, nearlyEndingState(), theLastMove()));
		  verify(mockView).setState("4B","3B");
		  verify(mockView).choosePosition("","");
		  gamePresenter.positionSelected("8C");
		  verify(mockView).choosePosition("8C","");
	  }
	  
	  @Test
	  public void testNearlyEndingStateTurnOfW_ForW_CanceledOrigin(){
		  gamePresenter.updateUI(createUpdateUI(wId, bId, nearlyEndingState(), theLastMove()));
		  verify(mockView).setState("4B","3B");
		  verify(mockView).choosePosition("","");
		  gamePresenter.positionSelected("8C");
		  verify(mockView).choosePosition("8C","");
		  gamePresenter.positionSelected("8C");  //select the same position as the origin, origin got canceled
		  verify(mockView).choosePosition("","");
	  }
	  
	  @Test
	  public void testNearlyEndingStateTurnOfW_ForW_SelectedDestinationButNotEndGame(){
		  gamePresenter.updateUI(createUpdateUI(wId, bId, nearlyEndingState(), theLastMove()));
		  verify(mockView).setState("4B","3B");
		  verify(mockView).choosePosition("","");
		  gamePresenter.positionSelected("8C");  //selected an origin
		  verify(mockView).choosePosition("8C","");
		  gamePresenter.positionSelected("8D");  //selected a destination that did not end the game
		  List<Operation> operations = new ArrayList<Operation>();
		  operations.add(new Set("8C","0"));
		  operations.add(new Set("8D","W"));
		  operations.add(new SetTurn(bId));
		  verify(mockContainer).sendMakeMove(operations);
	  }
	  
	  @Test
	  public void testNearlyEndingStateTurnOfW_ForW_SelectedDestinationThatEndGame(){
		  gamePresenter.updateUI(createUpdateUI(wId, bId, nearlyEndingState(), theLastMove()));
		  verify(mockView).setState("4B","3B");
		  verify(mockView).choosePosition("","");
		  gamePresenter.positionSelected("8C");	 //selected an origin
		  verify(mockView).choosePosition("8C","");
		  gamePresenter.positionSelected("6C");  //selected a destination that ended the game
		  List<Operation> operations = new ArrayList<Operation>();
		  operations.add(new Set("8C","0"));
		  operations.add(new Set("6C","W"));
		  operations.add(new SetTurn(bId));
		  operations.add(new EndGame(wId));
		  verify(mockContainer).sendMakeMove(operations);
	  }
	 
	
		
}
