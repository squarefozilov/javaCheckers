import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

/** Frame for the game Checkers. The frame contains the game
  board and necessary buttons and information fields. */
public class Checkers extends JFrame {
    // Components that go in the frame
    CheckerSquare[][] board; // The 8 x 8 game board
    JLabel redsLabel= new JLabel(  "  red pieces: 12");
    JLabel blacksLabel= new JLabel("  black pieces: 12");
    JLabel whoPlays= new JLabel("  Red to play ");
    JLabel nextTask= new JLabel("  Invalid destination. Choose  ");
    JLabel nextTask1= new JLabel(" an empty square to move to  ");
    Box help;  //Will contain the help message
    
    // Titles for buttons
    private final String[] buttons= {"New game", "Quit", "Help"};
    
    private Box mainBox= new Box(BoxLayout.X_AXIS);  // boardBox and infoBox
    private Box boardBox= new Box(BoxLayout.Y_AXIS); // 8 rows of checker-board boxes
    private Box infoBox= new Box(BoxLayout.Y_AXIS);  // buttons and other info
    
    private MouseEvents mouseEvent= new MouseEvents(); //object that processes mouse clicks
    
    // Number of red pieces and black pieces on the board
    private int redCount= 12;   //Start with 12 red pieces
    private int blackCount= 12; //Start wih 12 black pieces
    
    /** clickflag describes the clicks made by the user thus far in makina move:
      = 0: No piece has been chosen (clicked on). 
      For all squares, field toBeMoved is false.
      = 1: A click, on a piece of color colorToPlay, has been made.
      oldSq is the square of the piece to be moved, and it is
      highlighted (i.e. oldSq.toBeMoved is true). For all other
      squares, field toBeMoved is false. */
    private int clickflag= 0;
    private CheckerSquare oldSq;
    
    // These variables have values CheckerSquare.RED or CheckerSquare.BLACK
    private int colorToPlay; // player (i.e. color) to place next
    private int otherColor;  // the other color
    
    /** Show the GUI  */
    public static void main(String[] pars) {
        Checkers gui= new Checkers();
    }
    
    /** Constructor: An 8 x 8 checkerboard, with three buttons and some
      pieces of information */
    public Checkers() {
        super("Checkers");
        setFont(new Font("Dialog", Font.PLAIN, 10));
        
        addComponents();

        setFontAndSizes();
        
        getContentPane().add(mainBox, BorderLayout.CENTER);
        setLocation(150,50);
        newGame();
        setResizable(false);
        pack();
        setVisible(true);
    }
    
    /** Set the font and font size for all field components. */
    public void setFontAndSizes() {
        redsLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        blacksLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        whoPlays.setFont(new Font("Arial", Font.PLAIN, 22));
        nextTask.setFont(new Font("Arial", Font.PLAIN, 22));
        nextTask1.setFont(new Font("Arial", Font.PLAIN, 22));
    }
    
