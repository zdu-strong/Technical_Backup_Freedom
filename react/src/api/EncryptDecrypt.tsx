import axios from "axios";

export async function getKeyOfRSAPublicKey() {
  const response = await axios.get<string>("/encrypt_decrypt/rsa/public_key");
  return response;
}