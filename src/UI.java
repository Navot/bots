import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;


public class UI{

    private static void createAndShowGUI() throws IOException {

        JFrame.setDefaultLookAndFeelDecorated(true);

        JFrame frame = new JFrame("AUTOMATZIA");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 600));
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2-400, dim.height/2-frame.getSize().height/2-300);
        frame.setLayout(new GridLayout(2,1));

        JButton uploadButton = new JButton("Upload Application");
        JButton screensButton = new JButton("Show Screens");
        JButton testsButton = new JButton("Show Tests");

        JPanel uploadContainer = new JPanel();
        uploadContainer.add(uploadButton);

        JPanel resultsContainer = new JPanel();
        resultsContainer.setLayout(new GridLayout(1,2));
        frame.setPreferredSize(new Dimension(500, 200));
        resultsContainer.add(testsButton);
        resultsContainer.add(screensButton);

        frame.add(uploadContainer);
        frame.add(resultsContainer);
        frame.pack();
        frame.setVisible(true);

        uploadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                File file = getFile();
                JPanel nameContainer = new JPanel();
                JLabel fileLabel = new JLabel(file.getName());
                nameContainer.add(fileLabel);
                Button startButton = new Button("Start Working");
                nameContainer.add(startButton);
                startButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Map<String,String> MAP = null;
                        try {
                            MAP = RunSetUp.getRunDetails();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (SAXException e1) {
                            e1.printStackTrace();
                        } catch (ParserConfigurationException e1) {
                            e1.printStackTrace();
                        }

                        frame.remove(uploadContainer);
                        frame.remove(resultsContainer);
                        frame.remove(fileLabel);
                        nameContainer.remove(startButton);

                        nameContainer.add(new Label("Working On - "+file.getName()));
                        Button stopButton = new Button("STOP!");
                        nameContainer.add(stopButton);
                        nameContainer.repaint();
                        frame.pack();
                        frame.repaint();
                        frame.setVisible(true);
                        Thread thread = new Thread(new Runner(MAP,file.getAbsolutePath()));
                        thread.start();
                    }
                });
                frame.remove(uploadContainer);
                frame.remove(resultsContainer);
                frame.add(nameContainer);
                frame.pack();
                frame.setVisible(true);
            }
        });
        screensButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.remove(uploadContainer);
                frame.remove(resultsContainer);
                frame.setLayout(new FlowLayout());
                frame.add(getScreens());
                frame.pack();
                frame.repaint();
                frame.setVisible(true);
            }
        });
        testsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.remove(resultsContainer);
                frame.getContentPane().add(getScreens());
                frame.pack();
                frame.setVisible(true);
            }
        });
     }

    private static File getFile() {
        File file = null;
        JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showOpenDialog(fileChooser);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            System.out.println(file);

        }
        return file;
    }

    private static JScrollPane getScreens()  {
        JPanel container = new JPanel();
        Collection<File> found = FileUtils.listFiles(new File("Results\\captures"), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        container.setLayout(new GridLayout(found.size(),2));
        for (File f : found) {
            System.out.println("Found file: " + f);
            container.add(new JLabel(f.getName().substring(0,f.getName().indexOf("_"))));
            BufferedImage img = null;
            try {
                img = ImageIO.read(f);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Image resizedImage =
                    img.getScaledInstance((int) (img.getWidth()*0.1), (int) (img.getHeight()*0.1), 2);
            container.add(new JLabel(new ImageIcon(resizedImage)));
        }
        JScrollPane scrPane = new JScrollPane(container);
        return scrPane;
    }

    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {


                try {
                    createAndShowGUI();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        });

    }

}