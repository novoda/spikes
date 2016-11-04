public class Priority3Violator {
    public void foo() {
        if (true) {        // fixed conditional, not recommended
            System.out.println("bar");
        }
    }
}
