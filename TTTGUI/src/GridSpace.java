import java.awt.*;

public class GridSpace extends JAButton {
    private final int identifier;
    private State currentState;
    public enum State { X, O, EMPTY }

    private static final Font buttonFont = new Font("Arial", Font.BOLD, 45);

    public GridSpace(String text, int identifier) {
        super(text, JAButton.Action.ChangeGridSpace);
        this.identifier = identifier;
        this.currentState = State.EMPTY;
        this.setFont(buttonFont);
        this.revalidate();
    }

    public GridSpace(String text, int identifier, State state) {
        super(text, JAButton.Action.ChangeGridSpace);
        this.identifier = identifier;
        this.currentState = state;
        this.setFont(buttonFont);
        this.revalidate();
    }

    public int getIdentifier() { return this.identifier; }
    public State getCurrentState() { return this.currentState; }
    public void setCurrentState(State state) { this.currentState = state; }

    @Override
    public JAButton.Action getActionType() {
        return super.getActionType();
    }
}
