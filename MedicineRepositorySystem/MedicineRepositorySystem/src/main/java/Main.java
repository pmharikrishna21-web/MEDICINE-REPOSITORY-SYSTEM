import ui.LoginFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Main application entry point for the Medicine Repository System.
 * Sets the modern desktop Look and Feel and initiates the Swing Event Dispatch Thread.
 */
public class Main {

    public static void main(String[] args) {
        // Configure Look and Feel for a polished, modern desktop UI
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Fall back to default Swing look
            }
        }

        // Apply clean modern font defaults
        Font defaultFont = new Font("Segoe UI", Font.PLAIN, 13);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("Table.font", defaultFont);
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("TextArea.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);

        System.out.println("=================================================");
        System.out.println("   Medicine Repository System - Starting Up...   ");
        System.out.println("=================================================");
        System.out.println("Status: Initializing Java Swing Event Dispatch Thread");

        // Launch Login Frame on the Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
