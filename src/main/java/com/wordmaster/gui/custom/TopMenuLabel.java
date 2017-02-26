package com.wordmaster.gui.custom;

import javax.swing.*;
import java.awt.*;

public class TopMenuLabel extends JLabel {
    public TopMenuLabel(String text) {
        super(text);
        this.setFont(new Font(this.getFont().getName(), Font.PLAIN, 20));
    }
}
