import axios from 'axios';
import qs from 'qs';
import { UserModel } from '@/model/UserModel';
import { observable } from 'mobx-react-use-autorun';
import { from, fromEvent, retry, switchMap } from 'rxjs';
import { decryptByPrivateKeyOfRSA, decryptByPublicKeyOfRSA, encryptByPrivateKeyOfRSA, encryptByPublicKeyOfRSA } from '@/common/RSAUtils';
import { TypedJSON } from 'typedjson';
import { existsWindow } from '@/common/exists-window/exists-window';
import { UserEmailModel } from '@/model/UserEmailModel';

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
  privateKeyOfRSA: '',
  publicKeyOfRSA: '',
  userEmailList: [] as UserEmailModel[],
} as UserModel);

export async function setGlobalUserInfo(user?: UserModel): Promise<void> {
  const hasParam = !!user;
  if (!hasParam) {
    const jsonStringOfLocalStorage = window.localStorage.getItem(keyOfGlobalUserInfoOfLocalStorage);
    if (jsonStringOfLocalStorage) {
      user = new TypedJSON(UserModel).parse(jsonStringOfLocalStorage)!;
    } else {
      removeGlobalUserInfo();
      return;
    }
  }

  GlobalUserInfo.id = user!.id;
  GlobalUserInfo.username = user!.username;
  GlobalUserInfo.accessToken = user!.accessToken;
  GlobalUserInfo.publicKeyOfRSA = user!.publicKeyOfRSA;
  GlobalUserInfo.privateKeyOfRSA = user!.privateKeyOfRSA;
  GlobalUserInfo.userEmailList = user!.userEmailList;
  GlobalUserInfo.encryptByPublicKeyOfRSA = async (data: string) => {
    return await encryptByPublicKeyOfRSA(data, GlobalUserInfo.publicKeyOfRSA);
  };
  GlobalUserInfo.decryptByPrivateKeyOfRSA = async (data: string) => {
    return await decryptByPrivateKeyOfRSA(data, GlobalUserInfo.privateKeyOfRSA);
  };
  GlobalUserInfo.encryptByPrivateKeyOfRSA = async (data: string) => {
    return await encryptByPrivateKeyOfRSA(data, GlobalUserInfo.privateKeyOfRSA);
  };
  GlobalUserInfo.decryptByPublicKeyOfRSA = async (data: string) => {
    return await decryptByPublicKeyOfRSA(data, GlobalUserInfo.publicKeyOfRSA!);
  };
  if (hasParam) {
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
  GlobalUserInfo.privateKeyOfRSA = '';
  GlobalUserInfo.publicKeyOfRSA = '';
  GlobalUserInfo.userEmailList = [] as UserEmailModel[];
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
  fromEvent(window, "storage").pipe(
    switchMap(() => {
      return from(setGlobalUserInfo());
    }),
    retry(),
  ).subscribe();
}

export default main()

