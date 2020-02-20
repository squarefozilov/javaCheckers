import java.awt.*;
import javax.swing.*;
            
/** A square of the game Checkers. */
public class CheckerSquare extends Canvas {
    public static final int EMPTY= 0;  // Constants used to indicate
    public static final int RED= 1;    // what is on a square.
    public static final int BLACK= 2;  // Meaning: obvious
    public static final int REDKING= 3;
    public static final int BLACKKING= 4;
    
    /** Colors used for the background of red and black squares.
      Red (black) are not used because they are used for the pieces. */
    public static Color REDSQUARE= Color.pink;
    public static Color BLACKSQUARE= Color.gray;
    
    Rectangle b;   // Containing rectangle of this square
    int row;   // row number of this square on board
    int col;   // column number of this square on board
    
    /**  EMPTY: square is empty;
      RED (or REDKING) means red piece (red king) is on square;
      BLACK (or BLACKKING) means black piece (black king) is on square. */
    private int fill= EMPTY;
    
    // = "This square contains the piece to be moved"
    private boolean toBeMoved= false;
    
    /** Constructor: An initially empty square that belongs in
        row r, column c of the board. */
    public CheckerSquare(int r, int c) {
        super();
        row= r;
        col= c;
        if ((c+r)%2 == 1)
            setBackground(BLACKSQUARE);
        else
            setBackground(REDSQUARE);
        Dimension d= new Dimension(42, 42);
        setMaximumSize(d);
        setMinimumSize(d);
        setSize(d);
        b= getBounds();
        repaint();
    }
    
    /** Paint the square (with its piece, if any), using Graphics g. */
    public void paint (Graphics g) {
        // A checker is drawn as two circles. The first one is offset and
        // smaller, so that when the second is drawn, it covers most of the
        // first except for what appears as a side of the piece.
        // A King has a yellow "K" on it.
        // If toBeMoved is true, highlight this piece by making it
        // a radius of ew=eh=5 bigger than the others
        // 
        int ew= 0;
        int eh= 0;
        if (toBeMoved) {
            ew= 5;
            eh= 5;
        }
        
        if (fill == EMPTY) {
            return;
        }
        
        Color save= g.getColor(); // Save the color, to be reset at end
        
        // Draw an oval that will provide the "side" of a piece
        if (containsRed())  g.setColor(Color.magenta.darker());
        else     g.setColor(Color.darkGray.brighter());
        g.fillOval(b.x+b.width/4-ew, b.y+b.width/4-ew,
                   b.width/2+ew+ew, b.height/2+ew+ew);
        
        // Draw an oval that provides the "top" of the piece
        if (containsRed())
            g.setColor(Color.red);
        else 
            g.setColor(Color.black);
        g.fillOval(b.x+b.width/5-ew, b.y+b.width/5-ew,
                   b.width/2+ew+ew, b.height/2+ew+ew);
        
        // If the piece is a king, put a yellow K on it
        if (fill==REDKING || fill==BLACKKING) {
            g.setColor(Color.yellow);
            g.drawLine(b.x+3*b.width/8, b.y+b.height/3,
                       b.x+3*b.width/8, b.y+b.height/2);
            
            g.drawLine(b.x+3*b.width/8 + b.width/10, b.y+b.height/3,
                       b.x+3*b.width/8, b.y+5*b.height/12);
            
            g.drawLine(b.x+3*b.width/8, b.y+5*b.height/12,
                       b.x+3*b.width/8+b.width/10, b.y+b.height/2);
        }
        g.setColor(save);
    }
    
    /** Set the indication that this piece contains the piece to
      be moved (true if this piece contains
      the piece to be moved, false if not). */
    public void setToBeMoved(boolean b) {
        toBeMoved= b;
        repaint();
    }
    
    /** = the contents of the square --EMPTY, RED, REDKING,
      BLACK, or BLACKKING. */
    public int contents() {
        return fill;
    }
    
    /** = "The square contains a king". */
    public boolean isKing() {
        return fill == REDKING  ||  fill == BLACKKING;
    }
    
    /** Place piece p (RED, REDKING, BLACK, or BLACKKING) in
      this square (no action if square already filled). */
    public void placePiece(int p) {
        if (fill != EMPTY)
            return;
        fill= p;
        repaint();
    }
    
    /** = "This square is red". */
    public boolean isRed() {
        return (row+col) % 2 == 0;
    }
    
    /** Pick up the piece (no action if square is empty). */
    public void pickUpPiece() {
        if (fill == EMPTY)
            return;
        fill= EMPTY;
        repaint();
    }
    
    /** = "piece on square is red". */
    public boolean containsRed() {
        return fill == RED  ||  fill == REDKING;
    }
    
    /** "piece on square is black". */
    public boolean containsBlack() {
        return fill == BLACK  ||  fill == BLACKKING;
    }
    
    /** = "square contains piece that is same color as x"
      Precondition: x one of RED, REDKING, BLACK, BLACKKING. */
    public boolean isSameColor(int x) {
        if (fill == EMPTY) return false;
        if ((x == RED || x == REDKING) && containsRed())
            return true;
        return (x == BLACK || x == BLACKKING) && containsBlack();
    }
    
    /** = description of the square. */
    public String toString() {
        String s= "Square[" + row + "," + col + "]:";
        if (fill== EMPTY) s= s + "empty";
        if (fill== RED) s= s + "red";
        if (fill== BLACK) s= s + "black";
        if (fill== REDKING) s= s + "red king";
        if (fill== BLACKKING) s= s + "black king";
        return s;
    }
}




