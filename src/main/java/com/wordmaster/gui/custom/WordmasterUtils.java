package com.wordmaster.gui.custom;

import com.wordmaster.gui.i18n.Language;
import com.wordmaster.gui.page.GamePage;
import com.wordmaster.model.Player;

import javax.swing.*;
import java.util.List;
import java.util.ResourceBundle;

public class WordmasterUtils {
    public static void showErrorAlert(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.WARNING_MESSAGE);
    }

    public static void showGameEndsAlert(JFrame frame,
                                         List<Player> winners, Language language) {
        ResourceBundle resourceBundle = language.getResourceBundle();
        StringBuilder gameEndsAlert = new StringBuilder();
        int i;
        gameEndsAlert.append(resourceBundle.getString("game_end_alert"));
        for (i = 0; i < winners.size() - 1; i++) {
            gameEndsAlert.append(winners.get(i).getName());
            gameEndsAlert.append(", ");
        }
        gameEndsAlert.append(winners.get(i).getName());
        JOptionPane.showMessageDialog(frame, gameEndsAlert.toString());
    }

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
