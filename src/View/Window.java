package View;

import Controller.UserInputControllers.CanvasMouseController;
import Controller.ActionListeners.MenuActionListener;
import Model.*;
import View.MenuElements.*;
import View.MenuElements.ZoomPanel.ZoomPanel;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * The main program window
 */
public class Window extends JFrame implements Observer {
    private final MenuPanel menupanel;
    private final Dimension dimensions = new Dimension(1200, 750);
    private final String LOGO_PATH = "resources/icon.png";
    private Canvas canvas;
    private CanvasMouseController controller;

    /**
     * Creates window with map and menupanel
     * @param model model to be represented
     * @throws IOException
     * @throws SAXException
     */
    public Window(Model model) throws IOException, SAXException {
        super("Closed Street Map");
        model.addObserver(this);

        JPanel jp = new JPanel();
        jp.setLayout(new OverlayLayout(jp));

        MapRuler mapRuler = new MapRuler(model, this);
        jp.add(mapRuler);

        ZoomPanel zoompanel = new ZoomPanel();
        zoompanel.setPreferredSize(new Dimension(75,60));
        jp.add(zoompanel);

        menupanel = new MenuPanel(model, this);
        menupanel.setPreferredSize(new Dimension(dimensions.width/5,dimensions.height));
        add(menupanel, BorderLayout.WEST);

        canvas = new Canvas(model);
        canvas.setPreferredSize(new Dimension(dimensions.width,dimensions.height));

        controller = new CanvasMouseController(canvas, model,menupanel);
        canvas.pan(-model.getMinlon(), -model.getMaxlat());
        canvas.zoom(dimensions.width / (model.getMaxlon() - model.getMinlon())+100);
        canvas.toggleAA();

        jp.add(canvas);

        zoompanel.getPlusButton().addActionListener(new MenuActionListener.PlusButtonActionListener(this));
        zoompanel.getMinusButton().addActionListener(new MenuActionListener.MinusButtonActionListener(this));

        jp.setComponentZOrder(zoompanel, 0);
        jp.setComponentZOrder(canvas,2);
        jp.setComponentZOrder(mapRuler, 1);

        add(jp,BorderLayout.CENTER);
        setPreferredSize(dimensions);
        setResizable(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        toFront();
        this.setIconImage(new AutoFileReader.ImageReader(LOGO_PATH,true).getImage());
        View.MenuBar menubar = new View.MenuBar(model, menupanel, this);
        add(menubar, BorderLayout.NORTH);
    }

    public CanvasMouseController getController(){
        return controller;
    }

    public void toggleRouteMode() {
        menupanel.toggleRouteMode();
    }

    @Override
    public void update(Observable o, Object arg) {}

    /**
     * Method used to zoom in and out
     * @param zoomValue how much the program should zoom
     */
    public void zoom(int zoomValue) {
        double factor = Math.pow(0.9, zoomValue);
        double dx = (dimensions.width-menupanel.getWidth())/2;
        double dy = dimensions.height/2;

        canvas.pan(-dx, -dy);
        canvas.zoom(factor);
        canvas.pan(dx, dy);
    }

    public void toggleAA() {
        canvas.toggleAA();
    }

    public static void terminate() {

    }
}
