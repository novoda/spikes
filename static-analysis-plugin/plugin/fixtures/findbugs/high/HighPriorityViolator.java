public class HighPriorityViolator {

    public void impossibleCast() {
        final Object doubleValue = Double.valueOf(1.0);
        final Long value = (Long) doubleValue;
        System.out.println("   - " + value);
    }

}
