package Model.MapDrawStyle;

import java.awt.*;

public enum LineType {
    DEFAULT(new StrokeStyle(BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, null)),
    DASHED1(new StrokeStyle( BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, new float[] {0.000008f})),
    DASHED2(new StrokeStyle(BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, new float[] {0.00002f})),
    DASHED3(new StrokeStyle(BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, new float[] {0.000008f})),
    DASHED4(new StrokeStyle(BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, new float[] {0.00003f})),
    ROUND(new StrokeStyle(BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, null));

    public final StrokeStyle strokeStyle;
    LineType(StrokeStyle strokeStyle) {this.strokeStyle = strokeStyle;}
}
