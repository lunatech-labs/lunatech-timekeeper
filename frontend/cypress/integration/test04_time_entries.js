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

describe("04 Time entries", () => {

    /* Set-up the timekeeper database to a fixed version before the test */
    before(() => {
        cy.exec("node_modules/db-migrate/bin/db-migrate reset").its('stdout').should('contain', '[INFO]');
        cy.exec("node_modules/db-migrate/bin/db-migrate up").its('stdout').should('contain', '[INFO]');
    });

    beforeEach(() => {
        cy.kcLogout();
        cy.kcLogin("alice");
        /* We reload the home page between each test to ensure that the state is set correctly */
        cy.visit("http://localhost:3000/home");
        cy.get('.tk_Header_Profile').should('be.visible');
    });
    //
    // it("should return an empty list of timesheets", () => {
    //     cy.get('#linkEntries').should('be.visible');
    //     cy.get('#linkEntries').trigger('mouseover').click();
    //     cy.url().should('include', 'http://localhost:3000/time_entries');
    //     cy.get('#title').should('be.visible');
    //     cy.get('#title').should("contain.text", "Time entries");
    //
    //     cy.get('.ant-collapse-header').click();
    //     cy.contains('Time sheets list').should('be.visible');
    //     cy.get('.ant-empty-img-simple').should('be.visible');
    //     cy.get('.ant-collapse-header').click();
    //     cy.get('.ant-empty-img-simple').should('not.be.visible');
    // });
    //
    // it("should navigate to previous week", () => {
    //     cy.visit("http://localhost:3000/time_entries");
    //     cy.url().should('include', 'http://localhost:3000/time_entries');
    //
    //     cy.scrollTo('top');
    //
    //     cy.get('[data-cy=btnWeekPrevious]').should('be.visible');
    //     cy.scrollTo('top');
    //
    //     cy.get('[data-cy=btnWeekPrevious]').click({force: true});
    //     cy.url().should('include', '?weekNumber');
    // });

    it("should display bank holiday", () => {
        cy.visit("http://localhost:3000/time_entries?weekNumber=29");
        cy.url().should('include', 'http://localhost:3000/time_entries?weekNumber=29');
        cy.scrollTo('top');
        cy.get('[data-cy=weekNavigator] > p').should('contain.text','13 - 19 Jul 2020');

    });


    // it("should create a new client", () => {
    //     cy.visit("http://localhost:3000/clients");
    //     cy.url().should('include', 'http://localhost:3000/clients');
    //     cy.get('#btnAddNewClient').should('be.visible');
    //     cy.get('#btnAddNewClient').click();
    //     cy.url().should('include', 'http://localhost:3000/clients/new');
    //
    //     cy.get('.tk_MainContent_HeaderLeft').should("contain.text", "Create a new client");
    //     cy.get('label.ant-form-item-required').should('be.visible');
    //
    //     cy.get('#name')
    //         .type('Client for testing')
    //         .should('have.value', 'Client for testing');
    //
    //     cy.get('#description')
    //         .type('This client was created by Cypress for testing')
    //         .should('have.value', 'This client was created by Cypress for testing');
    //
    //     cy.get('#btnSubmitNewClient').click();
    //     cy.url().should('include', 'http://localhost:3000/clients');
    //     cy.get('.tk_SubHeader').should("contain.text", "1 client");
    //
    // });
    //
    // it("should load the edit page for a client", () => {
    //     cy.visit("http://localhost:3000/clients");
    //     cy.url().should('include', 'http://localhost:3000/clients');
    //     cy.get('[class*=tk_Card_ClientTitle]').should('have.text', "Client for testing");
    //     cy.get('[data-cy=editProject]').should('be.visible');
    //     cy.get('[data-cy=editProject]').click();
    //     cy.url().should('include', 'http://localhost:3000/clients/1/edit');
    // });
    //
    //  it("should have a correct Edit page for client 1", () => {
    //      cy.visit("http://localhost:3000/clients/1/edit");
    //      cy.url().should('include', 'http://localhost:3000/clients/1/edit');
    //      cy.get(':nth-child(3) > .ant-breadcrumb-link > a').should('be.visible');
    //      cy.get('#title').should('have.text','Edit a client');
    //      cy.get('.ant-spin-dot').should('not.exist');
    //      cy.get('#name').should('be.visible');
    //      cy.get('#name').should('have.value','Client for testing');
    //      cy.get('#description').should('have.value','This client was created by Cypress for testing');
    //      cy.get('#btnCancelEditClient').should('be.visible');
    //      cy.get('#btnSubmitEditClient').should('be.visible');
    //  });
    //
    // it("should update the client Description", () => {
    //     cy.visit("http://localhost:3000/clients/1/edit");
    //     cy.url().should('include', 'http://localhost:3000/clients/1/edit');
    //     cy.get('#description').should('have.value','This client was created by Cypress for testing');
    //     cy.get('#description').clear({force: true});
    //     cy.get('#description').type('Client updated form a Cypress test');
    //     cy.get('#btnSubmitEditClient').click();
    //     cy.url().should('include', 'http://localhost:3000/clients');
    //     cy.get('.ant-card-meta-description').should('have.text', 'Client updated form a Cypress test');
    // });
});



