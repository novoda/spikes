package com.novoda.movies;

public class Movie {

    public final String name;

    public Movie(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Movie movie = (Movie) o;

        return name.equals(movie.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
