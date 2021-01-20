import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String text = scanner.nextLine();
        SuffixTree st = new SuffixTree(text);
        System.out.println(st);
        System.out.print("Number of Distinct Substrings: ");
        System.out.println(st.getNDistinctSubstrings());
    }
}