package fr.lunatech.timekeeper.resources.utils;

import io.vavr.*;
import io.vavr.collection.List;

public class ScenarioRunner {

    public static <At, B> B chain1(At p, Function1<At, B> m) {
        return m.apply(p);
    }

    public static <A, B, C> C chain2(A p, Function1<A, B> m, Function1<B, C> m2) {
        return m2.compose(m).apply(p);
    }

    public static <A, B, C, D> D chain3(A p, Function1<A, B> m, Function1<B, C> m2, Function1<C, D> m3) {
        return m3.compose(m2.compose(m)).apply(p);
    }

    @SafeVarargs
    public static <A, B> Tuple2<A, List<B>> distribResource(A p, Function1<A, B>... distrib) {
        return Tuple.of(p, List.of(distrib).map(f -> f.apply(p)));
    }

    @SafeVarargs
    public static <A1,A2, B> Tuple3<A1, A2, List<B>> distribResource(A1 p1, A2 p2 , Function2<A1,A2, B>... distrib) {
        return Tuple.of(p1,p2, List.of(distrib).map(f -> f.apply(p1,p2)));
    }

    public static <NoReturn, A> RType chain(A p, Function1<A, NoReturn> m) {
        m.apply(p);
        return RType.NoReturn;
    }

}
