package ADCrawler;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
    This class serves to log into the main program.
    if a user is able to authenticate their account with
    active directory they may query any information available.
 */

public class LoginFrame {

    private JFrame loginPage = new JFrame();
    private JPanel loginPanel = new JPanel();
    private JLabel username, password;


    private JTextField user;
    private JPasswordField pwd;
    private LoginButtonHandler loginButtonHandler;
    private CancelButtonHandler cancelButtonHandler;

    public LoginFrame(){

        loginPage.setTitle("Login ~ ADCrawler v1.3 ~ Author: Damien Robinson");

        username = new JLabel("Enter your S#: ");
        password = new JLabel("Enter your password: ");
        user = new JTextField(12);
        pwd = new JPasswordField(12);

        JButton login, cancel;
        login = new JButton("Login");
        cancel = new JButton("Cancel");
        loginButtonHandler = new LoginButtonHandler();
        login.addActionListener(loginButtonHandler);
        cancelButtonHandler = new CancelButtonHandler();
        cancel.addActionListener(cancelButtonHandler);
        pwd.addActionListener(loginButtonHandler);

        // Add panel components in order.

        loginPanel.setLayout(new GridLayout(4,2));
        loginPanel.add(username);
        loginPanel.add(user);
        loginPanel.add(password);
        loginPanel.add(pwd);
        loginPanel.add(login);
        loginPanel.add(cancel);
        loginPanel.setVisible(true);

        loginPage.setSize(320, 150);
        loginPage.add(loginPanel);
        loginPage.setVisible(true);
    }



    private class LoginButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            ActiveDirectory ad = new ActiveDirectory(user.getText(), pwd.getPassword(), "SMUNET.SMU.CA");
            if(ad.isAuthenticated()){
                //Remove Login screen
                loginPanel.setVisible(false);
                loginPage.setVisible(false);

                //Open find user GUI
                JavaGUI javagui = new JavaGUI(ad);

            } else {

                //Alert user to incorrect credntials.
                JLabel auth, isAuth;

                auth = new JLabel("",SwingConstants.RIGHT);
                isAuth = new JLabel();

                loginPanel.add(auth);
                loginPanel.add(isAuth);

                auth.setText("Username or Password ");
                isAuth.setText("is Incorrect");

                isAuth.setForeground(Color.RED);
                auth.setForeground(Color.RED);

            }
        }
    }

    private class CancelButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
}
