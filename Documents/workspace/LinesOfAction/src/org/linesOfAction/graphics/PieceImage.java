package org.linesOfAction.graphics;

public final class PieceImage {
	enum PieceImageKind {
	    WHITE,
	    BLACK,
	    EMPTY,
	}
	
	public static class Factory {
	    public static PieceImage getWhite() {
	      return new PieceImage(PieceImageKind.WHITE);
	    }

	    public static PieceImage getBlack() {
	      return new PieceImage(PieceImageKind.BLACK);
	    }
	    
	    public static PieceImage getEmpty() {
	      return new PieceImage(PieceImageKind.EMPTY);
	    }
	}
	
	public final PieceImageKind kind;
	
	private PieceImage(PieceImageKind kind) {
		this.kind = kind;
	}

}
