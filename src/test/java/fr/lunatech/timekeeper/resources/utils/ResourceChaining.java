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

import io.vavr.*;
import io.vavr.collection.List;

/**
 * ResourceChaining proposes some methods that easing the way of writing dependencies between resources.
 * Those methods can be usefull to initialize some data set
 */
public class ResourceChaining {

    public static <FirstResource, ResourceResponse> ResourceResponse chain1(FirstResource p, Function1<FirstResource, ResourceResponse> m) {
        return m.apply(p);
    }

    public static <FirstResource, SecondResource, ResourceResponse> ResourceResponse chain2(FirstResource p, Function1<FirstResource, SecondResource> m, Function1<SecondResource, ResourceResponse> m2) {
        return m2.compose(m).apply(p);
    }

    public static <FirstResource, SecondResource, ThirdResource, ResourceResponse> ResourceResponse chain3(FirstResource p, Function1<FirstResource, SecondResource> m, Function1<SecondResource, ThirdResource> m2, Function1<ThirdResource, ResourceResponse> m3) {
        return m3.compose(m2.compose(m)).apply(p);
    }

    @SafeVarargs
    public static <ResourceDistributed, ResourceResponse> Tuple2<ResourceDistributed, List<ResourceResponse>> distribResource(ResourceDistributed p, Function1<ResourceDistributed, ResourceResponse>... distrib) {
        return Tuple.of(p, List.of(distrib).map(f -> f.apply(p)));
    }

    @SafeVarargs
    public static <ResourceDistributed1,ResourceDistributed2, ResourceResponse> Tuple3<ResourceDistributed1, ResourceDistributed2, List<ResourceResponse>> distribResource(ResourceDistributed1 p1, ResourceDistributed2 p2 , Function2<ResourceDistributed1,ResourceDistributed2, ResourceResponse>... distrib) {
        return Tuple.of(p1,p2, List.of(distrib).map(f -> f.apply(p1,p2)));
    }

    public static <NoReturn, Resource> RType chain(Resource p, Function1<Resource, NoReturn> m) {
        m.apply(p);
        return RType.NoReturn;
    }

}
