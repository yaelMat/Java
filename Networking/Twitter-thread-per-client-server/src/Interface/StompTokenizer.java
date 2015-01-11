package Interface;
import java.io.BufferedReader;
import java.io.IOException;

import StompFrame.StompFrame;

public interface StompTokenizer {
   public StompFrame getFrame(BufferedReader br) throws IOException, InterruptedException;
}