    /** Add the components of the GUI to mainBox */
    private void addComponents() {
        // (0) Create and place the 8*8 squares in the board and in boardBox
        //     and register mouseEvent as a listener for them
        board= new CheckerSquare[8][8];
        
        //invariant: rows 0..i-1 have been set up
        for (int i= 0; i != 8; i= i+1) {
            // Set up row i of the board.
            Box row= new Box(BoxLayout.X_AXIS);
            
            //inv: board[i][0..j-1] have been set up
            for (int j= 0; j != 8; j= j+1) {
                board[i][j]= new CheckerSquare(i,j);
                row.add(board[i][j]);
                board[i][j].addMouseListener(mouseEvent);
            }
            boardBox.add(row);
        }
        
        // (1) Add boardBox to the main box
        mainBox.add(boardBox);
        
        // (2) Create the buttons given by String buttons
        //     and register this object as a listener for them
        //invariant: buttons for buttons[0..j-1] have been set up
        for (int j= 0; j != buttons.length; j= j+1) {
            JButton jb= new JButton(buttons[j]);
            infoBox.add(jb);
            //jb.addActionListener(this);
            jb.addActionListener(e -> clickButton(e));
        }
        
        // (3) Place labels for the numbers of white and black pieces
        infoBox.add(redsLabel);
        infoBox.add(blacksLabel);
        infoBox.add(Box.createVerticalStrut(20)); //give vertical space
        infoBox.add(whoPlays);
        infoBox.add(nextTask);
        infoBox.add(nextTask1);
        // The following component placed at the bottom expands to fill the
        // space so that the column appears at the top and not vertically centered
        infoBox.add(Box.createGlue());
        
        // (4) Add the infoBox to the main box
        mainBox.add(infoBox);
    }
    
    
    /** Process a click of a button */
    public void clickButton(ActionEvent ae) {
        Object source= ae.getSource();
        if (!(source instanceof JButton))
            return;
        JButton jb= (JButton) source;
        String text= jb.getText();
        if (text.equals(buttons[0])) {
            // Handle press of "New game" and return
            newGame();
            return;
        }
        if (text.equals(buttons[1])) {
            // Handle press of "Quit" and return
            dispose();
            return;
        }
        if (text.equals(buttons[2])) {
            // Handle press of "Help" and return
            if (help != null) {
                // Help field already exists, so remove it and return
                Container c= getContentPane();
                c.remove(help);
                c.validate();
                help= null;
                pack();
                return;
            }
            help= new Box(BoxLayout.Y_AXIS);
            help.add(new JLabel(" This is the game of checkers."));
            help.add(new JLabel(" We assume you know how to play."));
            help.add(new JLabel(" A king is shown with a yellow \"K\" it."));
            help.add(new JLabel(" To move a piece:"));
            help.add(new JLabel("    1. Click the piece;"));
            help.add(new JLabel("    2. Click the square you wish to move it to."));
            help.add(new JLabel(" If you decide to move a different piece, click it."));
            help.add(new JLabel(""));
            help.add(new JLabel(" One change in the game: You can't jump two pieces at once."));
            help.add(new JLabel(" Try changing the program to do this."));
            help.add(new JLabel(""));
            
            getContentPane().add(help, BorderLayout.SOUTH);
            pack();
            return;
        }
    }
    
    /** Set the board up for a new game */
    public void newGame() {
        // Pick up the pieces from all the squares
        for (int i= 0; i != 8; i= i+1)
            for (int j= 0; j != 8; j= j+1)
            board[i][j].pickUpPiece();
        
        clickflag= 0;
        // Place red and black pieces in the initial configuration
        for (int j= 0; j <= 6; j= j+2) {
            board[0][j+1].placePiece(CheckerSquare.RED);  // row 0
            board[1][j]  .placePiece(CheckerSquare.RED);  // row 1
            board[2][j+1].placePiece(CheckerSquare.RED);  // row 2
            board[5][j]  .placePiece(CheckerSquare.BLACK);// row 5
            board[6][j+1].placePiece(CheckerSquare.BLACK);// row 6
            board[7][j]  .placePiece(CheckerSquare.BLACK);// row 7
        }
        
        // Set the number of red and number of black pieces to 12
        redCount= 12;
        redsLabel.setText("  red pieces: " + redCount + " ");
        blackCount= 12;
        blacksLabel.setText("  black pieces: " + blackCount + " ");
        colorToPlay= CheckerSquare.BLACK;
        otherColor= CheckerSquare.RED;
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        fixDisplay("  Select a piece to move ", "");
        changeTurn();
    }
    
    /** = the sign of v (1 if v>0; 0 if v=0; -1 if v<0) */
    private int sign(int v) {
        if (v > 0) return 1;
        if (v < 0) return -1;
        return v;
    }
    
