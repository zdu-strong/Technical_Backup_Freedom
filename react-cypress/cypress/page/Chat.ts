export default {
  signOutButton: () => cy.xpath("//Button[.='Sign out']", { timeout: 60000 }),
  MessageContentInput: () => cy.xpath(`//fieldset[contains(., 'Message content')]/../textarea[@rows]`),
  Message: (message: string) => cy.xpath(`//div[contains(@style, 'break-word')][contains(., '${message}')]`, { timeout: 120000 }),
  RecallMessageButton: (message: string) => cy.xpath(`//div[contains(@style, 'break-word')][contains(., '${message}')]/..//button[contains(., 'Withdrawn')]`, { timeout: 60000 }),
}