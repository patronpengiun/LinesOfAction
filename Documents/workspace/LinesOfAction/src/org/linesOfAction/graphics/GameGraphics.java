package org.linesOfAction.graphics;

import org.linesOfAction.client.GamePresenter;

import java.util.ArrayList;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.media.client.Audio;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class GameGraphics extends Composite implements GamePresenter.View{
	
	public interface GameGraphicsUiBinder extends UiBinder<Widget, GameGraphics> {}
	
	@UiField
	Grid gameGrid;
	
	@UiField
	AbsolutePanel boardPanel;

	private boolean enableClick;
	public boolean dragValid;
	private ArrayList<String> possibleClickPositions;
	private final PieceImageSupplier pieceImageSupplier;
	private GamePresenter presenter;
	public AbsolutePanel imageContainer[][];
	
	public GameGraphics() {
		enableClick = false;
		dragValid = false;
		
		GameSounds gameSounds = GWT.create(GameSounds.class);

	    PieceImages pieceImages = GWT.create(PieceImages.class);
	    this.pieceImageSupplier = new PieceImageSupplier(pieceImages);
	    GameGraphicsUiBinder uiBinder = GWT.create(GameGraphicsUiBinder.class);
	    initWidget(uiBinder.createAndBindUi(this));
	    
	    imageContainer = new AbsolutePanel[8][8];
	    initializeGrid();
	    
	    for (int i=0;i<8;i++)
	    	for (int j=0;j<8;j++){
	    		imageContainer[i][j] = new AbsolutePanel(); 
	    		imageContainer[i][j].setStyleName("imgContainer");
	    		gameGrid.setWidget(i, j, imageContainer[i][j]);
	    	}
	    
	    initializeBoard(false);
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
		initializeBoard(true);
	}
	
	private void initializeBoard(boolean flag){
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
	    				if (enableClick && clickCheck(row,col)) {
	    					enableClick = false; // cannot click another position until choosePosition is called
	    					presenter.positionSelected(position,false);
			            }
			          }
				});	
	    		imageContainer[i][j].clear();
	    		imageContainer[i][j].setPixelSize(42, 42);
	    		imageContainer[i][j].add(image);
	    	}
	}
		

	@Override
	public void setState(int[][] board, int[][] lastBoard, String from, String to, int color, boolean isDrag) {
		int moveFromX = from.charAt(0) - '1';
		int moveFromY = from.charAt(1) - 'A';
		int moveToX = to.charAt(0) - '1';
		int moveToY = to.charAt(1) - 'A';
		for (int i=0;i<8;i++)
	    	for (int j=0;j<8;j++) {
	    		final int row = i;
	    		final int col = j;
	    		StringBuilder str = new StringBuilder(Character.toString((char)('1'+i)));
	    		str.append((char)('A'+j));
	    		final String position = str.toString();
	    		Image image = getImage(board[i][j]);
	    		image.addClickHandler(new ClickHandler() {
	    			@Override
			          public void onClick(ClickEvent event) {
	    				if (enableClick && clickCheck(row,col)) {
	    					enableClick = false; // cannot click another position until choosePosition is called
	    					//System.out.println("set false! click");
	    					presenter.positionSelected(position,false);
			            }
			          }
				});	
	    		
	    		if ((i != moveFromX || j != moveFromY) && (i != moveToX || j != moveToY)){
		    		imageContainer[i][j].clear();
		    		imageContainer[i][j].setPixelSize(42, 42);
		    		imageContainer[i][j].add(image);
	    		}
	    		else{
	    			if (!isDrag){
	    				imageContainer[i][j].clear();
			    		imageContainer[i][j].setPixelSize(42, 42);
			    		Image oldImg = getImage(lastBoard[i][j]);
			    		oldImg.addClickHandler(new ClickHandler(){
			    			@Override
					          public void onClick(ClickEvent event) {
			    				if (enableClick && clickCheck(row,col)) {
			    					enableClick = false; // cannot click another position until choosePosition is called
			    					presenter.positionSelected(position,false);
					            }
					          }
			    		});
			    		imageContainer[i][j].add(oldImg);
	    			}
	    			else{
	    				imageContainer[i][j].clear();
			    		imageContainer[i][j].setPixelSize(42, 42);
			    		imageContainer[i][j].add(image);
	    			}
	    		}
	    	}
		if (!isDrag){
			PieceMovingAnimation animation = new PieceMovingAnimation(from,to,color);
			animation.run(1000);
		}
		else {
			playSound();
        }
	}
	
	@Override
	public void resetGraphics(int[][] board){
		for (int i=0;i<8;i++)
	    	for (int j=0;j<8;j++) {
	    		final int row = i;
	    		final int col = j;
	    		StringBuilder str = new StringBuilder(Character.toString((char)('1'+i)));
	    		str.append((char)('A'+j));
	    		final String position = str.toString();
	    		Image image = getImage(board[i][j]);
	    		image.addClickHandler(new ClickHandler() {
	    			@Override
			          public void onClick(ClickEvent event) {
	    				if (enableClick && clickCheck(row,col)) {
	    					enableClick = false; // cannot click another position until choosePosition is called
	    					presenter.positionSelected(position,false);
			            }
			          }
				});	
	    		imageContainer[i][j].clear();
	    		imageContainer[i][j].setPixelSize(42, 42);
	    		imageContainer[i][j].add(image);
	    	}
	}
	
	@Override
	public void choosePosition(String origin, String destination, ArrayList<String> possiblePosition){
		enableClick = true;
		possibleClickPositions = possiblePosition;
		//System.out.println("set true! choose pos");
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
	
	
	public boolean clickCheck(int row,int col){
		StringBuilder str = new StringBuilder(Character.toString((char)('1'+row)));
        str.append((char)('A'+col));
        if (possibleClickPositions.contains(str.toString())) 
        	return true;
        else 
        	return false;
	}
	
	public class PieceMovingAnimation extends Animation {

        AbsolutePanel panel;
        Image start, end, moving;
        int startX, startY, startWidth, startHeight;
        int endX, endY, color;
        int moveFromX,moveFromY,moveToX,moveToY;
        String moveFrom,moveTo;
        public PieceMovingAnimation(String from, String to, int clr) {
        		moveFromX = from.charAt(0) - '1';
    			moveFromY = from.charAt(1) - 'A';
    			moveToX = to.charAt(0) - '1';
    			moveToY = to.charAt(1) - 'A';
    			moveFrom = from;
    			moveTo = to;
    			color = clr;
    		
                start = (Image)imageContainer[moveFromX][moveFromY].getWidget(0);
                end = (Image)imageContainer[moveToX][moveToY].getWidget(0);
                panel = (AbsolutePanel) start.getParent();
                startX = panel.getWidgetLeft(start);
                startY = panel.getWidgetTop(start);
                startWidth = start.getWidth();
                startHeight = start.getHeight();
                endX = end.getAbsoluteLeft() - start.getAbsoluteLeft() + startX;
                endY = end.getAbsoluteTop() - start.getAbsoluteTop() + startY;
                moving = getImage(color);
                panel.add(moving, startX, startY);
        }

        @Override
        protected void onUpdate(double progress) {
                int x = (int) (startX + (endX - startX) * progress / Double.parseDouble(getScale()));
                int y = (int) (startY + (endY - startY) * progress / Double.parseDouble(getScale()));
                panel.setWidgetPosition(moving, x, y);
        }
        
        @Override
        protected void onStart(){
        	panel.getElement().addClassName("showMove");
        	
        	panel.remove(start);
        	Image emptyImg = getImage(0);
        	emptyImg.addClickHandler(new ClickHandler() {
    			@Override
		          public void onClick(ClickEvent event) {
    				if (enableClick && clickCheck(moveFromX,moveFromY)) {
    					enableClick = false; // cannot click another position until choosePosition is called
    					presenter.positionSelected(moveFrom,false);
		            }
		          }
			});	
        	panel.add(emptyImg);
        	moving.getElement().setClassName("movingImg");
        }

        @Override
        protected void onComplete() {
        	panel.remove(moving);
        	
        	Image pieceImg = getImage(color);
        	pieceImg.addClickHandler(new ClickHandler() {
    			@Override
		          public void onClick(ClickEvent event) {
    				if (enableClick && clickCheck(moveToX,moveToY)) {
    					enableClick = false; // cannot click another position until choosePosition is called
    					presenter.positionSelected(moveTo,false);
		            }
		          }
			});	
        	imageContainer[moveToX][moveToY].clear();
        	imageContainer[moveToX][moveToY].add(pieceImg);
        	playSound();
        }
	}
	
	private static native String getScale()/*-{
		return $doc.getElementById('scale').value;
	}-*/ ;
	
	private static native void playSound()/*-{
		$doc.getElementsByTagName('audio')[0].load()
		$doc.getElementsByTagName('audio')[0].play()
	}-*/ ;


}
