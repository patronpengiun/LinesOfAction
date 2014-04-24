package org.linesOfAction.graphics;

import org.linesOfAction.client.GamePresenter;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class GameDropController extends SimpleDropController{
	private final GamePresenter presenter;
	private final GameGraphics graphics;
	private int row;
	private int col;
	private AbsolutePanel thePanel;
	
	
	public GameDropController (AbsolutePanel panel, GamePresenter p, GameGraphics g, int r, int c){
		super(panel);
		presenter = p;
		graphics = g;
		row = r;
		col = c;
		thePanel = panel;
	}
	
	@Override
	public void onDrop(DragContext context) {
		if (graphics.dragValid && graphics.clickCheck(row, col)){
			StringBuilder str = new StringBuilder(Character.toString((char)('1'+row)));
    		str.append((char)('A'+col));
			presenter.positionSelected(str.toString(), true);
		}
		else{
			presenter.resetMove();
		}
	}
	
	@Override
	public void onPreviewDrop(DragContext context) throws VetoDragException {
		if (thePanel == null) {
			throw new VetoDragException();
		}
		super.onPreviewDrop(context);
	}
}
