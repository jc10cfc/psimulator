package psimulator.userInterface.SimulatorEditor.DrawPanel.Support;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

/**
 *
 * @author Martin
 */
public class GraphicUtils {
    
    public static Point getIntersectingPoint(Rectangle r, Point insidePoint, Point outsidePoint) {
        //Rectangle r = new Rectangle(getX(), getY(), bi.getWidth(), bi.getHeight());

        double s = (outsidePoint.getY() - insidePoint.getY()) / (outsidePoint.getX() - insidePoint.getX());

        Point intersectonPointX;
        Point intersectonPointY;


        if ((-r.height / 2.0 <= s * r.width / 2.0) && (s * r.width / 2.0 <= r.height / 2.0)) {
            if (outsidePoint.getX() > insidePoint.getX()) {
                // right edge
                //System.out.println("Right edge");
                intersectonPointX = new Point(r.x + r.width, r.y);
                intersectonPointY = new Point(r.x + r.width, r.y + r.height);
            } else {
                // left edge
                //System.out.println("Left edge");
                intersectonPointX = new Point(r.x, r.y);
                intersectonPointY = new Point(r.x, r.y + r.height);
            }
        } else {
            if (outsidePoint.getY() < insidePoint.getY()) {
                // top edge
                //System.out.println("Top edge");
                intersectonPointX = new Point(r.x, r.y);
                intersectonPointY = new Point(r.x + r.width, r.y);
            } else {
                // bottom edge
                //System.out.println("Bottom edge");
                intersectonPointX = new Point(r.x, r.y + r.height);
                intersectonPointY = new Point(r.x + r.width, r.y + r.height);
            }
        }
        return findLineIntersection(insidePoint, outsidePoint, intersectonPointX, intersectonPointY);
    }
    
    public static Point findLineIntersection(Point start1, Point end1, Point start2, Point end2) {
        float denom = (float) (((end1.getX() - start1.getX()) * (end2.getY() - start2.getY())) - ((end1.getY() - start1.getY()) * (end2.getX() - start2.getX())));

        float numer = (float) (((start1.getY() - start2.getY()) * (end2.getX() - start2.getX())) - ((start1.getX() - start2.getX()) * (end2.getY() - start2.getY())));

        float r = numer / denom;

        float numer2 = (float) (((start1.getY() - start2.getY()) * (end1.getX() - start1.getX())) - ((start1.getX() - start2.getX()) * (end1.getY() - start1.getY())));

        float s = numer2 / denom;

        int x = (int) (start1.getX() + (r * (end1.getX() - start1.getX())));
        int y = (int) (start1.getY() + (r * (end1.getY() - start1.getY())));
        // Find intersection point
        Point result = new Point(x, y);

        return result;
    }
}