    /** Attempt to move piece from square oldSq to square newSq
      If not possible, return false.
      If possible, make the move and return true */
    private boolean validMove(CheckerSquare oldSq, CheckerSquare newSq) {
        if (newSq.isRed()  ||  newSq.contents() != CheckerSquare.EMPTY)
            return false;
        
        // If this is a valid non-jump move, then make the move
        // and return true. Making the move requires: (0) picking up
        // the piece, (1) putting it down on the new square, and
        // (2) promoting it to a new king if necessary.
        if (isValidNonjump(oldSq,newSq)) {
            int piece= oldSq.contents();
            oldSq.pickUpPiece();
            newSq.placePiece(piece);
            promoteToKing(newSq);
            return true;
        }
        
        // If this is a valid jump move, then make the move
        // and return true. Making the move requires: (0) picking up
        // the piece, (1) putting it down on the new square, (1) picking
        // up the piece jumped (and subtracting 1 from the number of
        // pieces of that color), and (2) promoting the new piece to
        // a king if necessary 
        if (isValidjump(oldSq,newSq)) {
            int piece= oldSq.contents();
            oldSq.pickUpPiece();
            newSq.placePiece(piece);
            // Pick up the piece that was jumped over
            int x= (oldSq.col+newSq.col)/2; //col no of jumped piece
            int y= (oldSq.row+newSq.row)/2; //row no of jumped piece
            board[y][x].pickUpPiece();
            if (otherColor==CheckerSquare.RED) {
                redCount= redCount-1;
                redsLabel.setText("  red pieces: " + redCount + " ");
            }
            else {
                blackCount= blackCount-1;
                blacksLabel.setText("  black pieces: " + blackCount + " ");
            }
            promoteToKing(newSq);
            return true;
        }
        
        // The move is invalid, so return false
        return false;
    }
    
    /** Return "the move from oldSq to newSq is a valid non-jump
      move for player colorToPlay".
      Precondition: oldSq contains a piece of colorToPlay and newSq
      is an empty black square */
    private boolean isValidNonjump(CheckerSquare oldSq, CheckerSquare newSq) {
        int dx= newSq.col - oldSq.col;
        int dy= newSq.row - oldSq.row;
        
        // Return false if the move is not a move to an adjacent row and column,
        if (Math.abs(dx) != 1) return false;
        if (Math.abs(dy) != 1) return false;
        
        // Return true if this is a King (which can go in any direction)
        if (oldSq.isKing()) return true;
        
        // The piece is not a king. Return value of "the piece moves
        // forward (and not backward)"
        return (oldSq.containsRed()  &&  dy > 0) ||
            (oldSq.containsBlack()  &&  dy < 0);
    }
    
    /** Return "the move from oldSq to newSq is a valid jump
      move for player colorToPlay".
      Precondition: oldSq contains a piece of colorToPlay and newSq is an
      empty black square */
    private boolean isValidjump(CheckerSquare oldSq,
                                CheckerSquare newSq) {
        int dx= newSq.col - oldSq.col;
        int dy= newSq.row - oldSq.row;
        
        // Return false if the move is not a move of 2 columns
        // and two rows
        if (Math.abs(dx) != 2) return false;
        if (Math.abs(dy) != 2) return false;
        
        // Set s to the square between oldSq and newSq --remember
        // that the rows and cols differ by an absolute value of 2
        CheckerSquare s=
            board[oldSq.row+sign(dy)][oldSq.col + sign(dx)];
        
        // Return false if there is not a piece of the other color
        // between oldSq and newSq.
        if  (!s.isSameColor(otherColor))
            return false;
        
        // Return true if this is a King (which can go in any direction)
        if (oldSq.isKing()) return true;
        
        // The piece is not a king. Return value of "the piece moves
        // forward (and not backward)"
        return (oldSq.containsRed()  &&  dy > 0) ||
            (oldSq.containsBlack()  &&  dy < 0);
    }
    
