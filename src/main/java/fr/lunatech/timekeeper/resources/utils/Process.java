package fr.lunatech.timekeeper.resources.utils;

@FunctionalInterface
public interface Process<T> {
    T run();
}
