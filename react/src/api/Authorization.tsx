import { UserModel } from "@/model/UserModel";
import axios from "axios";
import { UserEmailModel } from "@/model/UserEmailModel";
import { generateKeyPairOfRSA } from "@/common/RSAUtils";
import { decryptByAES, encryptByAES, generateSecretKeyOfAES } from '@/common/AESUtils';
import { VerificationCodeEmailModel } from "@/model/VerificationCodeEmailModel";
import { getAccessToken, removeGlobalUserInfo, setGlobalUserInfo } from "@/common/Server";
import { TypedJSON } from "typedjson";
import CryptoJS from 'crypto-js';

export async function signUp(password: string, nickname: string, userEmailList: UserEmailModel[]): Promise<void> {
  const secretKeyOfAESPromise = generateSecretKeyOfAES(password);
  const keyPairPromise = generateKeyPairOfRSA();
  const secretKeyOfAESOfPasswordPromise = generateSecretKeyOfAES(CryptoJS.SHA3(password).toString(CryptoJS.enc.Base64));
  await Promise.all([secretKeyOfAESPromise, keyPairPromise, secretKeyOfAESOfPasswordPromise]);
  const { privateKey, publicKey } = await keyPairPromise;
  const secretKeyOfAES = await secretKeyOfAESPromise;
  const secretKeyOfAESOfPassword = await secretKeyOfAESOfPasswordPromise;
  const privateKeyOfRSAPromise = encryptByAES(privateKey, secretKeyOfAES);
  const passwordParamPromise = encryptByAES(secretKeyOfAESOfPassword, secretKeyOfAESOfPassword);
  await Promise.all([privateKeyOfRSAPromise, passwordParamPromise]);
  let { data: user } = await axios.post<UserModel>(`/sign_up`, {
    username: nickname,
    userEmailList: userEmailList,
    publicKeyOfRSA: publicKey,
    privateKeyOfRSA: await privateKeyOfRSAPromise,
    password: await passwordParamPromise,
  });
  user = new TypedJSON(UserModel).parse(user)!;
  user.privateKeyOfRSA = privateKey;
  await signOut();
  await setGlobalUserInfo(user);
}

export async function sendVerificationCode(email: string) {
  return await axios.post<VerificationCodeEmailModel>("/email/send_verification_code", null, { params: { email } });
}

export async function signIn(userIdOrEmail: string, password: string): Promise<void> {
  await signOut();
  const secretKeyOfAESPromise = generateSecretKeyOfAES(password);
  const secretKeyOfAESOfPasswordPromise = generateSecretKeyOfAES(CryptoJS.SHA3(password).toString(CryptoJS.enc.Base64));
  await Promise.all([secretKeyOfAESPromise, secretKeyOfAESOfPasswordPromise]);
  let { data: user } = await axios.post<UserModel>(`/sign_in`, null, {
    params: {
      userId: userIdOrEmail,
      password: await secretKeyOfAESOfPasswordPromise,
    }
  });
  user = new TypedJSON(UserModel).parse(user)!;
  user.privateKeyOfRSA = await decryptByAES(user.privateKeyOfRSA, await secretKeyOfAESPromise);
  await setGlobalUserInfo(user);
}

export async function signOut() {
  if (await isSignIn()) {
    try {
      await axios.post("/sign_out");
    } catch {
      // do nothing
    }
    await removeGlobalUserInfo();
  }
}

export async function isSignIn() {
  if (!getAccessToken()) {
    return false;
  }
  try {
    await axios.get("/get_user_info");
  } catch (e) {
    if (e && (e as any).status === 401) {
      await removeGlobalUserInfo();
      return false;
    }
  }
  return true;
}