package org.linesOfAction.graphics;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.linesOfAction.client.GamePresenter;

public class GameDragController extends PickupDragController {
	
	private final GamePresenter presenter;
	private final GameGraphics graphics;

	public GameDragController (AbsolutePanel panel, boolean flag, GamePresenter p, GameGraphics g){
		super(panel,flag);
		presenter = p;
		graphics = g;
	}
	
	@Override 
	public void dragStart() {
		super.dragStart();
		saveSelectedWidgetsLocationAndStyle();
		Image img = (Image)context.draggable;
		int i = img.getAbsoluteTop()/42;
		int j = img.getAbsoluteLeft()/42;
		StringBuilder str = new StringBuilder(Character.toString((char)('1'+i)));
		str.append((char)('A'+j));
		
		if (graphics.clickCheck(i, j)){
			presenter.positionSelected(str.toString(), true);
			graphics.dragValid = true;
		}
		else{
			graphics.dragValid = false;
		}
	}
}
