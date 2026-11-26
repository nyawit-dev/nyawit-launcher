import { spawn } from "child_process";
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const ROOT_DIR = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");

// Brand mark tunggal: emblem komik tanpa teks dipakai semua generator aset.
export const BRAND_MARK_URI =
  "data:image/png;base64," + fs.readFileSync(path.join(ROOT_DIR, "assets", "emblem-tight.png")).toString("base64");

const BROWSER_CANDIDATES = [
  process.env.NYAWIT_BROWSER_PATH,
  "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe",
  "C:\\Program Files\\Microsoft\\Edge\\Application\\msedge.exe",
  "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
  "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
  "/usr/bin/google-chrome",
  "/usr/bin/chromium",
  "/usr/bin/chromium-browser",
  "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
].filter(Boolean);

function resolveBrowser() {
  const found = BROWSER_CANDIDATES.find((candidate) => {
    try {
      return fs.existsSync(candidate);
    } catch {
      return false;
    }
  });
  if (!found) {
    throw new Error(
      "Browser headless tidak ditemukan. Pasang Edge/Chrome atau set NYAWIT_BROWSER_PATH ke executable browser."
    );
  }
  return found;
}

const BROWSER_PATH = resolveBrowser();

// Helper to ensure directory exists
function ensureDir(filePath) {
  const dir = path.dirname(filePath);
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
}

// Render HTML template via headless browser to PNG
function renderHtmlToPng(htmlContent, outputPath, width, height, transparent = true) {
  return new Promise((resolve, reject) => {
    ensureDir(outputPath);
    const tempHtmlPath = path.join(ROOT_DIR, `temp_${Date.now()}_${Math.random().toString(36).substring(7)}.html`);
    fs.writeFileSync(tempHtmlPath, htmlContent);

    const args = [
      "--headless",
      "--hide-scrollbars",
      "--disable-gpu",
      `--window-size=${width},${height}`,
      `--screenshot=${outputPath}`,
      transparent ? "--default-background-color=00000000" : "--default-background-color=ff0b0f14",
      `file:///${tempHtmlPath.replace(/\\/g, "/")}`
    ];

    const proc = spawn(BROWSER_PATH, args);

    proc.on("error", (err) => {
      try { fs.unlinkSync(tempHtmlPath); } catch {}
      reject(err);
    });

    proc.on("exit", (code) => {
      try { fs.unlinkSync(tempHtmlPath); } catch {}
      if (fs.existsSync(outputPath)) {
        const stats = fs.statSync(outputPath);
        if (stats.size > 0) {
          resolve({ path: outputPath, size: stats.size, width, height });
        } else {
          reject(new Error(`Generated file is empty: ${outputPath}`));
        }
      } else {
        reject(new Error(`Browser exited with code ${code} but output file was not found: ${outputPath}`));
      }
    });
  });
}

export { renderHtmlToPng, ensureDir, ROOT_DIR };
