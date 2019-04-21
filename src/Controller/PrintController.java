package Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * Created by mrkis on 26-04-2017.
 */
public class PrintController {

    public static void printPanel(JPanel printPanel){
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setJobName(" Print Component ");

        printerJob.setPrintable ((pg, pf, pageNum) -> {
            if (pageNum > 0){
                return Printable.NO_SUCH_PAGE;
            }

            Graphics2D g2 = (Graphics2D) pg;
            g2.translate(pf.getImageableX(), pf.getImageableY());
            printPanel.paint(g2);
            return Printable.PAGE_EXISTS;
        });
        if (!printerJob.printDialog())
            return;

        try {
            printerJob.print();
        } catch (PrinterException ex) {
            // handle exception
        }
    }
}
