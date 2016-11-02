public class Priority2Violator {
    public void foo(String string) {
        // should be &&
        if (string != null || !string.equals("")) {
            System.out.println(string);
        }
        System.out.println("bar");
    }
}
