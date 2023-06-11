import path from 'path'
import os from 'os'
import inquirer from "inquirer"
import linq from 'linq'
import execa from "execa"
import treeKill from 'tree-kill'
import util from 'util'
import fs from 'fs'

async function main() {
  const isRunAndroid = await getIsRunAndroid();
  const androidSdkRootPath = await getAndroidSdkRootPath();
  await addPlatformSupport(isRunAndroid);
  const deviceList = await getDeviceList(isRunAndroid);
  await buildReact();
  await runAndroidOrIOS(isRunAndroid, androidSdkRootPath, deviceList);
  // await copySignedApk(isRunAndroid);
  process.exit();
}

async function runAndroidOrIOS(isRunAndroid: boolean, androidSdkRootPath: string, deviceList: string[]) {
  await execa.command(
    [
      `cap sync ${isRunAndroid ? "android" : "ios"}`,
      "--deployment",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: (isRunAndroid ? {
        "ANDROID_SDK_ROOT": `${androidSdkRootPath}`
      } : {
      }) as any,
    }
  );
  if (isRunAndroid) {
    await updateDownloadAddressOfGradleZipFile();
    await updateDownloadAddressOfGrableDependencies();
  }
  await execa.command(
    [
      `cap run ${isRunAndroid ? "android" : "ios"}`,
      "--no-sync",
      `${deviceList.length === 1 ? `--target=${linq.from(deviceList).single()}` : ''}`,
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: (isRunAndroid ? {
        "ANDROID_SDK_ROOT": `${androidSdkRootPath}`,
      } : {
      }) as any,
    }
  );
  const childProcess = execa.command(
    [
      // `cap build ${isRunAndroid ? "android" : "ios"}`,
      `cap open ${isRunAndroid ? "android" : "ios"}`,
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: (isRunAndroid ? {
        "ANDROID_SDK_ROOT": `${androidSdkRootPath}`
      } : {
      }) as any,
    }
  );
  await childProcess;
  await util.promisify(treeKill)(childProcess.pid!).catch(async () => null);
}

async function buildReact() {
  await execa.command(
    [
      "react-app-rewired build",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: {
        "GENERATE_SOURCEMAP": "false",
      } as any,
    }
  );
}

async function getAndroidSdkRootPath() {
  let androidSdkRootPath = path.join(os.homedir(), "AppData/Local/Android/sdk").replace(new RegExp("\\\\", "g"), "/");
  if (os.platform() === "darwin") {
    androidSdkRootPath = path.join(os.homedir(), "Android/Sdk").replace(new RegExp("\\\\", "g"), "/");
  }
  return androidSdkRootPath;
}

async function getIsRunAndroid() {
  let isRunAndroid = true;
  if (os.platform() === "darwin") {
    const MOBILE_PHONE_ENUM = {
      iOS: "iOS",
      Android: "Android"
    };
    const answers = await inquirer.prompt([{
      type: "list",
      name: "mobile phone",
      message: "Do you wish to develop for android or ios?",
      default: MOBILE_PHONE_ENUM.iOS,
      choices: [
        {
          name: MOBILE_PHONE_ENUM.iOS,
          value: MOBILE_PHONE_ENUM.iOS,
        },
        {
          name: MOBILE_PHONE_ENUM.Android,
          value: MOBILE_PHONE_ENUM.Android,
        },
      ],
    }]);
    const chooseAnswer = linq.from(Object.values(answers)).single();
    if (chooseAnswer === MOBILE_PHONE_ENUM.iOS) {
      isRunAndroid = false;
    } else if (chooseAnswer === MOBILE_PHONE_ENUM.Android) {
      isRunAndroid = true;
    } else {
      throw new Error("Please select the type of mobile phone system to be developed!");
    }
  }
  return isRunAndroid;
}

async function copySignedApk(isRunAndroid: boolean) {
  if (isRunAndroid) {
    const apkPath = path.join(__dirname, "..", "android/app/build/outputs/apk/release", "app-release-signed.apk");
    const filePathOfNewApk = path.join(__dirname, "..", "app-release-signed.apk");
    await fs.promises.copyFile(apkPath, filePathOfNewApk);
  }
}

async function addPlatformSupport(isRunAndroid: boolean) {
  await execa.command(
    `cap add ${isRunAndroid ? 'android' : 'ios'}`,
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
    }
  );
}

async function getDeviceList(isRunAndroid: boolean) {
  let deviceList = [] as string[];
  if (isRunAndroid) {
    const { stdout: androidDeviceOutput } = await execa.command(
      `cap run ${isRunAndroid ? 'android' : 'ios'} --list`,
      {
        stdio: "pipe",
        cwd: path.join(__dirname, ".."),
      }
    );

    const androidDeviceOutputList = linq.from(androidDeviceOutput.split("\r\n")).selectMany(item => item.split("\n")).toArray();
    const startIndex = androidDeviceOutputList.findIndex((item: string) => item.includes('-----'));
    if (startIndex < 0) {
      throw new Error("No available Device!")
    }
    deviceList = linq.from(androidDeviceOutputList).skip(startIndex + 1).select(item => linq.from(item.split(new RegExp("\\s+"))).select(item => item.trim()).toArray()).select(s => linq.from(s).last()).toArray();
    deviceList = deviceList.filter(s => s === "Pixel_6_API_33");
    if (!deviceList.length) {
      throw new Error("No available Device!")
    }
    if (deviceList.length === 1) {
      return deviceList;
    }
    throw new Error("More than one available Device!")
  }
  return deviceList;
}

async function updateDownloadAddressOfGradleZipFile() {
  const filePathOfGradlePropertiesFile = path.join(__dirname, "..", "android", "gradle", "wrapper", "gradle-wrapper.properties");
  const text = await fs.promises.readFile(filePathOfGradlePropertiesFile, "utf8");
  const replaceText = text.replace("https\\://services.gradle.org/distributions/", "https\\://mirrors.cloud.tencent.com/gradle/");
  await fs.promises.writeFile(filePathOfGradlePropertiesFile, replaceText);
}

async function updateDownloadAddressOfGrableDependencies() {
  {
    const filePathOfGradlePropertiesFile = path.join(__dirname, "..", "android", "build.gradle");
    const text = await fs.promises.readFile(filePathOfGradlePropertiesFile, "utf8");
    const replaceText = text.replace(new RegExp("google\\(\\)\\s+mavenCentral\\(\\)", "ig"), `maven{ url 'https://maven.aliyun.com/repository/google' }\n        maven{ url 'https://maven.aliyun.com/repository/central' }`);
    await fs.promises.writeFile(filePathOfGradlePropertiesFile, replaceText);
  }
  {
    const filePathOfGradlePropertiesFile = path.join(__dirname, "..", "android", "capacitor-cordova-android-plugins", "build.gradle");
    const text = await fs.promises.readFile(filePathOfGradlePropertiesFile, "utf8");
    let replaceText = text.replace(new RegExp("google\\(\\)\\n        mavenCentral\\(\\)", "ig"), `maven{ url 'https://maven.aliyun.com/repository/google' }\n        maven{ url 'https://maven.aliyun.com/repository/central' }`);
    replaceText = replaceText.replace(new RegExp("google\\(\\)\\n    mavenCentral\\(\\)", "ig"), `maven{ url 'https://maven.aliyun.com/repository/google' }\n    maven{ url 'https://maven.aliyun.com/repository/central' }`);
    await fs.promises.writeFile(filePathOfGradlePropertiesFile, replaceText);
  }
}

export default main()