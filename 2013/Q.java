import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Q {
    public static final PrintStream log = System.out;
    public static final PrintStream err = System.err;
    public static final int MUTE = 128;
    public static final boolean LOG_FILE = false;

    public static void main(String[] args) throws Throwable {
        final PrintStream logFile;
        if (LOG_FILE) {
            logFile = new PrintStream("log.q" + Challenge.buildOutFilename(args[0]));
        } else {
            logFile = new PrintStream(new OutputStream() {
                public void write(int arg0) throws IOException {
                    // silent
                }
            });
        }

        final PrintStream out = new PrintStream("q" + Challenge.buildOutFilename(args[0]));
        DataInputStream in = new DataInputStream(new FileInputStream(args[0]));

        JFrame frame = new JFrame();
        frame.setBounds(250, 20, 1200, 820);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton save = new JButton();
        save.setBounds(810, 10, 200, 30);
        save.setText("Save 'q" + Challenge.buildOutFilename(args[0]) + "'");
        frame.add(save);

        ImagePanel image = new ImagePanel();
        image.setLayout(null);
        image.setImagePath(args[0]);
        image.setBounds(0, 0, 800, 800);
        frame.add(image);

        frame.setVisible(true);

        Painter painter = new Painter(image, out);
        image.addMouseMotionListener(painter);
        image.addMouseListener(painter);

        save.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent arg0) {
                // TODO Auto-generated method stub
            }

            public void mousePressed(MouseEvent arg0) {
                // TODO Auto-generated method stub
            }

            public void mouseExited(MouseEvent arg0) {
                // TODO Auto-generated method stub
            }

            public void mouseEntered(MouseEvent arg0) {
                // TODO Auto-generated method stub
            }

            public void mouseClicked(MouseEvent arg0) {
                out.println("exit");
                out.close();
            }
        });
    }
}

class Painter implements MouseMotionListener, MouseListener {
    JPanel parent;
    JPanel active;
    PrintStream out;

    public Painter(JPanel parent, PrintStream out) {
        super();
        this.parent = parent;
        this.out = out;
    }

    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void mousePressed(MouseEvent e) {
        active = new JPanel();
        active.setBounds(e.getX(), e.getY(), 2, 2);
        active.setBackground(Color.RED);
        parent.add(active);
        parent.updateUI();
    }

    public void mouseReleased(MouseEvent e) {
        int w = active.getWidth();
        int h = active.getHeight();

        int s = Math.min(w, h);
        boolean horz = w > h;

        int scale = 20;

        if (horz) {
            int c = w / s;
            for (int i = 0; i < c; ++i) {
                out.println("movc R3," + active.getX() * scale + i * s * scale);
                out.println("movc R4," + active.getY() * scale);
                out.println("movc R5," + s * scale);
                out.println("erase R3");
                out.println("erase R3");
                out.println("erase R3");
                out.println("erase R3");
            }
        } else {
            int c = h / s;
            for (int i = 0; i < c; ++i) {
                out.println("movc R3," + active.getX() * scale);
                out.println("movc R4," + active.getY() * scale + i * s * scale);
                out.println("movc R5," + s * scale);
                out.println("erase R3");
                out.println("erase R3");
                out.println("erase R3");
                out.println("erase R3");

            }
        }

        active = null;
    }

    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub
        active.setBounds(active.getX(), active.getY(), e.getX() - active.getX(), e.getY() - active.getY());
    }

    public void mouseMoved(MouseEvent e) {
    }
}
