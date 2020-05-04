package fr.lunatech.timekeeper.resources.utils;

/**
 * RType enum aims to indicated that no reference is returned.
 * We need this type because it is not possible with generic type's parameter to express this.
 *
 *  Sample usage : public static <NoReturn, A> RType sample(A p, Function1<A, NoReturn> m) {
 *      m.apply(p);
 *      return RType.NoReturn;
 *  }
 */
public enum RType {
    NoReturn
}
