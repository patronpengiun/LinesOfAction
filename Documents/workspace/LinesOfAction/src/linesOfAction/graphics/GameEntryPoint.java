package linesOfAction.graphics;

import linesOfAction.client.CheatLogic;
import linesOfAction.client.GamePresenter;
import linesOfAction.client.GameApi;
import linesOfAction.client.GameApi.Game;
import linesOfAction.client.GameApi.IteratingPlayerContainer;
import linesOfAction.client.GameApi.UpdateUI;
import linesOfAction.client.GameApi.VerifyMove;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;

public class GameEntryPoint implements EntryPoint {
	  IteratingPlayerContainer container;
	  GamePresenter gamePresenter;
	  
	  @Override
	  public void onModuleLoad() {
	    Game game = new Game() {
	      @Override
	      public void sendVerifyMove(VerifyMove verifyMove) {
	        container.sendVerifyMoveDone(new CheatLogic().verify(verifyMove));
	      }

	      @Override
	      public void sendUpdateUI(UpdateUI updateUI) {
	        gamePresenter.updateUI(updateUI);
	      }
	    };
	    container = new IteratingPlayerContainer(game, 2);
	    GameGraphics gameGraphics = new GameGraphics();
	    gamePresenter = new GamePresenter(gameGraphics, container);
	    final ListBox playerSelect = new ListBox();
	    playerSelect.addItem("WhitePlayer");
	    playerSelect.addItem("BlackPlayer");
	    playerSelect.addItem("Viewer");
	    playerSelect.addChangeHandler(new ChangeHandler() {
	      @Override
	      public void onChange(ChangeEvent event) {
	        int selectedIndex = playerSelect.getSelectedIndex();
	        int playerId = selectedIndex == 2 ? GameApi.VIEWER_ID
	            : container.getPlayerIds().get(selectedIndex);
	        container.updateUi(playerId);
	      }
	    });
	    FlowPanel flowPanel = new FlowPanel();
	    flowPanel.add(gameGraphics);
	    flowPanel.add(playerSelect);
	    RootPanel.get("mainDiv").add(flowPanel);
	    container.sendGameReady();
	    container.updateUi(container.getPlayerIds().get(0));
	  	}
	}
