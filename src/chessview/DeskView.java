package chessview;

import chessmodel.CheckerboardPosition;
import chessmodel.DeskModel;
import chessmodel.PositionWithPiece;
import chessmodel.piecemodel.BishopModel;
import chessmodel.piecemodel.KingModel;
import chessmodel.piecemodel.KnightModel;
import chessmodel.piecemodel.PawnModel;
import chessmodel.piecemodel.QueenModel;
import chessmodel.piecemodel.RookModel;
import chessview.pieceview.BishopView;
import chessview.pieceview.KingView;
import chessview.pieceview.KnightView;
import chessview.pieceview.PawnView;
import chessview.pieceview.PieceView;
import chessview.pieceview.PieceViewConstants;
import chessview.pieceview.QueenView;
import chessview.pieceview.RookView;
import gameplay.ClickOnDeskListener;
import gameplay.ClickOnPieceListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.util.List;

public class DeskView extends JPanel {
    private BufferedImage buffer;
    private PieceView currentPiece;
    private DeskModel deskModel;

    public DeskView(){
        super();
        setLayout(null);
        buffer = new BufferedImage(1500, 1500, BufferedImage.TYPE_INT_ARGB);
        deskModel = new DeskModel();
        drawDesk();
        setInitialState();
        addMouseListener(new ClickOnDeskListener(this));
        setPieceInDeskModel();
        setVisible(true);
    }

    public boolean isLelagMove(CheckerboardPosition newPosition){
        List<PositionWithPiece> allCandidate = deskModel.getAllCandidate();
        PositionWithPiece currentPosition;
        int n = allCandidate.size();
        for (int i = 0; i < n; i++) {
            currentPosition = allCandidate.get(i);
            if (currentPosition.equalsPosition(newPosition)){
                return true;
            }
        }
        return false;
    }

    public void movePiece(PieceView pieceView, CheckerboardPosition newPosition){
        pieceView.goToPosition(newPosition);
        deskModel.removePieceFromPosition(pieceView.getPieceModel());
        pieceView.getPieceModel().setPiecePosition(newPosition);
        pieceView.getPieceModel().setUsing(true);
        deskModel.addPieceOnPosition(currentPiece.getPieceModel());
        checkPawnToEnd();
    }
    private void checkPawnToEnd(){
        if (currentPiece==null) return;
        if (currentPiece.getPieceModel() instanceof PawnModel){
            PawnModel pawnPieceModel = (PawnModel)currentPiece.getPieceModel();
            if (pawnPieceModel.checkPositionForEnd()){
                String choosingPieceName = (String)JOptionPane.showInputDialog(this,
                        "Choose new piece",
                        "Choose pieces view",
                        JOptionPane.DEFAULT_OPTION,
                        null,
                        PieceViewConstants.PIECE_NAME,
                        PieceViewConstants.PIECE_NAME[0]);
                if (choosingPieceName == null){
                    choosingPieceName = PieceViewConstants.PIECE_NAME[0];
                }

                if (choosingPieceName.equals(PieceViewConstants.PIECE_NAME[0])){
                    currentPiece.setPieceModel(new QueenModel(currentPiece.getColor(),currentPiece.getCurrentPosition()));
                    currentPiece.setPiecePicture(currentPiece.getColor().equals("black") ?
                            PieceViewConstants.BLACK_QUEEN :
                            PieceViewConstants.WHITE_QUEEN);
                } else if (choosingPieceName.equals(PieceViewConstants.PIECE_NAME[1])){
                    currentPiece.setPieceModel(new RookModel(currentPiece.getColor(),currentPiece.getCurrentPosition()));
                    currentPiece.setPiecePicture(currentPiece.getColor().equals("black") ?
                            PieceViewConstants.BLACK_ROOK :
                            PieceViewConstants.WHITE_ROOK);
                } else if (choosingPieceName.equals(PieceViewConstants.PIECE_NAME[2])){
                    currentPiece.setPieceModel(new BishopModel(currentPiece.getColor(),currentPiece.getCurrentPosition()));
                    currentPiece.setPiecePicture(currentPiece.getColor().equals("black") ?
                            PieceViewConstants.BLACK_BISHOP :
                            PieceViewConstants.WHITE_BISHOP);
                } else if (choosingPieceName.equals(PieceViewConstants.PIECE_NAME[3])){
                    currentPiece.setPieceModel(new KnightModel(currentPiece.getColor(),currentPiece.getCurrentPosition()));
                    currentPiece.setPiecePicture(currentPiece.getColor().equals("black") ?
                            PieceViewConstants.BLACK_KNIGHT :
                            PieceViewConstants.WHITE_KNIGHT);
                }
                PositionWithPiece currentPosition = deskModel.getEqualElement(currentPiece.getCurrentPosition());
                currentPosition.setPiece(currentPiece.getPieceModel());
            }
        }
    }

    private void setPieceInDeskModel(){
        PieceView currentPiece;
        int numberOfPiece = getComponentCount();
        for (int i = 0; i < numberOfPiece; i++) {
            currentPiece = (PieceView)getComponent(i);
            if (currentPiece instanceof PieceView){
                deskModel.addPieceOnPosition(currentPiece.getPieceModel());
            }
        }
    }

    public String getWalkethPlayer(){
        return deskModel.getWalkethPlayer();
    }

