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

package fr.lunatech.timekeeper.importcsv;


import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@Named("counterBean")
public class NamedBean {


     public NamedBean(){

    }

    public String hello(String entries) {

        System.out.println("got entries " + entries);

        return "ok";

    }
}
