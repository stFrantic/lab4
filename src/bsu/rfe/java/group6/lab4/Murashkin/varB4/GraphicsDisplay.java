package bsu.rfe.java.group6.lab4.Murashkin.varB4;
import java.awt.BasicStroke;
import static java.lang.Math.abs;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GraphicsDisplay extends JPanel {

    private Double[][] graphicsData;
    private boolean showAxis = true;
    private boolean showMarkers = true;
    private boolean showIntGraphics = false;

    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    private double scale;

    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;

    private Font axisFont;

    public GraphicsDisplay() {
        setBackground(Color.WHITE);
        // Перо для рисования графика
        graphicsStroke = new BasicStroke(2.0f,BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND,10.0f, new float[] {1, 1, 1, 1, 1, 1, 4, 1, 2, 1, 2},0.0f);
        // Перо для рисования осей координат
        axisStroke = new BasicStroke(2.0f,BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,10.0f,null,0.0f);
        // Перо  для рисования контуров маркеров
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,10.0f,null,0.0f);
        // Шрифт для подписей осей координат
        axisFont = new Font("Serif", Font.BOLD, 36);
    }

    public void showGraphics(Double[][] graphicsData) {
        this.graphicsData = graphicsData;
        repaint();
    }
    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }

    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graphicsData == null || graphicsData.length == 0) {
            return;
        }
        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length - 1][0];
        minY = graphicsData[0][1];
        maxY = minY;
        // Найти минимальное и максимальное значение функции
        for (int i = 1; i < graphicsData.length; i++) {
            if (graphicsData[i][1] < minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1] > maxY) {
                maxY = graphicsData[i][1];
            }
        }
        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);
        scale = Math.min(scaleX, scaleY);

        if (scale == scaleX) {
            double yIncrement = (getSize().getHeight() / scale - (maxY - minY)) / 2;
            maxY += yIncrement;
            minY -= yIncrement;
        }

        if (scale == scaleY) {
            // Если за основу был взят масштаб по оси Y, действовать по аналогии
            double xIncrement = (getSize().getWidth() / scale - (maxX - minX)) / 2;
            maxX += xIncrement;
            minX -= xIncrement;
        }
        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();
        if (showAxis) {
            paintAxis(canvas);
        }
        paintGraphics(canvas);
           if (showMarkers) {
            paintMarkers(canvas);
        }
        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }
    protected void paintGraphics(Graphics2D canvas) {
        canvas.setStroke(graphicsStroke);
        canvas.setColor(Color.RED);
        GeneralPath graphics = new GeneralPath();
        for (int i = 0; i < graphicsData.length; i++) {
            Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
            if (i > 0) {
                graphics.lineTo(point.getX(), point.getY());
            } else {
                graphics.moveTo(point.getX(), point.getY());
            }
        }
        canvas.draw(graphics);
    }

    protected void paintMarkers(Graphics2D canvas) {
        canvas.setStroke(markerStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);

        for (Double[] point : graphicsData) {
            double znach = point[1];
            double sum =  0;
            for (int i = 0; i < graphicsData.length; i++) {
                sum += graphicsData[i][1];
            }
            double avg = (sum/graphicsData.length);
            if (znach > (avg/2)) {
                canvas.setColor(Color.RED);
                canvas.setPaint(Color.RED);
            } else {
                canvas.setColor(Color.BLUE);
                canvas.setPaint(Color.BLUE);
            }
            canvas.setStroke(markerStroke);
            GeneralPath path = new GeneralPath();
            Point2D.Double center = xyToPoint(point[0], point[1]);
            canvas.draw(new Line2D.Double(shiftPoint(center, 0, 6), shiftPoint(center, 6, 0)));
            canvas.draw(new Line2D.Double(shiftPoint(center, 4, 0), shiftPoint(center, 0, -6)));
            canvas.draw(new Line2D.Double(shiftPoint(center, 0, -6), shiftPoint(center, -6, 0)));
            canvas.draw(new Line2D.Double(shiftPoint(center, -6, 0), shiftPoint(center, 0, 6)));
        }
    }

    // Метод, обеспечивающий отображение осей координат
    protected void paintAxis(Graphics2D canvas) {

        // Установить особое начертание для осей
        canvas.setStroke(axisStroke);
        // Оси рисуются чёрным цветом
        canvas.setColor(Color.BLACK);
        // Стрелки заливаются чёрным цветом
        canvas.setPaint(Color.BLACK);
        // Подписи к координатным осям делаются специальным шрифтом
        canvas.setFont(axisFont);
        // Создать объект контекста отображения текста - для получения характеристик устройства (экрана)
        FontRenderContext context = canvas.getFontRenderContext();

        Rectangle2D centerBounds = axisFont.getStringBounds("0", context);
        Point2D.Double centerLabelPos = xyToPoint(0, 0);
        canvas.drawString("0", (float)centerLabelPos.getX() + 10,
                (float)(centerLabelPos.getY() - centerBounds.getY()));
        // Определить, должна ли быть видна ось Y на графике
        if (minX <= 0.0 && maxX >= 0.0) {
            // Она должна быть видна, если левая граница показываемой области (minX) <= 0.0, а правая (maxX) >= 0.0
            // Сама ось - это линия между точками (0, maxY) и (0, minY)
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));
            // Стрелка оси Y
            GeneralPath arrow = new GeneralPath();
            // Установить начальную точку ломаной точно на верхний конец оси Y
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            // Вести левый "скат" стрелки в точку с относительными координатами (5,20)
            arrow.lineTo(arrow.getCurrentPoint().getX() + 5, arrow.getCurrentPoint().getY() + 20);
            // Вести нижнюю часть стрелки в точку с относительными координатами (-10, 0)
            arrow.lineTo(arrow.getCurrentPoint().getX() - 10, arrow.getCurrentPoint().getY());
            // Замкнуть треугольник стрелки
            arrow.closePath();
            canvas.draw(arrow); // Нарисовать стрелку
            canvas.fill(arrow); // Закрасить стрелку
            // Нарисовать подпись к оси Y
            // Определить, сколько места понадобится для надписи "y"
            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
            // Вывести надпись в точке с вычисленными координатами
            canvas.drawString("y",
                    (float) labelPos.getX() + 10,
                    (float) (labelPos.getY() - bounds.getY()));
        }

        // Определить, должна ли быть видна ось X на графике
        if (minY <= 0.0 && maxY >= 0.0) {
            // Она должна быть видна, если верхняя граница показываемой области (maxX) >= 0.0, а нижняя (minY) <= 0.0
            canvas.draw(new Line2D.Double(xyToPoint(minX, 0),
                    xyToPoint(maxX, 0)));
            // Стрелка оси X
            GeneralPath arrow = new GeneralPath();
            // Установить начальную точку ломаной точно на правый конец оси X
            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            // Вести верхний "скат" стрелки в точку с относительными координатами (-20,-5)
            arrow.lineTo(arrow.getCurrentPoint().getX() - 20,
                    arrow.getCurrentPoint().getY() - 5);
            // Вести левую часть стрелки в точку с относительными координатами (0, 10)
            arrow.lineTo(arrow.getCurrentPoint().getX(),
                    arrow.getCurrentPoint().getY() + 10);
            // Замкнуть треугольник стрелки
            arrow.closePath();
            canvas.draw(arrow); // Нарисовать стрелку
            canvas.fill(arrow); // Закрасить стрелку
            // Нарисовать подпись к оси X
            // Определить, сколько места понадобится для надписи "x"
            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
            // Вывести надпись в точке с вычисленными координатами
            canvas.drawString("x",
                    (float) (labelPos.getX() - bounds.getWidth() - 10),
                    (float) (labelPos.getY() + bounds.getY()));
        }
    }

    protected Point2D.Double xyToPoint(double x, double y) {
        double deltaX = x - minX;
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX * scale, deltaY * scale);
    }

    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX, double deltaY) {
        Point2D.Double dest = new Point2D.Double();
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }
}
