package ass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class concert {
    public static void main(String[] args) {
        new WelcomeScreen();
    }
}

// Welcome Screen
class WelcomeScreen {
    JFrame frame;

    public WelcomeScreen() {
        frame = new JFrame("Welcome to Concert Booking");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel welcomeLabel = new JLabel("Welcome to the Concert Booking System!", JLabel.CENTER);
        JButton startButton = new JButton("Start Booking");

        frame.setLayout(new BorderLayout());
        frame.add(welcomeLabel, BorderLayout.CENTER);
        frame.add(startButton, BorderLayout.SOUTH);
        frame.setVisible(true);

        startButton.addActionListener(e -> {
            new BookingForm();
            frame.dispose();
        });
    }
}

// Booking Form
class BookingForm {
    JFrame frame;
    JTextField nameField;
    JComboBox<String> artistBox, timeBox, zoneBox;
    JSpinner quantitySpinner;
    JLabel totalLabel;
    JList<String> seatList;
    double unitPrice = 0;

    public BookingForm() {
        frame = new JFrame("Concert Booking");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        nameField = new JTextField();
        artistBox = new JComboBox<>(new String[]{"Ariana Grande", "Imagine Dragons", "Yiruma"});
        timeBox = new JComboBox<>(new String[]{"5:00 PM", "7:00 PM", "9:00 PM"});
        zoneBox = new JComboBox<>(new String[]{"VIP - RM 300", "Standing - RM 150", "General - RM 80"});
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        totalLabel = new JLabel("Total: RM 0.00");

        seatList = new JList<>(generateSeats());
        seatList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane seatScroll = new JScrollPane(seatList);

        JButton calculateButton = new JButton("Calculate Total");
        JButton nextButton = new JButton("Next");

        panel.add(new JLabel("Customer Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Select Artist:"));
        panel.add(artistBox);
        panel.add(new JLabel("Select Time:"));
        panel.add(timeBox);
        panel.add(new JLabel("Select Zone:"));
        panel.add(zoneBox);
        panel.add(new JLabel("Ticket Quantity:"));
        panel.add(quantitySpinner);
        panel.add(new JLabel("Select Seat(s):"));
        panel.add(seatScroll);
        panel.add(calculateButton);
        panel.add(totalLabel);
        panel.add(new JLabel());
        panel.add(nextButton);

        frame.add(panel);
        frame.setVisible(true);

        // Calculate total based on zone and quantity
        calculateButton.addActionListener(e -> {
            String zone = (String) zoneBox.getSelectedItem();
            int quantity = (Integer) quantitySpinner.getValue();

            if (zone.contains("VIP")) unitPrice = 300;
            else if (zone.contains("Standing")) unitPrice = 150;
            else unitPrice = 80;

            totalLabel.setText("Total: RM " + (unitPrice * quantity));
        });

        // Proceed to payment
        nextButton.addActionListener(e -> {
            String name = nameField.getText();
            String artist = (String) artistBox.getSelectedItem();
            String time = (String) timeBox.getSelectedItem();
            int quantity = (Integer) quantitySpinner.getValue();
            double totalAmount = unitPrice * quantity;

            java.util.List<String> selectedSeats = seatList.getSelectedValuesList();
            if (selectedSeats.size() != quantity) {
                JOptionPane.showMessageDialog(frame, "Please select exactly " + quantity + " seat(s).");
                return;
            }

            if (name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter your name.");
                return;
            }

            new PaymentWindow(name, artist, time, selectedSeats, quantity, totalAmount);
            frame.dispose();
        });
    }

    private String[] generateSeats() {
        String[] seats = new String[30];
        int index = 0;
        for (char row = 'A'; row <= 'C'; row++) {
            for (int num = 1; num <= 10; num++) {
                seats[index++] = row + String.valueOf(num);
            }
        }
        return seats;
    }
}

// Payment Window
class PaymentWindow {
    JFrame frame;

    public PaymentWindow(String name, String artist, String time, java.util.List<String> seats, int quantity, double totalAmount) {
        frame = new JFrame("Payment Method");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JRadioButton cardButton = new JRadioButton("Credit/Debit Card");
        JRadioButton cashButton = new JRadioButton("Cash");
        JRadioButton qrButton = new JRadioButton("DuitNow QR Code");

        ButtonGroup group = new ButtonGroup();
        group.add(cardButton);
        group.add(cashButton);
        group.add(qrButton);

        JButton payButton = new JButton("Proceed to Pay");

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Select Payment Method:"));
        panel.add(cardButton);
        panel.add(cashButton);
        panel.add(qrButton);
        panel.add(payButton);

        frame.add(panel);
        frame.setVisible(true);

        payButton.addActionListener(e -> {
            if (cardButton.isSelected()) {
                JTextField cardNumber = new JTextField();
                JTextField nameOnCard = new JTextField();
                JTextField expiry = new JTextField();
                JPasswordField cvv = new JPasswordField();

                Object[] fields = {
                    "Card Number:", cardNumber,
                    "Name on Card:", nameOnCard,
                    "Expiry (MM/YY):", expiry,
                    "CVV:", cvv
                };
                int result = JOptionPane.showConfirmDialog(null, fields, "Enter Card Details", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    showReceipt(name, artist, time, seats, quantity, totalAmount);
                }

            } else if (cashButton.isSelected()) {
                JOptionPane.showMessageDialog(frame, "Please proceed to the counter for cash payment.\nTotal: RM " + totalAmount);
            } else if (qrButton.isSelected()) {
                try {
                    ImageIcon qrImage = new ImageIcon(getClass().getResource("/resources/duitmap.png"));
                    JOptionPane.showMessageDialog(frame, new JLabel(qrImage), "Scan DuitNow QR", JOptionPane.PLAIN_MESSAGE);
                    showReceipt(name, artist, time, seats, quantity, totalAmount);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "QR code image not found.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a payment method.");
            }
        });
    }

    private void showReceipt(String name, String artist, String time, java.util.List<String> seats, int quantity, double totalAmount) {
        String message = "Payment Success!\nThank you, " + name +
                         "\nArtist: " + artist +
                         "\nTime: " + time +
                         "\nSeats: " + seats +
                         "\nQuantity: " + quantity +
                         "\nTotal: RM " + totalAmount;
        JOptionPane.showMessageDialog(frame, message);
    }
}
