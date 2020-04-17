package fr.lunatech.timekeeper.resources.utils;

@FunctionalInterface
public interface ItemScenario {
    <P, R> R run(P param);
}
