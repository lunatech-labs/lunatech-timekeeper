/*
 * Copyright 2020 Lunatech Labs
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

describe('Welcome TimeKeeper', () => {
    describe("Keycloak Login", () => {
        beforeEach(() => {
            cy.kcLogout();
            cy.kcLogin("alice");
            cy.visit("http://localhost:3000/");
        });
    });

    it('visits the app', () => {
        cy.visit("http://localhost:3000/");

        //
        cy.get('#tk_RightContent')
            .should('have.class', 'success')
            .should('have.text', 'Home')
            .should('contain', 'Home')
            .should('have.html', 'Home')

        cy.get('#tk_RightContent').click();
        cy.get('.ant-descriptions-row:nth-child(2) > .ant-descriptions-item-content').click();

    })
})