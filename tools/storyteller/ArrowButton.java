package tools.storyteller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/* Arrow button that customizes UI style for storyteller project drop down.*/
public class ArrowButton extends JButton implements SwingConstants {
  private Color separatorColor;
  private Color arrowColor;

  /**
   * Creates an {@code ArrowButton} as part of a ComboBox.
   *
   * @param background the background color of the button
   * @param separatorColor the color of the left separator
   * @param arrowColor the color of the arrow
   */
  public ArrowButton(Color background, Color separatorColor, Color arrowColor) {
    super();
    setRequestFocusEnabled(false);
    setBackground(background);
    this.separatorColor = separatorColor;
    this.arrowColor = arrowColor;
  }

  public void paint(Graphics g) {
    final Color origColor = g.getColor();
    final boolean isPressed = getModel().isPressed();
    int w;
    int h;

    w = getSize().width;
    h = getSize().height;

    g.setColor(getBackground());
    g.fillRect(1, 1, w - 2, h - 2);

    g.setColor(separatorColor);
    g.drawLine(0, 0, 0, h - 1);

    // Draw the arrow
    int size = Math.min((h - 4) / 3, (w - 4) / 3);
    size = Math.max(size, 2);
    if (isPressed) {
      g.translate(1, 1);
    }
    paintTriangle(g, (w - size) / 2, (h - size) / 2, size);
    if (isPressed) {
      g.translate(-1, -1);
    }

    g.setColor(origColor);
  }

  public Dimension getPreferredSize() {
    return new Dimension(16, 16);
  }

  public Dimension getMinimumSize() {
    return new Dimension(5, 5);
  }

  public Dimension getMaximumSize() {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }

  /**
   * Paints a triangle.
   *
   * @param g the {@code Graphics} to draw to
   * @param x the x coordinate
   * @param y the y coordinate
   * @param size the size of the triangle to draw
   */
  public void paintTriangle(Graphics g, int x, int y, int size) {
    final Color oldColor = g.getColor();
    int mid;

    size = Math.max(size, 2);
    mid = (size / 2) - 1;

    g.translate(x, y);
    g.setColor(arrowColor);

    int j = 0;

    for (int i = size - 1; i >= 0; i--) {
      g.drawLine(mid - i, j, mid + i, j);
      j++;
    }

    g.translate(-x, -y);
    g.setColor(oldColor);
  }
}

