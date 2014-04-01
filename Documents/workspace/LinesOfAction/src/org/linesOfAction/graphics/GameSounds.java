package org.linesOfAction.graphics;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;

public interface GameSounds extends ClientBundle {

        @Source("org/linesOfAction/graphics/audio/pieceDown.mp3")
        DataResource pieceDownMp3();

        @Source("org/linesOfAction/graphics/audio/pieceDown.mp3")
        DataResource pieceDownWav();
        
}

