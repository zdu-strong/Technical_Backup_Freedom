import path from 'path'
import os from 'os'
import inquirer from "inquirer"
import linq from 'linq'
import execa from "execa"

async function main() {
  await checkPlatform();
  const isRunAndroid = await getIsRunAndroid();
  const androidSdkRootPath = await getAndroidSdkRootPath();
  await buildReact();
  await runAndroidOrIOS(isRunAndroid, androidSdkRootPath);
  process.exit();
}

async function runAndroidOrIOS(isRunAndroid: boolean, androidSdkRootPath: string) {
  if (isRunAndroid) {
    await execa.command(
      [
        "ionic capacitor build android",
        "--no-build",
        "--prod",
      ].join(" "),
      {
        stdio: "inherit",
        cwd: path.join(__dirname, ".."),
        extendEnv: true,
        env: {
          "ANDROID_SDK_ROOT": `${androidSdkRootPath}`
        },
      }
    );
  } else {
    await execa.command(
      [
        "ionic capacitor build ios",
        "--no-build",
        "--prod",
      ].join(" "),
      {
        stdio: "inherit",
        cwd: path.join(__dirname, ".."),
        extendEnv: true,
      }
    );
  }
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
      },
    }
  );
}

async function checkPlatform() {
  if (os.platform() !== "win32" && os.platform() !== "darwin") {
    throw new Error("The development of linux has not been considered yet");
  }
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

export default main()