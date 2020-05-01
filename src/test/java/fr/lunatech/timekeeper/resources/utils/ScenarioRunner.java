package fr.lunatech.timekeeper.resources.utils;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;

public class ScenarioRunner {



    public static <A, B, C, D> D createLinkedResource3(A p, Function1<A, B> m, Function1<B, C> m2, Function1<C, D> m3) {
        return m3.compose(m2.compose(m)).apply(p);
    }

    public static <A, B, C> C createLinkedResource2(A p, Function1<A, B> m, Function1<B, C> m2) {
        return m2.compose(m).apply(p);
    }

    public static <A, B> B createLinkedResource1(A p, Function1<A, B> m) {
        return m.apply(p);
    }

    @SafeVarargs
    public static <A, B> Tuple2<A, List<B>> distribResource(A p, Function1<A, B>... distrib) {
        return Tuple.of(p, List.of(distrib).map(f -> f.apply(p)));
    }

    public static <NoReturn, A> RType createWithLink0(A p, Function1<A, NoReturn> m) {
        m.apply(p);
        return RType.NoReturn;
    }

}
