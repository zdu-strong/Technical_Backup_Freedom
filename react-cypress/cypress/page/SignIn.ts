export default {
  username: () => cy.xpath("//legend/span[.='Account ID']/../../..//input", { timeout: 180000 }),
  password: () => cy.xpath(`//textarea[@rows='6']`),
  signInButton: () => cy.xpath(`//button[.='SignIn']`),
  showPasswordButton: () => cy.xpath(`//button[.='The password has been filled in, click Edit']`),
}