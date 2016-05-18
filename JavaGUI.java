package ADCrawler;

import com.jaunt.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.awt.Color.*;

/**
 *
 */


public class JavaGUI extends JFrame {
    private JLabel descriptionLabel, descriptionLabel1, userLabel, fnameLabel, lnameLabel, emailLabel, expireLabel,
            renewedLabel, fnameText, lnameText, expireText, renewedText;
    private JLabel[] smuInfo = new JLabel[20];
    private JLabel[] grouping = new JLabel[20];

    private JTextField userText, emailText;
    private JButton searchButton, clearButton;
    private JFrame pane;

    private JPanel upperPanel = new JPanel();
    private JPanel midPanel = new JPanel();
    private JPanel lowerPanel = new JPanel();

    private static final int HEIGHT = 600;
    private static final int WIDTH = 500;
    private SearchButtonHandler searchButtonHandler;
    private ClearButtonHandler clearButtonHandler;

    private ActiveDirectory ad;

    public JavaGUI(ActiveDirectory activeDirectory) {
        ad = activeDirectory;
        descriptionLabel = new JLabel("Please Enter an S# or an E-Mail address.");
        descriptionLabel1 = new JLabel("Example: s0000000 OR example@example.ca");
        userLabel = new JLabel("S Number: ");
        emailLabel = new JLabel("Email Address: ");
        fnameLabel = new JLabel("First name: ");
        lnameLabel = new JLabel("Last name: ");
        expireLabel = new JLabel("Expires on: ");
        renewedLabel = new JLabel("Renewed on: ");

        grouping[0] = new JLabel("Groups: ");
        fnameText = new JLabel("");
        lnameText = new JLabel("");
        expireText = new JLabel("");
        renewedText = new JLabel("");

        userText = new JTextField(12);
        emailText = new JTextField(12);


        //This section specifies the handlers for the buttons and adds an ActionListener.
        searchButton = new JButton("Search");
        searchButtonHandler = new SearchButtonHandler();
        searchButton.addActionListener(searchButtonHandler);
        userText.addActionListener(searchButtonHandler);
        emailText.addActionListener(searchButtonHandler);
        clearButton = new JButton("Clear");
        clearButtonHandler = new ClearButtonHandler();
        clearButton.addActionListener(clearButtonHandler);

        pane = new JFrame();
        pane.setTitle("Find user info v1.3 ~ Author: Damien Robinson");
        pane.setLayout(new GridLayout(3, 1));
        upperPanel.setLayout(new GridLayout(4, 2));
        midPanel.setLayout(new GridLayout(11, 2));
        lowerPanel.setLayout(new GridLayout(9, 2));
        midPanel.setBorder(BorderFactory.createLineBorder(black));
        lowerPanel.setBorder(BorderFactory.createLineBorder(black));

        pane.add(upperPanel);
        pane.add(midPanel);
        pane.add(lowerPanel);

        //Grid layout requires that you add components to the content pane in the order they should appear

        upperPanel.add(descriptionLabel);
        upperPanel.add(descriptionLabel1);
        upperPanel.add(userLabel);
        upperPanel.add(userText);
        upperPanel.add(emailLabel);
        upperPanel.add(emailText);
        upperPanel.add(searchButton);
        upperPanel.add(clearButton);
        upperPanel.setVisible(true);

        midPanel.add(fnameLabel);
        midPanel.add(fnameText);
        midPanel.add(lnameLabel);
        midPanel.add(lnameText);
        midPanel.add(renewedLabel);
        midPanel.add(renewedText);
        midPanel.add(expireLabel);
        midPanel.add(expireText);
        midPanel.add(grouping[0]);
        midPanel.setVisible(false);

        pane.setSize(HEIGHT, WIDTH);
        pane.setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        userText.setText("");
        emailText.setText("");
    }

    private class SearchButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            midPanel.setVisible(true);
            String queryField = "";
            String queryType = "";
            if (!userText.getText().equals("")) {
                queryField = userText.getText();
                queryType = "username";
            } else if (!emailText.getText().equals("")) {
                queryField = emailText.getText();
                queryType = "email";
            }
            ADCrawler adc = new ADCrawler(ad, queryField, queryType);
            fnameText.setText(adc.getFname());
            fnameText.setForeground(Color.RED);
            lnameText.setText(adc.getLname());
            lnameText.setForeground(Color.RED);
            emailText.setText(adc.getEmailAddress());
            userText.setText(adc.getUsername());
            cmdTool(adc.getUsername());
            try {
                getInfo(adc.getFname(), adc.getLname());
            } catch (JauntException e1) {
                e1.printStackTrace();
            }
            lowerPanel.setVisible(true);
        }
    }

    private class ClearButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            fnameText.setText("");
            lnameText.setText("");
            userText.setText("");
            emailText.setText("");
            renewedText.setText("");
            expireText.setText("");
            for (int i = 0; i < smuInfo.length; i++) {
                try {
                    smuInfo[i].setText("NULL");
                    upperPanel.remove(smuInfo[i]);
                } catch (NullPointerException n) {
                }
            }
            midPanel.setVisible(false);
            lowerPanel.setVisible(false);
        }
    }

    public static void main(String[] args) {
        LoginFrame loginFrame = new LoginFrame();
    }

    private void cmdTool(String username) {
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c",
                "net user /domain " + username);
        builder.redirectErrorStream(true);
        try {
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            int i = 1;
            while ((line = r.readLine()) != null) {
                if (line.contains("Password last")) {
                    renewedText.setText(line.substring(line.indexOf("set") + 15));
                    renewedText.setForeground(Color.RED);
                } else if (line.contains("Password expires")) {
                    expireText.setText(line.substring(line.lastIndexOf("expires") + 20));
                    expireText.setForeground(Color.RED);
                } else if (line.contains("*")) {
                    grouping[i] = new JLabel(line.substring(line.indexOf("*")));
                    grouping[i].setForeground(Color.RED);
                    midPanel.add(grouping[i]);
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getInfo(String firstName, String lastName) throws JauntException {
        UserAgent userAgent = new UserAgent();         //create new userAgent (headless browser)
        userAgent.visit("http://smuphone.smu.ca/shome/search.html");          //visit google
        userAgent.doc.apply(lastName);
        int i = 0;
        userAgent.doc.apply(firstName);   //apply form input (starting at first editable field)
        userAgent.doc.submit("Submit");         //click submit button labelled "Google Search"
        Elements tableElements;
        Elements noUser = userAgent.doc.findEvery("<b>");
        for (Element notFound : noUser) {
            if (!notFound.getText().equals("The exact search found no results. " +
                    "The search using SoundEx function resulted in following matches:"))
                return false;
        }
        Elements foundUser = userAgent.doc.findEvery("<a>");  //find search result links
        for (Element link : foundUser) {
            userAgent.visit(link.getAt("href"));
            tableElements = userAgent.doc.findEvery("<td>");
            for (Element nlink : tableElements) {
                    smuInfo[i] = new JLabel(nlink.getText());
                    if (i % 2 == 1) smuInfo[i].setForeground(Color.RED);
                    //smuInfo[i].setBorder(BorderFactory.createLineBorder(Color.black));
                    lowerPanel.add(smuInfo[i]);
                    i++;
                    if (nlink.getText().contains("Department URL Address")) break;
            }
        }
        return true;
    }
}


