package novoda.com.sandbox.models;

public class User {
    private final Credentials credentials;
    private final boolean isAdmin;

    private User(UserBuilder builder) {
        this.credentials = builder.credentials;
        this.isAdmin = builder.adminStatus;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public static class UserBuilder {
        private final Credentials credentials;
        private boolean adminStatus = false;

        public UserBuilder(Credentials credentials) {
            this.credentials = credentials;
        }

        public UserBuilder withAdminStatus() {
            this.adminStatus = true;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (isAdmin != user.isAdmin) return false;
        return credentials.equals(user.credentials);

    }

    @Override
    public int hashCode() {
        int result = credentials.hashCode();
        result = 31 * result + (isAdmin ? 1 : 0);
        return result;
    }
}
