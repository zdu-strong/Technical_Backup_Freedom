import { execSync } from 'child_process'
import fs from 'fs'
import path from 'path'

async function main() {
  await deletePackageLockFile();
  await deleteBuildFolder();
  await installDependencies();
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

async function deleteBuildFolder() {
  const folderPath = path.join(__dirname, "..", "build");
  await fs.promises.rm(folderPath, { recursive: true, force: true });
}

export default main()