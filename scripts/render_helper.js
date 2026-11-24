import { spawn } from "child_process";
import fs from "fs";
import path from "path";

const EDGE_PATH = "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe";
const ROOT_DIR = process.cwd();

// Helper to ensure directory exists
function ensureDir(filePath) {
  const dir = path.dirname(filePath);
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
}

// Render HTML template via Headless Edge to PNG
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

    const proc = spawn(EDGE_PATH, args);

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
        reject(new Error(`Edge exited with code ${code} but output file was not found: ${outputPath}`));
      }
    });
  });
}

export { renderHtmlToPng, ensureDir, ROOT_DIR };
