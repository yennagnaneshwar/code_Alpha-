import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.*;

class Student {
    private final String name;
    private final double grade;

    public Student(String name, double grade) {
        this.name = name;
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public double getGrade() {
        return grade;
    }
}

public class StudentGradeTrackerGUI extends JFrame {
    private final JTextField nameField;
    private final JTextField gradeField;
    private final JTextArea resultArea;
    private final ArrayList<Student> students;

    public StudentGradeTrackerGUI() {
        super("Student Grade Tracker");

        students = new ArrayList<>();
        nameField = new JTextField(15);
        gradeField = new JTextField(5);
        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0 - Student Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Student Name:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(nameField, gbc);

        // Row 1 - Grade
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Grade (0 - 100):"), gbc);
        gbc.gridx = 1;
        inputPanel.add(gradeField, gbc);

        // Row 2 - Add & Show
        JButton addButton = new JButton("Add Student");
        addButton.addActionListener(e -> addStudent());
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(addButton, gbc);

        JButton showSummaryButton = new JButton("Show Summary");
        showSummaryButton.addActionListener(e -> updateResults(false));
        gbc.gridx = 1;
        inputPanel.add(showSummaryButton, gbc);

        // Row 3 - Delete & Sort
        JButton deleteButton = new JButton("Delete Student");
        deleteButton.addActionListener(e -> deleteStudent());
        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(deleteButton, gbc);

        JButton sortByGradeButton = new JButton("Sort by Grade");
        sortByGradeButton.addActionListener(e -> {
            students.sort(Comparator.comparingDouble(Student::getGrade).reversed());
            updateResults(false);
        });
        gbc.gridx = 1;
        inputPanel.add(sortByGradeButton, gbc);

        // Row 4 - Search & Export
        JButton searchButton = new JButton("Search by Name");
        searchButton.addActionListener(e -> searchStudent());
        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(searchButton, gbc);

        JButton exportButton = new JButton("Export Summary");
        exportButton.addActionListener(e -> exportSummary());
        gbc.gridx = 1;
        inputPanel.add(exportButton, gbc);

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        String gradeText = gradeField.getText().trim();

        if (name.isEmpty() || gradeText.isEmpty()) {
            showError("Both name and grade are required.");
            return;
        }

        try {
            double grade = Double.parseDouble(gradeText);
            if (grade < 0 || grade > 100) {
                showError("Grade must be between 0 and 100.");
                return;
            }
            students.add(new Student(name, grade));
            updateResults(true);
            nameField.setText("");
            gradeField.setText("");
        } catch (NumberFormatException e) {
            showError("Grade must be a numeric value.");
        }
    }

    private void deleteStudent() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("Enter the name of the student to delete.");
            return;
        }
        boolean removed = students.removeIf(s -> s.getName().equalsIgnoreCase(name));
        if (removed) {
            showMessage("Student deleted.");
            updateResults(false);
        } else {
            showError("Student not found.");
        }
    }

    private void searchStudent() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("Enter a name to search.");
            return;
        }

        for (Student s : students) {
            if (s.getName().equalsIgnoreCase(name)) {
                resultArea.setText("Search Result:\n" + String.format("%-20s %.2f\n", s.getName(), s.getGrade()));
                return;
            }
        }
        showError("Student not found.");
    }

    private void updateResults(boolean sortByName) {
        if (students.isEmpty()) {
            resultArea.setText("No student data available.");
            return;
        }

        if (sortByName) {
            students.sort(Comparator.comparing(Student::getName));
        }

        StringBuilder summary = new StringBuilder("Student Grades:\n");
        double total = 0;
        double highest = Double.MIN_VALUE;
        double lowest = Double.MAX_VALUE;

        for (Student s : students) {
            double grade = s.getGrade();
            total += grade;
            highest = Math.max(highest, grade);
            lowest = Math.min(lowest, grade);
            summary.append(String.format("%-20s %.2f\n", s.getName(), grade));
        }

        double average = total / students.size();

        summary.append("\n--- Summary ---\n");
        summary.append(String.format("Average Grade: %.2f\n", average));
        summary.append(String.format("Highest Grade: %.2f\n", highest));
        summary.append(String.format("Lowest Grade: %.2f\n", lowest));

        resultArea.setText(summary.toString());
    }

    private void exportSummary() {
        try (FileWriter writer = new FileWriter("student_summary.txt")) {
            writer.write(resultArea.getText());
            showMessage("Summary exported to student_summary.txt");
        } catch (IOException e) {
            showError("Error exporting file: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showMessage(String message) {
        resultArea.setText(message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentGradeTrackerGUI::new);
    }
}
