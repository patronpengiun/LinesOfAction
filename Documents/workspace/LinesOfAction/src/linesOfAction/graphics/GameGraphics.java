package linesOfAction.graphics;

import linesOfAction.client.GamePresenter;

import java.util.ArrayList;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

public class GameGraphics extends Composite implements GamePresenter.View{
	
	public interface GameGraphicsUiBinder extends UiBinder<Widget, GameGraphics> {}
	
	@UiField
	Grid gameGrid;

	private boolean enableClick;
	private ArrayList<String> possibleClickPositions;
	private final PieceImageSupplier pieceImageSupplier;
	private GamePresenter presenter;
	private FlowPanel imageContainer[][];

	
	public GameGraphics() {
		enableClick = false;
		System.out.println("set false! constructor");
	    PieceImages pieceImages = GWT.create(PieceImages.class);
	    this.pieceImageSupplier = new PieceImageSupplier(pieceImages);
	    GameGraphicsUiBinder uiBinder = GWT.create(GameGraphicsUiBinder.class);
	    initWidget(uiBinder.createAndBindUi(this));
	    imageContainer = new FlowPanel[8][8];
	    initializeGrid();
	    
	    for (int i=0;i<8;i++)
	    	for (int j=0;j<8;j++){
	    		imageContainer[i][j] = new FlowPanel(); 
	    		imageContainer[i][j].setStyleName("imgContainer");
	    		gameGrid.setWidget(i, j, imageContainer[i][j]);
	    	}
	}
	
	private void initializeGrid(){
		gameGrid.setPixelSize(336, 336);
		gameGrid.resize(8, 8);
		gameGrid.setCellSpacing(0);
		gameGrid.setCellPadding(0);
		gameGrid.setBorderWidth(1);
	}
	
	@Override
	public void setPresenter(GamePresenter gamePresenter) {
		this.presenter = gamePresenter;
	}
	
	@Override
	public void setInitialState(String color) {
		enableClick = false; //cannot click until choosePosition is called
		System.out.println("set false! set initial");
		
	    int[][] initialBoard = new int[][]{
	    		  { 0, 1, 1, 1, 1, 1, 1, 0 },
	    		  { 2, 0, 0, 0, 0, 0, 0, 2 },
	    		  { 2, 0, 0, 0, 0, 0, 0, 2 },
	    		  { 2, 0, 0, 0, 0, 0, 0, 2 },
	    		  { 2, 0, 0, 0, 0, 0, 0, 2 },
	    		  { 2, 0, 0, 0, 0, 0, 0, 2 },
	    		  { 2, 0, 0, 0, 0, 0, 0, 2 },
	    		  { 0, 1, 1, 1, 1, 1, 1, 0 }
	    		};
	    
	    for (int i=0;i<8;i++)
	    	for (int j=0;j<8;j++) {
	    		final int row = i;
	    		final int col = j;
	    		StringBuilder str = new StringBuilder(Character.toString((char)('1'+i)));
	    		str.append((char)('A'+j));
	    		final String position = str.toString();
	    		Image image = getImage(initialBoard[i][j]);
	    		image.addClickHandler(new ClickHandler() {
	    			@Override
			          public void onClick(ClickEvent event) {
    					System.out.println(row);
    					System.out.println(col);
    					
	    				if (enableClick && clickCheck(row,col)) {
	    					enableClick = false; // cannot click another position until choosePosition is called
	    					System.out.println("set false! click");
	    					presenter.positionSelected(position);
			            }
			          }
				});	
	    		imageContainer[i][j].setPixelSize(42, 42);
	    		imageContainer[i][j].add(image);
	    	}
	}
		

	@Override
	public void setState(String moveFrom, String moveTo, int color) {
		final int from_row = moveFrom.charAt(0) - '1';
		final int from_col = moveFrom.charAt(1) - 'A';
		final String fromPosition = moveFrom;
		final String toPosition = moveTo;
		final int to_row = moveTo.charAt(0) - '1';
		final int to_col = moveTo.charAt(1) - 'A';
		imageContainer[from_row][from_col].clear();
		Image image = getImage(0);
		image.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (enableClick && clickCheck(from_row,from_col)) {
					enableClick = false; // cannot click another position until choosePosition is called
					System.out.println("set false! click");
					presenter.positionSelected(fromPosition);
	            }
	          }
		});	
		imageContainer[from_row][from_col].add(image);
		
		imageContainer[to_row][to_col].clear();
		image = getImage(color);
		image.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (enableClick && clickCheck(to_row,to_col)) {
					enableClick = false; // cannot click another position until choosePosition is called
					System.out.println("set false! click");
					presenter.positionSelected(toPosition);
	            }
	          }
		});	
		imageContainer[to_row][to_col].add(image);
	}
	
	@Override
	public void choosePosition(String orgin, String destination, ArrayList<String> possiblePosition){
		enableClick = true;
		possibleClickPositions = possiblePosition;
		System.out.println("set true! choose pos");
	}
	
	private Image getImage(int color){
		PieceImage img;
		if (color == 1)
			img = PieceImage.Factory.getBlack();
		else if (color == 2)
			img = PieceImage.Factory.getWhite();
		else 
			img = PieceImage.Factory.getEmpty();
		return new Image(pieceImageSupplier.getResource(img));
	}
	
	@Override
	public void declareWinner(boolean winner){
		if (winner) 
			Window.alert("You Win!");
		else 
			Window.alert("You Lose!");
	}
	
	
	private boolean clickCheck(int row,int col){
		StringBuilder str = new StringBuilder(Character.toString((char)('1'+row)));
        str.append((char)('A'+col));
        if (possibleClickPositions.contains(str.toString())) 
        	return true;
        else 
        	return false;
	}

}
