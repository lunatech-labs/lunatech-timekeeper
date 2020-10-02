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
ALTER TABLE event_template
    ADD COLUMN creator_id bigint
        constraint fk_user_events_to_users
            references users
            on delete set null;

ALTER TABLE user_events
    ADD COLUMN creator_id bigint
        constraint fk_user_events_to_users_owner
            references users
            on delete set null;

UPDATE user_events
SET creator_id = 1;