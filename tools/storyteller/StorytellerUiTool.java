package tools.storyteller;

import static java.time.temporal.ChronoUnit.MILLIS;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.startupos.common.CommonModule;
import com.google.startupos.common.flags.Flags;
import com.google.startupos.tools.reviewer.aa.AaModule;
import com.google.startupos.tools.reviewer.local_server.service.AuthService;
import dagger.Component;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicComboBoxUI;
import tools.storyteller.Protos.Config;
import tools.storyteller.Protos.Story;
import tools.storyteller.Protos.UiDefaults;

/* UI tool for Storyteller */
public class StorytellerUiTool {
  private static final int DEFAULT_WIDTH = 300;
  private static final int DEFAULT_HEIGHT = 440;
  private static final int LEFT = 20;
  private static final int WIDTH = DEFAULT_WIDTH - 2 * LEFT;
  private static final String HTML_TEMPLATE =
      "<font face=\"%s\" size=\"%d\" color=\"%s\">%s</font>";
  // Font sizes
  private static final int SMALL_TEXT = 14;
  private static final int NORMAL_TEXT = 20;
  // Heights
  private static final int TOP_HEIGHT = 30;
  private static final int SCREENSHOT_HEIGHT = 110;
  private static final int INPUTS_HEIGHT = 175;
  private static final int STATS_HEIGHT = 310;
  // Colors
  private static final Color DARK_GRAY = new Color(0x585858);
  private static final Color LIGHT_GRAY = new Color(0x808080);
  private static final Color DARK_BLUE = new Color(0x2869A5);
  private static final Color LIGHT_BLUE = new Color(0xF4F6F8);

  // Debug mode means things related to clock happen faster
  private static final boolean DEBUG_MODE = true;

  // Input args
  private static final String TIME_AND_SCREENSHOT_ARG = "time_and_screenshot";
  private static final String SCREENSHOT_ONLY_ARG = "screenshot_only";

  private Preferences preferences;
  private JFrame frame;
  private JToggleButton toggleButton;
  private JLabel timeLabel;
  private Storyteller storyteller;
  private ImmutableList<Story> unsharedStories;
  private Timer timer;
  private long timerStartTime;
  // Time accumulated by the timer in this run. Restarting the tool resets this
  private long accumulatedTimerTime;
  private long unsharedStoriesTime;
  private UiDefaults.Builder uiDefaults;
  private JComboBox projectDropDown;
  private JTextField storyTextArea;
  private Mode mode;
  private Config config;

  enum Mode {
    TIME_AND_SCREENSHOT,
    SCREENSHOT_ONLY
  }

  @Singleton
  @Component(modules = {CommonModule.class, AaModule.class})
  interface StorytellerUiToolComponent {
    StorytellerUiTool getStoryTellerUiTool();
  }

  public static void main(String[] args) {
    Flags.parse(args, AuthService.class.getPackage());
    checkInputArgs(args);

    StorytellerUiTool uiTool =
        DaggerStorytellerUiTool_StorytellerUiToolComponent.create().getStoryTellerUiTool();

    EventQueue.invokeLater(
        () -> {
          uiTool.mode = getMode(args[0]);
          uiTool.init();
        });
  }

  @Inject
  StorytellerUiTool(Storyteller storyteller, StorytellerConfig storytellerConfig) {
    this.storyteller = storyteller;
    this.config = storytellerConfig.getConfig();
  }

  private void init() {
    unsharedStories = storyteller.getUnsharedStories();
    for (Story story : unsharedStories) {
      unsharedStoriesTime += story.getEndTimeMs() - story.getStartTimeMs();
    }
    UIManager.put("ToggleButton.select", Color.WHITE);
    UIManager.put("JTextField.background", new ColorUIResource(Color.red));
    UIManager.put("ComboBox.selectionBackground", new ColorUIResource(LIGHT_BLUE));
    UIManager.put("ComboBox.selectionForeground", new ColorUIResource(Color.black));
    UIManager.put("ComboBox.buttonBackground", new ColorUIResource(LIGHT_BLUE));
    UIManager.put("ComboBox.buttonShadow", new ColorUIResource(Color.white));
    UIManager.put("ComboBox.buttonDarkShadow", new ColorUIResource(Color.white));

    initDefaults();
    initTimer();
    initFrame();
    addScreenshotButton();
    addProjectDropDown();
    addStoryText();
    if (mode.equals(Mode.TIME_AND_SCREENSHOT)) {
      addToggleButton();
      addTimeLabel();
      addBottomTimeSummaries();
    }
    frame.setVisible(true);
  }

