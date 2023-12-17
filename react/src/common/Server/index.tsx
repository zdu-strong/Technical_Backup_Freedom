import axios from 'axios';
import qs from 'qs';
import { UserModel } from '@/model/UserModel';
import { observable } from 'mobx-react-use-autorun';
import { concat, from, fromEvent, of, retry, switchMap } from 'rxjs';
import { decryptByPrivateKeyOfRSA, decryptByPublicKeyOfRSA, encryptByPrivateKeyOfRSA, encryptByPublicKeyOfRSA } from '@/common/RSAUtils';
import { TypedJSON } from 'typedjson';
import { runWoker } from '@/common/WebWorkerUtils';
import { decryptByAES, generateSecretKeyOfAES } from '@/common/AESUtils';
import { existsWindow } from '@/common/exists-window/exists-window';

let ServerAddress = 'http://127.0.0.1:8080';
let ClientAddress = 'http://127.0.0.1:3000';

if (existsWindow) {
  ServerAddress = window.location.protocol + "//" + window.location.hostname + ":" + 8080;

  if (process.env.NODE_ENV === "development") {
    if (process.env.REACT_APP_SERVER_PORT) {
      ServerAddress = window.location.protocol + "//" + window.location.hostname + ":" + process.env.REACT_APP_SERVER_PORT;
    }
  }
}

if (existsWindow) {
  ClientAddress = window.location.origin;
}

const serverUrl = new URL(ServerAddress);

export { ClientAddress };
export { ServerAddress };
export const WebSocketServerAddress = `${serverUrl.protocol.replace("http", "ws")}//${serverUrl.host}`;

axios.defaults.baseURL = ServerAddress;

axios.defaults.paramsSerializer = {
  serialize(params: Record<string, any>) {
    return qs.stringify(
      params,
      {
        arrayFormat: 'repeat',
      }
    );
  }
}

axios.interceptors.response.use(undefined, async (error) => {
  if (typeof error?.response?.data === "object") {
    for (const objectKey in error.response.data) {
      error[objectKey] = error.response.data[objectKey];
    }
  }

  throw error;
});

axios.interceptors.request.use((config) => {
  if (config.url?.startsWith("/") || config.url?.startsWith(ServerAddress + "/") || config.url === ServerAddress) {
    const accessToken = getAccessToken();
    if (accessToken) {
      config.headers!["Authorization"] = 'Bearer ' + accessToken
    }
  }
  return config;
})

export const GlobalUserInfo = observable({
  id: '',
  username: '',
  accessToken: '',
} as UserModel);

export async function setGlobalUserInfoWithPrivateKeyOfRSA(accessToken: string, privateKeyOfRSA: string) {
  const userInfo = new TypedJSON(UserModel).parse(await runWoker(new Worker(new URL('../../common/WebWorker/GetUserInfo/getUserInfo.worker', import.meta.url), { type: "module" }),
    {
      ServerAddress,
      accessToken
    }
  ))!;
  userInfo.privateKeyOfRSA = privateKeyOfRSA!;
  userInfo.accessToken = accessToken;
  GlobalUserInfo.id = userInfo!.id;
  GlobalUserInfo.username = userInfo!.username;
  GlobalUserInfo.accessToken = userInfo!.accessToken;
  GlobalUserInfo.encryptByPublicKeyOfRSA = async (data: string) => {
    return await encryptByPublicKeyOfRSA(GlobalUserInfo.publicKeyOfRSA, data);
  };
  GlobalUserInfo.decryptByPrivateKeyOfRSA = async (data: string) => {
    return await decryptByPrivateKeyOfRSA(GlobalUserInfo.privateKeyOfRSA, data);
  };
  GlobalUserInfo.encryptByPrivateKeyOfRSA = async (data: string) => {
    return await encryptByPrivateKeyOfRSA(GlobalUserInfo.privateKeyOfRSA, data);
  };
  GlobalUserInfo.decryptByPublicKeyOfRSA = async (data: string) => {
    return await decryptByPublicKeyOfRSA(GlobalUserInfo.publicKeyOfRSA!, data);
  };
  window.localStorage.setItem(keyOfGlobalUserInfoOfLocalStorage, JSON.stringify(GlobalUserInfo));
}

