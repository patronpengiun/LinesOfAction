package linesOfAction.graphics;


import com.google.gwt.resources.client.ImageResource;

public class PieceImageSupplier {
	private final PieceImages pieceImages;
	
	public PieceImageSupplier(PieceImages pieceImages) {
		this.pieceImages = pieceImages;
	}
	
	public ImageResource getResource(PieceImage pieceImage) {
	    switch (pieceImage.kind) {
	      case WHITE:
	        return pieceImages.white();
	      case BLACK:
	        return pieceImages.black();
	      case EMPTY:
	    	return pieceImages.empty();
	      default:
	        throw new RuntimeException("Forgot kind=" + pieceImage.kind);
	    }
	  }


}
