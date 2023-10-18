import { execSync } from 'child_process'
import fs from 'fs'
import path from 'path'

async function main() {
  await deletePackageLockFile();
  await deleteDistFolder();
  await deleteOutputFolder();
  await installDependencies();
  await eslint();
  process.exit();
}

async function installDependencies() {
  execSync(
    [
      "npm install",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      env: {
        ...process.env,
        "npm_config_package_lock": "false",
      } as any,
    }
  );
}

async function deletePackageLockFile() {
  const filePathOfPackageLockFile = path.join(__dirname, "..", "package-lock.json");
  await fs.promises.rm(filePathOfPackageLockFile, { recursive: true, force: true });
}

async function deleteDistFolder() {
  const folderPath = path.join(__dirname, "..", "dist");
  await fs.promises.rm(folderPath, { recursive: true, force: true });
}

async function deleteOutputFolder() {
  const folderPath = path.join(__dirname, "..", "output");
  await fs.promises.rm(folderPath, { recursive: true, force: true });
}

async function eslint() {
  execSync(
    "eslint \"{src,apps,libs}/**/*.ts\"",
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
    }
  )
}

export default main()