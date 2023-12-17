import { UserModel } from "@/model/UserModel";
import axios from "axios";
import { UserEmailModel } from "@/model/UserEmailModel";
import { encryptByPrivateKeyOfRSA, generateKeyPairOfRSA } from "@/common/RSAUtils";
import { decryptByAES, encryptByAES, generateSecretKeyOfAES } from '@/common/AESUtils';
import { VerificationCodeEmailModel } from "@/model/VerificationCodeEmailModel";
import { getAccessToken, removeGlobalUserInfo, setGlobalUserInfo } from "@/common/Server";

export async function signUp(password: string, nickname: string, userEmailList: UserEmailModel[]): Promise<void> {
  const { privateKey, publicKey } = await generateKeyPairOfRSA();
  const keyPairOfRSAForPassword = await generateKeyPairOfRSA();
  var { data: accessToken } = await axios.post<string>(`/sign_up`, {
    username: nickname,
    userEmailList: userEmailList,
    publicKeyOfRSA: publicKey,
    privateKeyOfRSA: await encryptByAES(await generateSecretKeyOfAES(password), privateKey),
    password: Buffer.from(JSON.stringify([await encryptByAES(await generateSecretKeyOfAES(password), keyPairOfRSAForPassword.privateKey), keyPairOfRSAForPassword.publicKey]), "utf8").toString("base64"),
  });
  await signOut();
  await setGlobalUserInfo(accessToken, password);
}

export async function sendVerificationCode(email: string) {
  return await axios.post<VerificationCodeEmailModel>("/email/send_verification_code", null, { params: { email } });
}

export async function signIn(userIdOrEmail: string, password: string): Promise<void> {
  await signOut();
  const { data: user } = await axios.post<UserModel>(`/sign_in/get_account`, null, { params: { userId: userIdOrEmail } });
  const { data: accessToken } = await axios.post<string>(`/sign_in`, null, {
    params: {
      userId: user.id,
      password: await encryptByPrivateKeyOfRSA(await decryptByAES(await generateSecretKeyOfAES(password), user.password), JSON.stringify({
        createDate: new Date(),
      })),
    }
  });
  await setGlobalUserInfo(accessToken, password);
}

export async function signOut() {
  if (isSignIn()) {
    try {
      await axios.post("/sign_out");
    } catch {
      // do nothing
    }
  }
  await removeGlobalUserInfo();
}

export function isSignIn() {
  return !!getAccessToken();
}