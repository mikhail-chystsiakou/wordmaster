package com.wordmaster.gui.custom;

import com.wordmaster.gui.i18n.Language;
import com.wordmaster.model.Player;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Helper class with auxiliary staff
 *
 * @version 1.0
 * @author Mike
 */
public class WordmasterUtils {
    /**
     * Shows default error alert
     *
     * @param frame owner of alert
     * @param message alert message
     * @param language language of alert
     */
    public static void showErrorAlert(JFrame frame, String message, Language language) {
        ResourceBundle resourceBundle = language.getResourceBundle();
        JOptionPane.showMessageDialog(frame, resourceBundle.getString(message),
                resourceBundle.getString("e_error"), JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Shows the game end alert
     *
     * @param frame owner of the alert
     * @param winners list of players that win
     * @param language language of alert
     */
    public static void showGameEndsAlert(JFrame frame,
                                         List<Player> winners, Language language) {
        ResourceBundle resourceBundle = language.getResourceBundle();
        StringBuilder gameEndsAlert = new StringBuilder();
        int i;
        if (winners.size() > 1) {
            gameEndsAlert.append(resourceBundle.getString("game_end_alert2"));
        } else {
            gameEndsAlert.append(resourceBundle.getString("game_end_alert1"));
        }
        for (i = 0; i < winners.size() - 1; i++) {
            gameEndsAlert.append(winners.get(i).getName());
            gameEndsAlert.append(", ");
        }
        gameEndsAlert.append(winners.get(i).getName());
        JOptionPane.showMessageDialog(frame, gameEndsAlert.toString());
    }

    /**
     * Shows alert to ask if player wish to save game replay
     *
     * @param frame owner of frame
     * @param language language of alert
     * @return true if player wish to save replay, false otherwise
     */
    public static boolean askSaveReplay(JFrame frame, Language language) {
        ResourceBundle resourceBundle = language.getResourceBundle();
        int n = JOptionPane.showConfirmDialog(
                frame,
                resourceBundle.getString("save_replay_question"),
                resourceBundle.getString("save_replay_title"),
                JOptionPane.YES_NO_OPTION);
        return n == 0;
    }
}
