package fr.lunatech.timekeeper.resources.utils;

@FunctionalInterface
public interface Mapper<P,R> {

    <P, R> R apply(P param);
}
