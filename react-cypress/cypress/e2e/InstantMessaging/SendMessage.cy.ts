import page from '../../page'
import { v1 } from 'uuid'
import * as action from '../../action'

it('', () => {
  page.Chat.MessageContentInput().type(message).type("{enter}")
  page.Chat.Message(message).should("exist")
})

before(() => {
  cy.visit("/sign_up")
  action.signUp(email, password)
})

const message = `Hello, World! ${v1()}`
const email = `${v1()}zdu.strong@gmail.com`
const password = 'Hello, World!'