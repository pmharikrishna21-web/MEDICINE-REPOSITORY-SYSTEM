package ui;

import model.Medicine;
import util.DateUtil;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Details frame displaying complete information for a selected medicine record.
 */
public class MedicineDetailsFrame extends JFrame {

    public MedicineDetailsFrame(Medicine medicine) {
        initializeUI(medicine);
    }

    private void initializeUI(Medicine m) {
        setTitle("Medicine Specifications - " + m.getMedicineName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(680, 720);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(new Color(248, 250, 252));

        // Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 23, 42));
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel nameLabel = new JLabel(m.getMedicineName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameLabel.setForeground(Color.WHITE);

        JLabel genericLabel = new JLabel("Generic: " + m.getGenericName() + " | Category: " + m.getCategory());
        genericLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        genericLabel.setForeground(new Color(148, 163, 184));

        headerPanel.add(nameLabel, BorderLayout.NORTH);
        headerPanel.add(genericLabel, BorderLayout.SOUTH);

        // Content Scrollable Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(248, 250, 252));

        // Group 1: General & Clinical Information
        contentPanel.add(createSectionHeader("General & Clinical Information"));
        JPanel grid1 = new JPanel(new GridLayout(4, 2, 12, 10));
        grid1.setBackground(new Color(248, 250, 252));
        grid1.setBorder(new EmptyBorder(8, 0, 14, 0));

        addField(grid1, "Medicine ID:", m.getMedicineId());
        addField(grid1, "Category:", m.getCategory());
        addField(grid1, "Manufacturer:", m.getManufacturer());
        addField(grid1, "Dosage:", m.getDosage());
        addField(grid1, "Dosage Form:", m.getDosageForm());
        addField(grid1, "Prescription Required:", m.isPrescriptionRequired() ? "YES (Rx Only)" : "NO (Over-The-Counter)");
        contentPanel.add(grid1);

        // Group 2: Inventory & Pricing
        contentPanel.add(createSectionHeader("Inventory & Pricing"));
        JPanel grid2 = new JPanel(new GridLayout(2, 2, 12, 10));
        grid2.setBackground(new Color(248, 250, 252));
        grid2.setBorder(new EmptyBorder(8, 0, 14, 0));

        addField(grid2, "Unit Price:", String.format("$%.2f", m.getPrice()));
        addField(grid2, "Stock Quantity:", m.getStockQuantity() + " units");
        addField(grid2, "Stock Status:", m.getStockStatus());
        addField(grid2, "Operational Status:", m.getEffectiveStatus());
        contentPanel.add(grid2);

        // Group 3: Manufacturing & Expiry
        contentPanel.add(createSectionHeader("Batch & Expiry Details"));
        JPanel grid3 = new JPanel(new GridLayout(2, 2, 12, 10));
        grid3.setBackground(new Color(248, 250, 252));
        grid3.setBorder(new EmptyBorder(8, 0, 14, 0));

        addField(grid3, "Batch Number:", m.getBatchNumber());
        addField(grid3, "Manufacturing Date:", DateUtil.formatDisplay(m.getManufacturingDate()));
        addField(grid3, "Expiry Date:", DateUtil.formatDisplay(m.getExpiryDate()));
        addField(grid3, "Days Until Expiry:", DateUtil.daysUntilExpiry(m.getExpiryDate()) + " days");
        contentPanel.add(grid3);

        // Group 4: Textual Descriptions & Instructions
        contentPanel.add(createSectionHeader("Therapeutic Information & Storage"));
        contentPanel.add(createTextAreaSection("Description:", m.getDescription()));
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(createTextAreaSection("Primary Uses / Indications:", m.getUses()));
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(createTextAreaSection("Storage Instructions:", m.getStorageInstructions()));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);

        // Footer with Close Button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(new Color(248, 250, 252));
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        closeButton.setBackground(new Color(15, 23, 42));
        closeButton.setForeground(Color.WHITE);
        closeButton.setPreferredSize(new Dimension(100, 34));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dispose());
        footerPanel.add(closeButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    private JLabel createSectionHeader(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(14, 116, 144));
        label.setBorder(new EmptyBorder(10, 0, 4, 0));
        return label;
    }

    private void addField(JPanel panel, String label, String value) {
        JPanel fieldBox = new JPanel(new BorderLayout(5, 2));
        fieldBox.setBackground(new Color(248, 250, 252));

        JLabel titleLbl = new JLabel(label);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(new Color(100, 116, 139));

        JLabel valLbl = new JLabel(value != null ? value : "N/A");
        valLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valLbl.setForeground(new Color(15, 23, 42));

        fieldBox.add(titleLbl, BorderLayout.NORTH);
        fieldBox.add(valLbl, BorderLayout.CENTER);
        panel.add(fieldBox);
    }

    private JPanel createTextAreaSection(String title, String text) {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBackground(new Color(248, 250, 252));

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(100, 116, 139));

        JTextArea area = new JTextArea(text != null ? text : "None provided.");
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setBackground(Color.WHITE);
        area.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));

        panel.add(label, BorderLayout.NORTH);
        panel.add(area, BorderLayout.CENTER);
        return panel;
    }
}
