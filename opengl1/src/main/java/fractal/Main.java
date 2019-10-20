package fractal;

import com.jogamp.newt.event.*;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;

import javax.swing.*;
import java.awt.*;

public class Main implements KeyListener, MouseListener {

  private static GLWindow window;
  private static Animator animator;
  private JuliaFractal julia;

  private boolean frameAlive = true;

  private static final int sizeX = 1024;
  private static final int sizeY = 768;

  private int posX = 0;
  private int posY = 0;
  private JFrame frame;

  public static void main(String[] args) {
    new Main().setup();
  }

  private void setup() {

    GLProfile glProfile = GLProfile.get(GLProfile.GL4);
    GLCapabilities glCapabilities = new GLCapabilities(glProfile);
    glCapabilities.setHardwareAccelerated(true);

    window = GLWindow.create(glCapabilities);
    window.setSize(sizeX, sizeY);
    window.setResizable(true);
    window.setTitle("Fractal");


    window.setContextCreationFlags(GLContext.CTX_OPTION_DEBUG);
    window.setVisible(true);

    window.getContext().addGLDebugListener(System.out::println);
    julia = new JuliaFractal(sizeX, sizeY);
    window.addGLEventListener(julia);
    window.addKeyListener(this);
    window.addMouseListener(this);

    frame = new JFrame("Settings");

    Box sliders = Box.createVerticalBox();
    JSlider rSlider = new JSlider(10, 1000);
    JSlider iterationSlider = new JSlider(1, 1000);

    rSlider.addChangeListener((e) -> {
      julia.changeR(rSlider.getValue() / 100f);
    });

    iterationSlider.addChangeListener((e) -> {
      julia.changeMaxIter(iterationSlider.getValue());
    });

    rSlider.setValue(200);
    iterationSlider.setValue(70);

    sliders.add(new Label("Threshold (0.1 - 10)"));
    sliders.add(rSlider);

    sliders.add(new Label("Max iterations (1 - 1000)"));
    sliders.add(iterationSlider);


    final JPanel openGLPanel = new JPanel();
    openGLPanel.setLayout(new BoxLayout(openGLPanel, BoxLayout.LINE_AXIS));
    openGLPanel.add(sliders);

    frame.getContentPane().add(openGLPanel);

    frame.setSize(sizeX / 4, sizeY / 6);
    frame.setVisible(true);

    animator = new Animator(window);
    animator.start();

    frame.addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(java.awt.event.WindowEvent e) {
        frameAlive = false;
        window.destroy();
      }
    });

    window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowDestroyed(WindowEvent e) {
        animator.stop();
        if (frameAlive) {
          closeFrame();
        }
        System.exit(0);
      }
    });
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
      closeFrame();
    }
  }

  private void closeFrame() {
    new Thread( () ->
      frame.dispatchEvent(new java.awt.event.WindowEvent(frame, java.awt.event.WindowEvent.WINDOW_CLOSING))
    ).start();
  }

  @Override
  public void keyReleased(KeyEvent e) {}

  private void setPos(int x, int y) {
    posX = x;
    posY = y;
  }

  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {}

  @Override
  public void mousePressed(MouseEvent e) {}

  @Override
  public void mouseReleased(MouseEvent e) {}

  @Override
  public void mouseMoved(MouseEvent e) {
    setPos(e.getX(), e.getY());
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    int x = e.getX() - posX;
    int y = e.getY() - posY;
    setPos(e.getX(), e.getY());
    julia.moveCenter(x, y);
  }

  @Override
  public void mouseWheelMoved(MouseEvent e) {
    julia.zoom(1 + 0.1f * e.getRotation()[1], e.getX(), e.getY());
  }
}