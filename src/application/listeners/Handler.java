package application.listeners;

import application.UserInterface;

import java.awt.event.ActionListener;

/**
 * Abstract class to extend for any ActionListeners
 */
public abstract class Handler implements ActionListener {
    private final UserInterface UI; //The GUI that is using this instance

    /**
     * Constructor for the abstract class to set the UI property
     * @param ui the class containing the GUI that is using this ActionListener
     */
    public Handler(UserInterface ui) {
        this.UI = ui;
    }

    /**
     * Gets the class containing the GUI attached to the handler
     * @return the UserInterface that is using this handler
     */
    public UserInterface getUI() {
        return this.UI;
    }
}
