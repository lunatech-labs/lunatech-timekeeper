package fr.lunatech.timekeeper.timeutils;

import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.TimeSheet;


public class TimeSheetBuilder {

    public static TimeSheet build(User owner, Project project) {

        TimeSheet sheet = new TimeSheet();
        sheet.owner=owner;
        sheet.project=project;

        return sheet;
    }
}
