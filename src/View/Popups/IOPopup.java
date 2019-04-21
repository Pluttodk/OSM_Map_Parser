package View.Popups;

import Model.AutoFileReader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.TimerTask;

/**
 * Abstract class that represents an IO dialog with a gif and a progressbar
 */
public abstract class IOPopup extends JDialog {
    private static final String LOADING_GIF_PATH = "resources/loading.gif";
    protected JPanel panel = new JPanel();
    protected JProgressBar progressBar;

    /**
     * Constructor adding components and sets repaint schedule
     */
    IOPopup() {
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(300,25));
        progressBar.setVisible(true);
        progressBar.setStringPainted(true);

        ImageIcon loadingGif = null;
        try {
            loadingGif = new AutoFileReader.ImageReader(LOADING_GIF_PATH, true).getImageIcon();
        } catch (IOException ignored) {
            //Do nothing
        }
        JLabel gif = new JLabel(loadingGif, SwingConstants.CENTER);
        panel.add(gif, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);

        add(panel);

        setAlwaysOnTop(true);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setPreferredSize(new Dimension(300, 500));

        //Setting repaint schedule
        new java.util.Timer().scheduleAtFixedRate(
            new TimerTask() {
                @Override
                public void run() {
                    panel.paintImmediately(0, 0, 300, 500);
                }
            }, 0, 10
        );
    }

    /**
     * Closes dialog
     */
    public void close() {
        setVisible(false);
        dispose();
    }

    /**
     * Opens dialog
     */
    void publish() {
        pack();
        centerWindow();
        setVisible(true);
    }

    /**
     * Centers dialog
     */
    private void centerWindow() {
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2;
        setLocation(x,y);
    }


}