    /** Precondition: clickflag=1.
      If the pieces on oldSq and newSq have the same color,
      then switch to moving the piece on newSq. Otherwise, attempt to
      move the piece from oldSq to square newSq. If not possible, give
      error message on the GUI; if possible, do it and switch players. */
    private void processClickTwo(CheckerSquare newSq) {
        enableBoard(false);
        if (newSq.isSameColor(colorToPlay)) {
            // Change the piece to move and return
            oldSq.setToBeMoved(false);
            oldSq= newSq;
            oldSq.setToBeMoved(true);
            fixDisplay("  another square ", "");
            return;
        }
        
        if (newSq.contents() != CheckerSquare.EMPTY ) {
            // newSq is not empty. Display error message and return
            fixDisplay("  Invalid destination. Choose ", "  another square ");
            return;
        }
        
        if (validMove(oldSq, newSq)) {
            // Move the piece from olddSq to newSq and return
            oldSq.setToBeMoved(false);
            clickflag= 0;
            fixDisplay("  Select a piece to move", "");
            changeTurn();
            return;                
        }
        
        // Move was not handled. Tell user to retry the move
        fixDisplay("  Invalid destination. Choose ", "  another square ");
    }
    
    /** Precondition: clickflag = 0.
      Process a click on square sq --the first one in making a move.
      If the click is on an empty square or on a piece of wrong color,
      give a message and ask user to try again.*/
    private void processSquareClick(CheckerSquare sq) {
        enableBoard(false);
        if (sq.contents() == CheckerSquare.EMPTY ||
               !sq.isSameColor(colorToPlay)) {
            fixDisplay("  Invalid choice. Choose ",
                "  a " + (colorToPlay == CheckerSquare.RED ? "red" : "black") + " piece to move ");
            return;
        }
        oldSq= sq;
        sq.setToBeMoved(true);
        clickflag= 1;
        fixDisplay("  Select destination ", "");
    }
    
    /** Set two lines of display messages to s1 and s2 and enable the board. */
    private void fixDisplay(String s1, String s2) {
        nextTask.setText(s1);
        nextTask1.setText(s2);
        enableBoard(true);
    }
    
    /** Set the enabled property of all board squares to en. */
    private void enableBoard(boolean en) {
        for (int i= 0; i != 8; i= i+1)
            for (int j= 0; j != 8; j= j+1)
            board[i][j].setEnabled(en);
    }
    
    /** If the piece in square sq is not a king and is in the
      last row for its color, change it to a King. Changing it
      to a king should be done by picking the piece up and then
      placing a king on the square. */
    private void promoteToKing(CheckerSquare sq) {
        if ((sq.row == 7) && sq.contents() == CheckerSquare.RED) {
            sq.pickUpPiece();
            sq.placePiece(CheckerSquare.REDKING);
            return;
        }
        if ((sq.row == 0) && sq.contents() == CheckerSquare.BLACK) {
            sq.pickUpPiece();
            sq.placePiece(CheckerSquare.BLACKKING);
        } 
    }
    
    /** Switch player (i.e. switch colorToPlay and otherColor) and
      set clickflag for a new move. */
    private void changeTurn() {
        int temp= colorToPlay;
        colorToPlay= otherColor;
        otherColor= temp;
        if (colorToPlay == CheckerSquare.RED) {
            whoPlays.setText("  Red to play ");
            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }
        else {
            whoPlays.setText("  Black to play ");
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        clickflag= 0;
    }
    
    /** Class contains a method that responds to a mouse click in
      a CheckerSquare */
    class MouseEvents extends MouseInputAdapter {
        /** If e is a click in a CheckerSquare, process it, by calling
           either processSquareClick or processClickTwo. */
        public void mouseClicked(MouseEvent e) {
            Object ob= e.getSource();
            if (ob instanceof CheckerSquare  &&  clickflag == 0) {
                processSquareClick((CheckerSquare)ob);
                return;
            }
            if (ob instanceof CheckerSquare  &&  clickflag == 1) {
                processClickTwo((CheckerSquare)ob);
                return;
            }
            
        }
    }
}
