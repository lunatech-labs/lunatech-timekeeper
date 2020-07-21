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

describe("Test Login and Logout", () => {
    beforeEach(() => {
        cy.kcLogout();
        cy.kcLogin("alice");
        cy.visit("");
    });

    it("should authenticate Alice an Admin User and display Avatar", () => {
        cy.visit("http://localhost:3000/home");
        cy.get('.tk_Header_Profile').should('be.visible');
        cy.get(".tk_Header_Profile p").should("contain.text", "Alice Test");
        cy.get(".tk_Header_Profile p span").should("contain.text", "User");
        cy.get(':nth-child(1) > .ant-descriptions-item-label').should("contain.text", "Avatar");
        cy.get(':nth-child(4) > .ant-descriptions-item-content').should("contain.text", "ADMIN");
    });

    it("should logout Alice", () => {
        cy.visit("http://localhost:3000/home");
        cy.get('#avatarBtn').should('be.visible');
        cy.get('#avatarBtn').trigger('mouseover');

        cy.get("#logoutBtn").should('be.visible');
        cy.get("#logoutBtn").click();
        cy.url().should('contains', 'http://localhost:3000/login');
    });

    it("should redirect to login if not authenticated", () => {
        cy.kcLogout();
        cy.visit("http://localhost:3000/home");

        cy.url().should('contains', 'http://localhost:3000/login');
    });
});