  private void initDefaults() {
    preferences = Preferences.userRoot().node("/tools/storyteller");
    try {
      uiDefaults =
          UiDefaults.parseFrom(preferences.getByteArray("defaults", new byte[0])).toBuilder();
    } catch (InvalidProtocolBufferException e) {
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
      Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
      int defaultLeft = (int) rect.getMaxX() - DEFAULT_WIDTH;
      int defaultTop = (int) rect.getMaxY() - DEFAULT_HEIGHT;
      uiDefaults = UiDefaults.newBuilder().setLeft(defaultLeft).setTop(defaultTop);
    }
  }

  /* Init timer. This does not start the timer. */
  private void initTimer() {
    final int timerPeriodicity = DEBUG_MODE ? 1000 : 1000 * 60;
    timer =
        new Timer(
            timerPeriodicity,
            event -> {
              long millisPassed =
                  System.currentTimeMillis() - timerStartTime + accumulatedTimerTime;
              if (DEBUG_MODE) {
                millisPassed *= 60;
              }
              int minutesPassed = (int) (millisPassed / (1000 * 60));
              timeLabel.setText(getTimeHtml(millisPassed));
              storyteller.periodicUpdate(
                  minutesPassed,
                  projectDropDown.getSelectedItem().toString(),
                  storyTextArea.getText());
            });
  }