    public void changePlayer(){
        deskModel.changePlayer();
        if (deskModel.checkForCheckmate()){
            deskModel.changePlayer();
            endGame();
            JOptionPane.showMessageDialog(this, "CHECKMATE. Win " + getWalkethPlayer() + " player.");
        }
    }
    private void endGame(){
        PieceView currentPiece;
        int numberOfPiece = getComponentCount();
        for (int i = 0; i < numberOfPiece; i++) {
            currentPiece = (PieceView)getComponent(i);
            if (currentPiece instanceof PieceView){
                MouseListener[] allMouseListeners = currentPiece.getMouseListeners();
                for (MouseListener currentMouseListener : allMouseListeners){
                    currentPiece.removeMouseListener(currentMouseListener);
                }
            }
        }
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.drawImage(buffer, null,null);
    }

    private void drawDesk(){
        Graphics2D painter = (Graphics2D)buffer.getGraphics();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i+j) % 2 ==0){
                    painter.setColor(Color.white);
                } else {
                    painter.setColor(Color.gray);
                }
                painter.fillRect(300+j*90, i * 90, 90, 90);
            }
        }
        painter.setColor(Color.gray);
        painter.drawRect(300, 0, 720, 720);
        Font font = new Font("Tahoma", Font.BOLD, 20);
        painter.setFont(font);
        painter.setColor(Color.black);
        for (int i = 0; i < 8; i++) {
            painter.drawString(Integer.toString(8-i), 270, i*90+55);
        }

        for (int i = 0; i < 8; i++) {
            painter.drawString("" + (char)('A'+i), 335+i*90, 740);
        }
        repaint();
    }

    private void setListenersOnPiece(){
        PieceView currentPiece;
        int numberOfPiece = getComponentCount();
        for (int i = 0; i < numberOfPiece; i++) {
            currentPiece = (PieceView)getComponent(i);
            if (currentPiece instanceof PieceView){
                currentPiece.addMouseListener(new ClickOnPieceListener(this, currentPiece));
            }
        }
    }

    public PieceView getCurrentPiece() {
        return currentPiece;
    }

    public void setCurrentPiece(PieceView newCurrentPiece) {
        if (newCurrentPiece == null){
            currentPiece.notChoose();
            currentPiece = newCurrentPiece;
            eraseAllCandidate();
            deskModel.setAllCandidate(null);
        } else {
            if (currentPiece != null){
                eraseAllCandidate();
                currentPiece.notChoose();
            }
            currentPiece = newCurrentPiece;
            currentPiece.choose();
            deskModel.createCandidateList(currentPiece.getPieceModel());
            drawAllCandidate();
        }
    }

    private void drawAllCandidate(){
        List<PositionWithPiece> allCandidate = deskModel.getAllCandidate();
        PositionWithPiece currentPosition;
        int n = allCandidate.size();
        for (int i = 0; i < n; i++) {
            currentPosition = allCandidate.get(i);
            if (currentPosition.getPiece() == null){
                leadRound(Color.green, currentPosition);
            }
        }
        for (int i = 0; i < n; i++) {
            currentPosition = allCandidate.get(i);
            if (currentPosition.getPiece() != null){
                if (currentPosition.getPiece().getColor().equals(getWalkethPlayer())){
                    leadRound(Color.green, currentPosition);
                } else {
                    leadRound(Color.red, currentPosition);
                }
            }
        }
    }

    private void eraseAllCandidate(){
        List<PositionWithPiece> allCandidate = deskModel.getAllCandidate();
        PositionWithPiece currentPosition;
        int n = allCandidate.size();
        for (int i = 0; i < n; i++) {
            currentPosition = allCandidate.get(i);
            if (currentPosition.getColor().equals("white")){
                leadRound(Color.white, currentPosition);
            } else {
                leadRound(Color.gray, currentPosition);
            }
        }
    }

    private void leadRound(Color currentColor, PositionWithPiece currentPosition){
        Graphics2D painter = (Graphics2D)buffer.getGraphics();
        painter.setColor(currentColor);
        painter.setStroke(new BasicStroke(2.0f));
        painter.drawRect(300 + currentPosition.getColumn() * 90, currentPosition.getRow() * 90, 90, 90);
        repaint();
    }

    public void setInitialState(){
        setInitialPositionPawns();
        setInitialPositionRook();
        setInitialPositionKnight();
        setInitialPositionBishop();
        setInitialPositionQueen();
        setInitialPositionKing();
        setListenersOnPiece();
        currentPiece = null;
    }

    private void setInitialPositionPawns(){
        for (int i = 0; i < 8; i++) {
            add(new PawnView("black", new CheckerboardPosition(1, i)));
            add(new PawnView("white", new CheckerboardPosition(6, i)));
        }
    }

    private void setInitialPositionRook(){
        add(new RookView("black",new CheckerboardPosition(0,0)));
        add(new RookView("black",new CheckerboardPosition(0,7)));
        add(new RookView("white",new CheckerboardPosition(7,0)));
        add(new RookView("white",new CheckerboardPosition(7,7)));
    }

    private void setInitialPositionKnight(){
        add(new KnightView("black",new CheckerboardPosition(0, 1)));
        add(new KnightView("black",new CheckerboardPosition(0,6)));
        add(new KnightView("white",new CheckerboardPosition(7,1)));
        add(new KnightView("white",new CheckerboardPosition(7,6)));
    }

    private void setInitialPositionBishop(){
        add(new BishopView("black",new CheckerboardPosition(0, 2)));
        add(new BishopView("black",new CheckerboardPosition(0,5)));
        add(new BishopView("white",new CheckerboardPosition(7,2)));
        add(new BishopView("white",new CheckerboardPosition(7,5)));
    }

    private void setInitialPositionQueen(){
        add(new QueenView("black", new CheckerboardPosition(0, 3)));
        add(new QueenView("white",new CheckerboardPosition(7,3)));
    }

    private void setInitialPositionKing(){
        add(new KingView("black", new CheckerboardPosition(0, 4)));
        add(new KingView("white", new CheckerboardPosition(7, 4)));
    }
}
