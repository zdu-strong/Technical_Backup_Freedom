import { FriendshipModel } from "@/model/FriendshipModel";
import { FriendshipPaginationModel } from "@/model/FriendshipPaginationModel";
import axios from "axios";
import { v1 } from "uuid";
import { getUserById } from "@/api/User";
import { generateSecretKeyOfAES } from "@/common/AESUtils";
import { GlobalUserInfo } from "@/common/Server";

export async function addToFriendList(friendId: string) {
  const keyOfAES = await generateSecretKeyOfAES();
  const aesOfUser = await GlobalUserInfo.encryptByPublicKeyOfRSA(await GlobalUserInfo.encryptByPrivateKeyOfRSA(keyOfAES));
  const friend = await getUserById(friendId);
  const aesOfFriend = friend.data.encryptByPublicKeyOfRSA(await GlobalUserInfo.encryptByPrivateKeyOfRSA(keyOfAES));
  await axios.post("/friendship/add_to_friend_list", null, { params: { friendId, aesOfUser, aesOfFriend } });
}

export async function deleteFromFriendList(friendId: string) {
  await axios.delete("/friendship/delete_from_friend_list", { params: { friendId } })
}

export async function deleteFromBlacklist(friendId: string) {
  await axios.delete("/friendship/delete_from_black_list", { params: { friendId } })
}

export async function getFriendList() {
  const response = await axios.get<FriendshipPaginationModel>("/friendship/get_friend_list", { params: { pageNum: 1, pageSize: 100 } });
  for (const friendship of response.data.list) {
    if (!friendship.id) {
      friendship.id = v1()
    }
  }
  return response;
}

export async function getStrangerList() {
  const response = await axios.get<FriendshipPaginationModel>("/friendship/get_stranger_list", { params: { pageNum: 1, pageSize: 100 } });
  for (const friendship of response.data.list) {
    if (!friendship.id) {
      friendship.id = v1()
    }
  }
  return response;
}

export async function getFriendship(friendId: string) {
  return await axios.get<FriendshipModel>("/friendship/get_friendship", { params: { friendId } });
}