  private void initFrame() {
    frame = new JFrame();

    frame.setLayout(null);
    frame.setResizable(false);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // TODO: Fix drift - every time the tool is run, it starts slightly higher.
    frame.setLocation(uiDefaults.getLeft(), uiDefaults.getTop());
    frame
        .getContentPane()
        .setPreferredSize(
            new Dimension(
                DEFAULT_WIDTH,
                mode.equals(Mode.SCREENSHOT_ONLY) ? DEFAULT_HEIGHT - 230 : DEFAULT_HEIGHT));
    frame.pack();
    frame.setTitle("Storyteller");
    frame.getContentPane().setBackground(Color.WHITE);
    frame.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            onClose();
            System.exit(0);
          }
        });
  }

  private void addToggleButton() {
    final ImageIcon onIcon = getIcon("pause.png", "Pause");
    final ImageIcon offIcon = getIcon("play.png", "Play");

    toggleButton = new JToggleButton();
    toggleButton.setIcon(offIcon);
    toggleButton.setBounds(DEFAULT_WIDTH - LEFT - 96, TOP_HEIGHT + 12, 96, 36);
    toggleButton.setBackground(Color.WHITE);

    toggleButton.setPreferredSize(
        new Dimension(onIcon.getIconWidth() + 8, onIcon.getIconHeight() + 8));
    toggleButton.setMargin(new Insets(1, 1, 1, 1));
    if (!"Aqua".equals(UIManager.getLookAndFeel().getID())) {
      // Remove border, except on Mac where it paints the toggle state
      toggleButton.setBorder(
          new EmptyBorder(toggleButton.getBorder().getBorderInsets(toggleButton)));
    }
    toggleButton.setBorder(new LineBorder(LIGHT_GRAY, 1, true));

    toggleButton.setFocusable(false);
    toggleButton.addItemListener(
        event -> {
          if (toggleButton.isSelected()) {
            toggleButton.setIcon(onIcon);
            storyteller.startup(projectDropDown.getSelectedItem().toString());
            timer.start();
            timerStartTime = System.currentTimeMillis();
            timeLabel.setText(getTimeHtml(accumulatedTimerTime));
            updateProjectMruAndDropDown();
          } else {
            toggleButton.setIcon(offIcon);
            storyteller.shutdown(projectDropDown.getSelectedItem().toString());
            timer.stop();
            accumulatedTimerTime += System.currentTimeMillis() - timerStartTime;
          }
        });
    frame.add(toggleButton);
  }

  /* Html for time labels, in the form of "3h 15m" where the numbers are big.*/
  private String getTimeHtml(long millis) {
    Duration duration = Duration.of(millis, MILLIS);
    long hours = duration.toHours();
    long minutes = duration.minusHours(hours).toMinutes();
    String darkBlue = colorToString(DARK_BLUE);
    String darkGray = colorToString(DARK_GRAY);

    String html = "<html><center>";
    if (hours > 0) {
      html += String.format(HTML_TEMPLATE, "arial", 12, darkBlue, hours);
      html += String.format(HTML_TEMPLATE, "arial", 6, darkGray, "h");
    }
    html += String.format(HTML_TEMPLATE, "arial", 12, darkBlue, minutes);
    html += String.format(HTML_TEMPLATE, "arial", 6, darkGray, "m");
    return html;
  }

  private void addTimeLabel() {
    timeLabel = addLabel(getTimeHtml(0), LEFT, TOP_HEIGHT, WIDTH, 60, 20);
  }

  private void updateProjectMruAndDropDown() {
    String project = (String) projectDropDown.getSelectedItem();
    // Put 'project' on top of MRU
    ArrayList<String> projects = new ArrayList<>(uiDefaults.getProjectList());
    projects.remove(project);
    projects.add(0, project);
    uiDefaults.clearProject();
    uiDefaults.addAllProject(projects);
    projectDropDown.setModel(new DefaultComboBoxModel<String>(getProjects()));
    projectDropDown.setSelectedItem(project);
  }

  private void addScreenshotButton() {
    JButton button = new JButton("Take Screenshot");
    button.setLocation(LEFT, SCREENSHOT_HEIGHT);
    if (mode.equals(Mode.SCREENSHOT_ONLY)) {
      button.setLocation(LEFT, SCREENSHOT_HEIGHT - 90);
    }
    button.setSize(WIDTH, 40);
    button.setFont(new Font("Arial", Font.PLAIN, 18));
    button.setFocusable(false);
    button.setBackground(LIGHT_BLUE);
    button.setForeground(DARK_BLUE);
    button.setBorder(new LineBorder(LIGHT_GRAY, 1, true));
    button.addActionListener(
        event -> {
          String project = projectDropDown.getSelectedItem().toString();
          if (!isTimerOn() && mode.equals(Mode.TIME_AND_SCREENSHOT)) {
            storyteller.startup(project);
          }
          storyteller.saveScreenshot();
          if (!isTimerOn() && mode.equals(Mode.TIME_AND_SCREENSHOT)) {
            storyteller.shutdown(project);
          }
          System.out.println("Screenshot saved");
        });
    frame.add(button);
  }

  private JLabel addLabel(String text, int left, int top, int width, int height, int fontSize) {
    JLabel label = new JLabel(text);
    label.setLocation(left, top);
    label.setSize(width, height);
    label.setFont(new Font("Arial", Font.PLAIN, fontSize));
    label.setForeground(DARK_GRAY);
    frame.add(label);
    return label;
  }

  private void addProjectDropDown() {
    if (mode.equals(Mode.SCREENSHOT_ONLY)) {
      addLabel("Project:", LEFT, INPUTS_HEIGHT - 100, WIDTH, SMALL_TEXT, SMALL_TEXT);
    } else {
      addLabel("Project:", LEFT, INPUTS_HEIGHT, WIDTH, SMALL_TEXT, SMALL_TEXT);
    }
    projectDropDown = new JComboBox(getProjects());
    projectDropDown.setLocation(LEFT, INPUTS_HEIGHT + 20);
    if (mode.equals(Mode.SCREENSHOT_ONLY)) {
      projectDropDown.setLocation(LEFT, INPUTS_HEIGHT - 80);
    }
    projectDropDown.setSize(WIDTH, 30);
    projectDropDown.setUI(new DropDownArrowUI());
    projectDropDown.setBorder(new LineBorder(LIGHT_GRAY, 1, true));
    frame.add(projectDropDown);
  }

  private void addStoryText() {
    int height = INPUTS_HEIGHT + 60;
    if (mode.equals(Mode.SCREENSHOT_ONLY)) {
      height = INPUTS_HEIGHT - 40;
    }
    addLabel("Story:", LEFT, height, WIDTH, SMALL_TEXT, SMALL_TEXT);
    storyTextArea = new JTextField(20);
    storyTextArea.setLocation(LEFT, height + 20);
    storyTextArea.setSize(WIDTH, 30);
    storyTextArea.setBackground(LIGHT_BLUE);
    frame.add(storyTextArea);
  }

  private void addBottomTimeSummaries() {
    int height = STATS_HEIGHT;
    addLabel("Unshared", LEFT, height, WIDTH, 30, SMALL_TEXT);
    addLabel("Today (shared)", LEFT, height + 40, WIDTH, 30, SMALL_TEXT);
    addLabel("This week (shared)", LEFT, height + 80, WIDTH, 30, SMALL_TEXT);
    int left = DEFAULT_WIDTH - LEFT - WIDTH;
    JLabel unsharedLabel = addLabel("TBD", left, height, WIDTH, 30, NORMAL_TEXT);
    unsharedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    JLabel todayLabel = addLabel("TBD", left, height + 40, WIDTH, 30, NORMAL_TEXT);
    todayLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    JLabel thisWeekLabel = addLabel("TBD", left, height + 80, WIDTH, 30, NORMAL_TEXT);
    thisWeekLabel.setHorizontalAlignment(SwingConstants.RIGHT);
  }

  /* Get projects list, placing the MRU projects on top. */
  private String[] getProjects() {
    ImmutableList.Builder<String> result = new ImmutableList.Builder<>();
    for (String project : uiDefaults.getProjectList()) {
      if (config.getProjectsList().contains(project)) {
        result.add(project);
      }
    }
    for (String project : config.getProjectsList()) {
      // No contains in builder.. :(
      if (!result.build().contains(project)) {
        result.add(project);
      }
    }
    return result.build().toArray(new String[0]);
  }

  private void onClose() {
    uiDefaults = uiDefaults.setLeft(frame.getX()).setTop(frame.getY());
    preferences.putByteArray("defaults", uiDefaults.build().toByteArray());
    if (isTimerOn()) {
      storyteller.shutdown(projectDropDown.getSelectedItem().toString());
    }
  }

  private boolean isTimerOn() {
    return mode.equals(Mode.TIME_AND_SCREENSHOT) && toggleButton.isSelected();
  }

  private ImageIcon getIcon(String filename, String description) {
    return new ImageIcon(Resources.getResource("tools/storyteller/res/" + filename), description);
  }

  private class DropDownArrowUI extends BasicComboBoxUI {
    @Override
    protected JButton createArrowButton() {
      return new ArrowButton(LIGHT_BLUE, LIGHT_GRAY, DARK_BLUE);
    }
  }

  private String colorToString(Color colour) {
    return "#" + String.format("%06x", colour.getRGB() & 0xffffff);
  }

  private static Mode getMode(String arg) {
    switch (arg) {
      case TIME_AND_SCREENSHOT_ARG:
        return Mode.TIME_AND_SCREENSHOT;
      case SCREENSHOT_ONLY_ARG:
        return Mode.SCREENSHOT_ONLY;
      default:
        throw new IllegalArgumentException(
            "Input arg is incorrect. "
                + "Expected: "
                + TIME_AND_SCREENSHOT_ARG
                + " or "
                + SCREENSHOT_ONLY_ARG);
    }
  }

  private static void checkInputArgs(String[] args) {
    if (args.length == 0) {
      throw new IllegalArgumentException(
          "Running mode not specified ("
              + TIME_AND_SCREENSHOT_ARG
              + " or "
              + SCREENSHOT_ONLY_ARG
              + ")");
    } else if (args.length > 1) {
      throw new IllegalArgumentException("Input args contain more then one argument.");
    }
  }
}

