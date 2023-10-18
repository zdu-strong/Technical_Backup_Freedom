import execa from "execa"
import path from 'path'

async function main() {
  await buildReact();
  await buildElectron();
  process.exit();
}

async function buildElectron() {
  await execa.command(
    [
      "electron-builder build",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
    }
  );
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
      }
    }
  );
}

export default main()