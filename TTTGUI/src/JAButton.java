import javax.swing.*;

public class JAButton extends JButton {
    public enum Action {
        ChangeGridSpace, Restart, ChangeGameConfig, ChangeGameMode, Quit, ResetPlayerStats
    }

    private Action actionType;

    public JAButton(String text, Action action) {
        super(text);
        this.actionType = action;
    }

    public Action getActionType() {
        return this.actionType;
    }
}
