package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class TimeSheetService {

    public List<TimeSheetResponse> findAllActivesForUser(AuthenticationContext ctx){
        return streamAll(ctx, TimeSheetResponse::bind, Collectors.toList());
    }

    <R extends Collection<TimeSheetResponse>> R streamAll(
            AuthenticationContext ctx,
            Function<TimeSheet, TimeSheetResponse> bind,
            Collector<TimeSheetResponse, ?, R> collector
    ) {
        try (final Stream<TimeSheet> ts = TimeSheet.stream("user_id = ?1", ctx.getUserId())) {
            return ts
                    .map(bind)
                    .collect(collector);
        }
    }
}
