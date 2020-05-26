package fr.lunatech.timekeeper.timeutils;

import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.UserEvent;

import java.time.LocalDate;
import java.util.List;

/**
 * Purpose of this entity is to store time entries and special events like public holidays, for a User.
 */
public class Week {
    public LocalDate firstDayOfWeek;

    public User owner;

    public List<UserEvent> userEvents;

}
