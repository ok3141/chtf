import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

public class Launcher {
    private static final Color COLOR_DARK_GREEN = new Color(0, 192, 0);
    private static final Color COLOR_DARK_ORANGE = new Color(224, 96, 0);
    private static final int INPUT_COUNT = 10;
    private static final int BUTTON_SIZE = 20;
    private static final int WINDOW_WIDTH = 1500;
    private static final int MENU_WIDTH = 200;
    private static final int WINDOW_HEIGHT = 800;
    public static final String EC_PROBLEMS = "ABCDEFGH";
    public static final String PREEC_PROBLEMS = "PQRS";

    public static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        final String problems;
        final String title;

        if (args.length > 0 && args[0].equals("-p")) {
            problems = PREEC_PROBLEMS;
            title = "Challenge24 :: PreEC";
        } else {
            problems = EC_PROBLEMS;
            title = "Challenge24 :: EC";
        }

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                start(problems, title);
            }
        });
    }

    interface Messanger {
        void setMessage(String text, Color color);
    }

    private static void start(final String problems, final String title) {
        final JFrame frame = new JFrame();
        final JLabel msg = new JLabel();
        final JPanel parametersPanel = new JPanel();
        final Map<String, JTextField> parameters = new HashMap<String, JTextField>();
        final JButton[] problemsButtons = new JButton[INPUT_COUNT];
        final JButton[] inputButtons = new JButton[INPUT_COUNT];
        final Problem[] problemInstance = new Problem[1];
        final int[] inputId = { -1 };
        final boolean[] solving = { false };
        final int buttonSize = BUTTON_SIZE;
        final int problemsCount = problems.length();

        final Messanger messanger = new Messanger() {
            public void setMessage(String text, Color color) {
                msg.setForeground(color);
                msg.setText(text);
            }
        };

        final Runnable onProblemLoaded = new Runnable() {
            public void run() {
                parametersPanel.removeAll();
                parameters.clear();

                int offsetY = 0;

                for (String paramName : problemInstance[0].getParams()) {
                    JLabel param = new JLabel();
                    param.setBounds(0, offsetY, MENU_WIDTH / 2, BUTTON_SIZE);
                    param.setBorder(new BevelBorder(BevelBorder.RAISED));
                    param.setText(paramName);
                    parametersPanel.add(param);

                    JTextField input = new JTextField();
                    input.setBounds(MENU_WIDTH / 2, offsetY, MENU_WIDTH / 2, BUTTON_SIZE);
                    input.setText(problemInstance[0].getParam(paramName));
                    input.setBorder(new BevelBorder(BevelBorder.LOWERED));
                    parametersPanel.add(input);

                    parameters.put(paramName, input);

                    offsetY += BUTTON_SIZE;
                }

                parametersPanel.updateUI();
            }
        };

        MouseListener onProblemSelected = new MouseAdapter() {
            public void mouseClicked(MouseEvent ev) {
                if (solving[0]) {
                    return;
                }

                if (!ev.getComponent().isEnabled()) {
                    return;
                }

                try {
                    for (int i = 0; i < problemsCount; ++i) {
                        JButton b = problemsButtons[i];
                        if (b.equals(ev.getComponent())) {
                            b.setForeground(Color.BLACK);
                        } else {
                            b.setForeground(Color.GRAY);
                        }
                    }

                    problemInstance[0] = (Problem) Class.forName(((JButton) ev.getComponent()).getText()).newInstance();
                    onProblemLoaded.run();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    ev.getComponent().setEnabled(false);
                }
            }
        };

        MouseListener onInputSelected = new MouseAdapter() {
            public void mouseClicked(MouseEvent ev) {
                if (solving[0]) {
                    return;
                }

                if (!ev.getComponent().isEnabled()) {
                    return;
                }

                try {
                    for (int i = 0; i < INPUT_COUNT; ++i) {
                        JButton b = inputButtons[i];
                        if (b.equals(ev.getComponent())) {
                            b.setForeground(Color.BLACK);
                            inputId[0] = i;
                        } else {
                            b.setForeground(Color.GRAY);
                        }
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    ev.getComponent().setEnabled(false);
                }
            }
        };

        MouseListener onGoClicked = new MouseAdapter() {
            public void mouseClicked(MouseEvent ev) {
                if (!ev.getComponent().isEnabled()) {
                    return;
                }

                final Problem p = problemInstance[0];
                if (p == null) {
                    messanger.setMessage("Problem not selected!", Color.RED);
                    return;
                }

                final int i = inputId[0] + 1;
                if (i < 1 || i > INPUT_COUNT) {
                    messanger.setMessage("Input not selected!", Color.RED);
                    return;
                }

                final String pi = p.getClass().getSimpleName() + ", " + p.getInputFilename(i);
                messanger.setMessage("Solving problem: " + pi, Color.BLUE);

                final JButton b = (JButton) ev.getComponent();
                b.setEnabled(false);
                solving[0] = true;

                for (Entry<String, JTextField> e : parameters.entrySet()) {
                    p.setParam(e.getKey(), e.getValue().getText());
                }

                final Messanger async = new Messanger() {
                    public void setMessage(final String text, final Color color) {
                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                messanger.setMessage(text, color);
                                b.setEnabled(true);
                                solving[0] = false;
                            }
                        });
                    }
                };

                EXECUTOR.execute(new Runnable() {
                    public void run() {
                        try {
                            p.solve(i);
                            async.setMessage("Solved problem: " + pi, COLOR_DARK_GREEN);
                        } catch (ProblemStoppedException ex) {
                            ex.printStackTrace();
                            async.setMessage("Stopped problem: " + pi, COLOR_DARK_ORANGE);
                        } catch (Throwable ex) {
                            ex.printStackTrace();
                            async.setMessage("Failed problem: " + pi, Color.RED);
                        }
                    }
                });
            }
        };

        MouseListener onStopClicked = new MouseAdapter() {
            public void mouseClicked(MouseEvent ev) {
                if (!ev.getComponent().isEnabled()) {
                    return;
                }

                final Problem p = problemInstance[0];
                if (p != null) {
                    p.stop();
                }
            }
        };

        frame.setBounds(50, BUTTON_SIZE, WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setTitle(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLayout(null);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                EXECUTOR.shutdownNow();
            }
        });

        final JPanel menu = new JPanel();
        menu.setBounds(0, 0, MENU_WIDTH, WINDOW_HEIGHT);
        menu.setLayout(null);
        frame.add(menu, BorderLayout.NORTH);

        int offsetY = 0;

        JButton buttonGo = new JButton("Go");
        buttonGo.setBounds(0, offsetY, MENU_WIDTH / 2, buttonSize);
        menu.add(buttonGo);
        buttonGo.addMouseListener(onGoClicked);

        JButton buttonStop = new JButton("Stop");
        buttonStop.setBounds(MENU_WIDTH / 2, offsetY, MENU_WIDTH / 2, buttonSize);
        menu.add(buttonStop);
        buttonStop.addMouseListener(onStopClicked);

        offsetY += buttonSize;

        msg.setBounds(0, offsetY, MENU_WIDTH, buttonSize);
        menu.add(msg);

        offsetY += buttonSize;

        JLabel problem = new JLabel("Problem");
        problem.setBounds(0, offsetY, MENU_WIDTH, buttonSize);
        menu.add(problem);

        offsetY += buttonSize;

        for (int i = 0; i < problemsCount; ++i) {
            JButton button = new JButton();
            button.setMargin(new Insets(0, 0, 0, 0));
            button.setBounds(i * buttonSize, offsetY, buttonSize, buttonSize);
            button.setLayout(null);
            button.setText(String.valueOf(problems.charAt(i)));
            button.setForeground(Color.GRAY);
            menu.add(button);
            problemsButtons[i] = button;

            button.addMouseListener(onProblemSelected);
        }

        offsetY += buttonSize;

        JLabel input = new JLabel("Input");
        input.setBounds(0, offsetY, 200, buttonSize);
        menu.add(input);

        offsetY += buttonSize;

        for (int i = 0; i < INPUT_COUNT; ++i) {
            JButton button = new JButton();
            button.setMargin(new Insets(0, 0, 0, 0));
            button.setBounds(i * buttonSize, offsetY, buttonSize, buttonSize);
            button.setLayout(null);
            button.setText(String.valueOf(i + 1));
            button.setForeground(Color.GRAY);
            menu.add(button);
            inputButtons[i] = button;

            button.addMouseListener(onInputSelected);
        }

        offsetY += buttonSize;

        JLabel parametersLabel = new JLabel("Parameters");
        parametersLabel.setBounds(0, offsetY, 200, buttonSize);
        menu.add(parametersLabel);

        offsetY += buttonSize;

        parametersPanel.setBounds(0, offsetY, MENU_WIDTH, WINDOW_HEIGHT - offsetY);
        parametersPanel.setLayout(null);
        menu.add(parametersPanel);

        final OutputTextArea logArea = new OutputTextArea();
        Font font = new Font("Monospaced", Font.PLAIN, 14);
        logArea.setFont(font);

        final JScrollPane scroll = new JScrollPane(logArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        int scrollWidth = WINDOW_WIDTH - MENU_WIDTH - scroll.getVerticalScrollBar().getPreferredSize().width;
        int scrollHeight = WINDOW_HEIGHT - scroll.getHorizontalScrollBar().getPreferredSize().height;
        scroll.setBounds(MENU_WIDTH, 0, scrollWidth, scrollHeight);

        frame.add(scroll);
        frame.setVisible(true);

        final OutputStream logStream = new OutputStream() {
            public void write(int b) throws IOException {
                logArea.write(b);
            }
        };

        System.setOut(new PrintStream(logStream));
        System.setErr(System.out);
    }
}