export async function setGlobalUserInfo(accessToken?: string, password?: string): Promise<void> {
  let userInfo: UserModel;
  if (accessToken) {
    userInfo = new TypedJSON(UserModel).parse(await runWoker(new Worker(new URL('../../common/WebWorker/GetUserInfo/getUserInfo.worker', import.meta.url), { type: "module" }),
      {
        ServerAddress,
        accessToken
      }
    ))!;
    userInfo.privateKeyOfRSA = await decryptByAES(await generateSecretKeyOfAES(password!), userInfo.privateKeyOfRSA);
    userInfo.accessToken = accessToken;
  } else {
    const jsonStringOfLocalStorage = window.localStorage.getItem(keyOfGlobalUserInfoOfLocalStorage);
    if (jsonStringOfLocalStorage) {
      userInfo = new TypedJSON(UserModel).parse(jsonStringOfLocalStorage)!;
    } else {
      removeGlobalUserInfo();
      return;
    }
  }

  GlobalUserInfo.id = userInfo!.id;
  GlobalUserInfo.username = userInfo!.username;
  GlobalUserInfo.accessToken = userInfo!.accessToken;
  GlobalUserInfo.encryptByPublicKeyOfRSA = async (data: string) => {
    return await encryptByPublicKeyOfRSA(GlobalUserInfo.publicKeyOfRSA, data);
  };
  GlobalUserInfo.decryptByPrivateKeyOfRSA = async (data: string) => {
    return await decryptByPrivateKeyOfRSA(GlobalUserInfo.privateKeyOfRSA, data);
  };
  GlobalUserInfo.encryptByPrivateKeyOfRSA = async (data: string) => {
    return await encryptByPrivateKeyOfRSA(GlobalUserInfo.privateKeyOfRSA, data);
  };
  GlobalUserInfo.decryptByPublicKeyOfRSA = async (data: string) => {
    return await decryptByPublicKeyOfRSA(GlobalUserInfo.publicKeyOfRSA!, data);
  };
  if (accessToken) {
    window.localStorage.setItem(keyOfGlobalUserInfoOfLocalStorage, JSON.stringify(GlobalUserInfo));
  }
}

export function getAccessToken() {
  if (GlobalUserInfo.accessToken) {
    return GlobalUserInfo.accessToken;
  }
  if (existsWindow) {
    const jsonStringOfLocalStorage = window.localStorage.getItem(keyOfGlobalUserInfoOfLocalStorage);
    if (jsonStringOfLocalStorage) {
      return new TypedJSON(UserModel).parse(jsonStringOfLocalStorage)!.accessToken;
    }
  }
  return '';
}

export async function removeGlobalUserInfo() {
  GlobalUserInfo.id = '';
  GlobalUserInfo.username = '';
  GlobalUserInfo.accessToken = '';
  GlobalUserInfo.encryptByPublicKeyOfRSA = undefined as any;
  GlobalUserInfo.decryptByPrivateKeyOfRSA = undefined as any;
  GlobalUserInfo.encryptByPrivateKeyOfRSA = undefined as any;
  GlobalUserInfo.decryptByPublicKeyOfRSA = undefined as any;
  if (window.localStorage.getItem(keyOfGlobalUserInfoOfLocalStorage)) {
    window.localStorage.removeItem(keyOfGlobalUserInfoOfLocalStorage);
  }
}

const keyOfGlobalUserInfoOfLocalStorage = 'GlobalUserInfo-c12e6be9-e969-4a54-b5d4-b451755bf49a';

function main() {
  if (!existsWindow) {
    return;
  }
  setGlobalUserInfo();
  concat(of(null), fromEvent(window, "storage")).pipe(
    switchMap(() => {
      return from(setGlobalUserInfo());
    }),
    retry(),
  ).subscribe();
}

export default main()

