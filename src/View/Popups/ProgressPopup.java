package View.Popups;

import Controller.ActionListeners.WindowCloseActionListener;
import Model.Model;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Popup t show the progress of a map load
 */
public class ProgressPopup extends IOPopup {
    private JLabel timeLeftLabel;

    public ProgressPopup() {
        timeLeftLabel = new JLabel("", SwingConstants.CENTER);
        timeLeftLabel.setPreferredSize(new Dimension(300,100));
        panel.add(timeLeftLabel, BorderLayout.SOUTH);
        addWindowListener(new WindowCloseActionListener());
        publish();
    }

    /**
     * Update the percentage shown to the user
     * @param p The percentage
     */
    public void updatePercentage(double p) {
        progressBar.setValue((int)p);
        if (p == 0) {
            progressBar.setString(Model.getString("loading-to-buffer"));
        } else {
            DecimalFormat df = new DecimalFormat("#.##");
            progressBar.setString(Model.getString("parsing")+ df.format(p) + "%");
        }
    }

    /**
     * Update the estimated time left shown to the user
     * @param secondsLeft The number of seconds left
     */
    public void updateTimeLeft(long secondsLeft) {
        timeLeftLabel.setText(Model.getString("estimated-time")+(secondsLeft / 60)+":"+String.format("%02d", secondsLeft % 60)+Model.getString("minutes"));
    }
}
