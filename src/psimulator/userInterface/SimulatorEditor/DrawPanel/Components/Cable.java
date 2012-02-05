package psimulator.userInterface.SimulatorEditor.DrawPanel.Components;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import psimulator.AbstractNetwork.HwTypeEnum;
import psimulator.userInterface.SimulatorEditor.DrawPanel.ZoomManager;

/**
 *
 * @author Martin
 */
public class Cable extends AbstractComponent {

    private HwTypeEnum hwType;
    private ZoomManager zoomManager;
    private AbstractHwComponent component1;
    private AbstractHwComponent component2;
    private EthInterface eth1;
    private EthInterface eth2;
    private int delay;
    private Line2D line = new Line2D.Float();
    private Stroke stroke = new BasicStroke(3.5f);
    int x1, y1, x2, y2;

    public Cable(HwTypeEnum hwType, AbstractHwComponent component1, AbstractHwComponent component2, EthInterface eth1, EthInterface eth2, ZoomManager zoomManager) {
        this.hwType = hwType;
        this.component1 = component1;
        this.component2 = component2;
        this.eth1 = eth1;
        this.eth2 = eth2;
        this.zoomManager = zoomManager;

        // set delay according to type
        switch (hwType) {
            case CABLE_ETHERNET:
                delay = 10;
                break;
            case CABLE_OPTIC:
            default:
                delay = 2;
                break;
        }
    }

    public AbstractHwComponent getComponent1() {
        return component1;
    }

    public AbstractHwComponent getComponent2() {
        return component2;
    }

    public EthInterface getEth1() {
        return eth1;
    }

    public EthInterface getEth2() {
        return eth2;
    }

    public void paintComponent(Graphics g, int x1, int y1, int x2, int y2) {
        Graphics2D g2 = (Graphics2D) g;

        stroke = new BasicStroke(zoomManager.getStrokeWidth());

        line.setLine(x1, y1, x2, y2);

        Color tmpC = g2.getColor();
        Stroke tmpS = g2.getStroke();

        if (isMarked()) {
            g2.setColor(Color.blue);
            g2.setStroke(stroke);
            g2.drawLine(x1, y1, x2, y2);
        } else {
            // set cable color
            switch (hwType) {
                case CABLE_ETHERNET:
                    g2.setColor(Color.black);
                    break;
                case CABLE_OPTIC:
                default:
                    g2.setColor(Color.gray);
                    break;
            }

            //g2.setColor(Color.black);
            g2.setStroke(stroke);
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setColor(tmpC);
        g2.setStroke(tmpS);
    }

    @Override
    public int getWidth() {
        //return Math.abs(getX1()-getX2());
        //return LINE_WIDTH;
        return (int) zoomManager.getStrokeWidth();
    }

    @Override
    public int getHeight() {
        //return Math.abs(getY1()-getY2());
        //return LINE_WIDTH;
        return (int) zoomManager.getStrokeWidth();
    }

    @Override
    public int getX() {
        if (getX1() <= getX2()) {
            return getX1();
        }
        return getX2();
    }

    @Override
    public int getY() {
        if (getY1() <= getY2()) {
            return getY1();
        }
        return getY2();
    }

    @Override
    public boolean intersects(Point p) {
        Rectangle r = new Rectangle(p);
        return intersects(r);
    }

    @Override
    public boolean intersects(Rectangle r) {
        return line.intersects(r);
    }

    public int getX1() {
        return component1.getCenterLocation().x;
    }

    public int getY1() {
        return component1.getCenterLocation().y;
    }

    public Point2D getP1() {
        return component1.getCenterLocation();
    }

    public int getX2() {
        return component2.getCenterLocation().x;
    }

    public int getY2() {
        return component2.getCenterLocation().y;
    }

    public Point2D getP2() {
        return component2.getCenterLocation();
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public HwTypeEnum getHwType() {
        return hwType;
    }
}
