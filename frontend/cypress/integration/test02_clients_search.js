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

describe("02 Client list and search", () => {

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

    it("should return an empty list of clients", () => {
        cy.get('#linkClients').should('be.visible');
        cy.get('#linkClients').trigger('mouseover').click();
        cy.url().should('include', 'http://localhost:3000/clients');
        cy.get('#btnAddNewClient').should('be.visible');
        cy.get('.tk_MainContent_HeaderLeft').should("contain.text", "List of clients");
        cy.get('.tk_SubHeader').should("contain.text", "No client");
        // The Input box ID is not set thus we need to use data-cy here
        cy.get('[data-cy=searchClientBox]').should('be.visible');
    });

    it("should create a new client", () => {
        cy.visit("http://localhost:3000/clients");
        cy.url().should('include', 'http://localhost:3000/clients');
        cy.get('#btnAddNewClient').should('be.visible');
        cy.get('#btnAddNewClient').click();
        cy.url().should('include', 'http://localhost:3000/clients/new');

        cy.get('.tk_MainContent_HeaderLeft').should("contain.text", "Create a new client");
        cy.get('label.ant-form-item-required').should('be.visible');

        cy.get('#name')
            .type('Client for testing')
            .should('have.value', 'Client for testing');

        cy.get('#description')
            .type('This client was created by Cypress for testing')
            .should('have.value', 'This client was created by Cypress for testing');

        cy.get('#btnSubmitNewClient').click();
        cy.url().should('include', 'http://localhost:3000/clients');
        cy.get('.tk_SubHeader').should("contain.text", "1 client");

    });

    it("should return an empty list of project for a new client", () => {
        cy.visit("http://localhost:3000/clients");
        cy.url().should('include', 'http://localhost:3000/clients');
        cy.get('.tk_SubHeader').should("contain.text", "1 client");
        cy.get('#tk_ProjectNoCollapse > div > div > div:nth-child(2)').should("contain.text", "No project");

    });

    it("should not create a client with the same name", () => {
        cy.visit("http://localhost:3000/clients");
        cy.url().should('include', 'http://localhost:3000/clients');
        cy.get('#btnAddNewClient').should('be.visible');
        cy.get('#btnAddNewClient').click();
        cy.url().should('include', 'http://localhost:3000/clients/new');

        cy.get('.tk_MainContent_HeaderLeft').should("contain.text", "Create a new client");
        cy.get('label.ant-form-item-required').should('be.visible');

        cy.get('#name')
            .type('Client for testing')
            .should('have.value', 'Client for testing');

        cy.get('#description')
            .type('This client was created by Cypress for testing')
            .should('have.value', 'This client was created by Cypress for testing');

        cy.get('#btnSubmitNewClient').click();
        cy.url().should('include', 'http://localhost:3000/clients/new');

        cy.get('.ant-alert').should('contain.text', 'Unable to save the new Client');
    });

    it("should search and find a client by name", () => {
        cy.visit("http://localhost:3000/clients");
        cy.url().should('include', 'http://localhost:3000/clients');
        cy.get('[data-cy=searchClientBox]').should('be.visible');
        cy.get('[data-cy=searchClientBox]').clear({force: true});
        cy.get('[data-cy=searchClientBox]').type('for test', {force: true});
        cy.get('[class*=tk_Card_ClientTitle]').should('have.text', "Client for testing");

        cy.get('[data-cy=searchClientBox]').clear({force: true});
        cy.get('[data-cy=searchClientBox]').type('yoyo', {force: true});
        cy.get('[class*=tk_Card_ClientTitle]').should('not.exist');
        cy.get('.ant-empty').should('be.visible');

    });

});



