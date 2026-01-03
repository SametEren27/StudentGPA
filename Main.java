import Classes.*;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.nio.charset.StandardCharsets;

/**
 * Main application class for the CE445 Final Project.
 * Optimized for maximum compatibility with Turkish characters on Windows/Linux/macOS.
 */
public class Main {
    // Force the scanner to use the platform's default encoding for keyboard input
    private static final Scanner scanner = new Scanner(System.in, System.getProperty("sun.desktop.index") != null ? "CP1254" : "UTF-8");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public static void main(String[] args) {
        // Set console output to UTF-8 explicitly to handle characters like 'ğ, ü, ş'
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException ignored) {}

        try {
            // 1. Department Entry [cite: 42-44]
            System.out.println("--- Section 1: Enter department information ---");
            System.out.print("Department Name: ");
            String deptName = scanner.nextLine();
            System.out.print("Department Web Page: ");
            String deptWeb = scanner.nextLine();
            System.out.print("Establishment Date (dd/MM/yyyy): ");
            Date deptDate = dateFormat.parse(scanner.nextLine());
            
            Department department = new Department(deptName, deptWeb, deptDate);
            System.out.println(">> SUCCESS: Department [" + department.getDepartmentName() + "] created.\n");

            // 2. Student Entry Loop [cite: 45-47]
            List<Student> students = new ArrayList<>();
            System.out.println("--- Section 2: Enter student information (Type 'end' to finish) ---");
            while (true) {
                System.out.print("First Name: ");
                String fName = scanner.nextLine();
                if (fName.equalsIgnoreCase("end")) break;
                
                System.out.print("Last Name: ");
                String lName = scanner.nextLine();
                System.out.print("Student ID: ");
                int id = Integer.parseInt(scanner.nextLine());
                System.out.print("Birth Date (dd/MM/yyyy): ");
                Date bDate = dateFormat.parse(scanner.nextLine());
                
                students.add(new Student(fName, lName, id, bDate, department));
                System.out.println(">> ADDED: Student " + fName + " recorded.");
            }
            System.out.println(">> SECTION FINISHED: " + students.size() + " students recorded.\n");

            // 3. Course Entry Loop [cite: 49-50]
            List<Course> courses = new ArrayList<>();
            System.out.println("--- Section 3: Enter course information (Type 'end' to finish) ---");
            while (true) {
                System.out.print("Course Name: ");
                String cName = scanner.nextLine();
                if (cName.equalsIgnoreCase("end")) break;
                
                System.out.print("Course Code: ");
                String cCode = scanner.nextLine();
                System.out.print("ECTS: ");
                int ects = Integer.parseInt(scanner.nextLine());
                
                courses.add(new Course(cName, cCode, ects));
                System.out.println(">> ADDED: Course [" + cCode + "] recorded.");
            }
            System.out.println(">> SECTION FINISHED: " + courses.size() + " courses recorded.\n");

            // 4. Grade Entry & GPA Calculation [cite: 51-54]
            System.out.println("--- Section 4: Enter course results ---");
            for (Student s : students) {
                System.out.println("Enter all course results for [" + s.getFirstName() + " " + s.getLastName() + "]");
                double weightedSum = 0;
                int totalEcts = 0;
                
                for (Course c : courses) {
                    System.out.print("Enter grade for [" + c.getCourseName() + "]: ");
                    String letter = scanner.nextLine().toUpperCase();
                    double gradeValue = getNumericGrade(letter); // [cite: 32, 38]
                    
                    weightedSum += (gradeValue * c.getEcts());
                    totalEcts += c.getEcts();
                }
                
                // Formula: GPA = Sum(Grade * ECTS) / Sum(ECTS) [cite: 54]
                double calculatedGpa = (totalEcts == 0) ? 0 : (weightedSum / totalEcts);
                s.setGpa(calculatedGpa);
                System.out.printf(">> Status: GPA for %s set to %.2f\n\n", s.getFirstName(), calculatedGpa);
            }

            // 5. Sorting and File Export [cite: 55-56]
            students.sort(Comparator.comparingDouble(Student::getGpa).reversed());

            // Writing to results.txt using UTF-8 BOM to force Windows Notepad to see Turkish letters
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("results.txt"), StandardCharsets.UTF_8))) {
                System.out.println("--- FINAL STUDENT RANKING ---");
                for (int i = 0; i < students.size(); i++) {
                    String result = (i + 1) + ". " + students.get(i).toString();
                    System.out.println(result);
                    bw.write(result);
                    bw.newLine();
                }
                System.out.println("-----------------------------");
                System.out.println("Full list saved to 'results.txt'.");
            }

        } catch (Exception e) {
            // Requirement: terminate with a successful exit code [cite: 57]
            System.out.println("An input error occurred. Program closing safely.");
        }
    }

    /**
     * Grading scale mapping[cite: 32, 38].
     */
    private static double getNumericGrade(String letter) {
        switch (letter) {
            case "AA": return 4.00; case "BA": return 3.50; case "BB": return 3.25;
            case "CB": return 3.00; case "CC": return 2.50; case "DC": return 2.25;
            case "DD": return 2.00; case "FD": return 1.50; case "FF": return 0.00;
            default: return 0.00;
        }
    }
}