package fr.lunatech.timekeeper.services.responses;

import fr.lunatech.timekeeper.models.User;

import javax.validation.constraints.NotNull;

public final class Attendee {
    @NotNull
    private final Long userId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String picture;

    public Attendee(Long id, String firstName, String lastName, String email, String picture) {
        this.userId = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.picture = picture;
    }

    public static Attendee bind(@NotNull User user) {
        return new Attendee(user.id, user.firstName, user.lastName, user.email, user.picture);
    }

    public Long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPicture() {
        return picture;
    }

    @Override
    public String toString() {
        return "Attendee{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", picture='" + picture + '\'' +
                '}';
    }
}