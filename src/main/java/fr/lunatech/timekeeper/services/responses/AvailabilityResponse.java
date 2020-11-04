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

package fr.lunatech.timekeeper.services.responses;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class AvailabilityResponse {
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    @NotNull
    private List<UserResponse> available;
    @NotNull
    private List<UserResponse> unavailable;

    public AvailabilityResponse(
            @NotNull LocalDateTime start,
            @NotNull LocalDateTime end,
            @NotNull List<UserResponse> available,
            @NotNull List<UserResponse> unavailable
    ) {
        this.start = start;
        this.end = end;
        this.available = available;
        this.unavailable = unavailable;
    }

    public LocalDateTime getStart() { return this.start; }
    public LocalDateTime getEnd() { return this.end; }

    public List<UserResponse> getAvailable() {
        return available;
    }

    public List<UserResponse> getUnavailable() {
        return unavailable;
    }
}
