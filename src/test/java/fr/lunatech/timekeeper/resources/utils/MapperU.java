package fr.lunatech.timekeeper.resources.utils;

@FunctionalInterface
public interface MapperU<P> {

    <P> void apply(P param);
}
