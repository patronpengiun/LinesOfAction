package org.linesOfAction.graphics;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface PieceImages extends ClientBundle {
	  @Source("images/white.png")
	  ImageResource white();
	  
	  @Source("images/black.png")
	  ImageResource black();
	  
	  @Source("images/empty.png")
	  ImageResource empty();
}
