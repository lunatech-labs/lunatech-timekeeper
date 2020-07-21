/*
 * Copyright 2020 Lunatech S.A.S
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
