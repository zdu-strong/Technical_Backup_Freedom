import { PaginationModel } from "@/model";
import linq from "linq";
import { v1 } from "uuid";
import { subMilliseconds } from "date-fns";
import { Database } from "@/common/database";
import path from 'path'
import { Directory, Filesystem } from '@capacitor/filesystem'

export async function getStorageSpaceListByPagination(pageNum: number, pageSize: number) {
  const db = new Database();
  let stream = linq.from(await db.StorageSpaceList.toArray());
  stream = stream.orderBy(s => s.createDate).thenBy(s => s.id);
  return PaginationModel(pageNum, pageSize, stream);
}

export async function isUsed(folderName: string) {
  const db = new Database();

  /* Get folder name from file path */
  const folderNameOfRelative = await getFolderNameBaseOnBaseFolderPath(folderName);

  /* Has been used */
  if (await isUsedByProgramData(folderNameOfRelative)) {
    const stream = linq.from(await db.StorageSpaceList.toArray());
    const list = stream.where(s => s.folderName === folderNameOfRelative).select(s => s.id).toArray();
    db.StorageSpaceList.bulkDelete(list);
    return true;
  }

  /* Save data to database */
  await createStorageSpaceEntityIfNotExist(folderNameOfRelative);

  /* Check if it is used */
  const tempFileValidTime = 24 * 60 * 60 * 1000;
  const expiredDate = subMilliseconds(new Date(), 0 - tempFileValidTime);

  const isUsed = linq
    .from(await db.StorageSpaceList.toArray())
    .where((s) => s.folderName === folderNameOfRelative)
    .groupBy(s => s.folderName)
    .where(s =>
      s.where(m => m.updateDate.getTime() < expiredDate.getTime())
        .where(() => s.all(m => m.updateDate.getTime() < expiredDate.getTime()))
        .any()
    )
    .any();
  return isUsed;
}

export async function deleteFolder(folderName: string): Promise<void> {
  const db = new Database();

  /* Get folder name from file path */
  const folderNameOfRelative = await getFolderNameBaseOnBaseFolderPath(
    folderName
  );

  /* Do not delete when in use */
  if (await isUsed(folderNameOfRelative)) {
    return;
  }

  /* Delete from disk */
  await Filesystem.deleteFile({
    path: folderNameOfRelative,
    directory: Directory.External,
  });

  /* Delete from database */
  const stream = linq.from(await db.StorageSpaceList.toArray());
  const list = stream.where((s) => s.folderName === folderNameOfRelative).select(s => s.id).toArray();
  db.StorageSpaceList.bulkDelete(list);
}

async function isUsedByProgramData(folderName: string) {
  return false;
}

async function createStorageSpaceEntityIfNotExist(folderName: string) {
  const db = new Database();
  if (linq.from(await db.StorageSpaceList.toArray()).where((s) => s.folderName === folderName)
    .any()) {
    return;
  }

  db.StorageSpaceList.add({
    id: v1(),
    folderName: folderName,
    createDate: new Date(),
    updateDate: new Date(),
  });
}

async function getFolderNameBaseOnBaseFolderPath(relativePathOfFile: string) {
  let absolutePath = relativePathOfFile;
  if (!path.isAbsolute(relativePathOfFile)) {
    absolutePath = (await Filesystem.getUri({
      path: relativePathOfFile,
      directory: Directory.External,
    })).uri;
  }
  absolutePath = path.normalize(absolutePath);
  const rootPath = (await Filesystem.getUri({
    path: "",
    directory: Directory.External,
  })).uri;
  if (!absolutePath.startsWith(rootPath)) {
    throw new Error("Unsupported path");
  }
  if (absolutePath === rootPath) {
    throw new Error("Unsupported path");
  }
  const folderName = linq.from(path.relative(rootPath, absolutePath).split("/")).where(s => !!s).first();
  return folderName;
}
