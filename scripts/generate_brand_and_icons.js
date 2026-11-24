import { renderHtmlToPng, ROOT_DIR } from "./render_helper.js";
import path from "path";

// Common SVG & CSS components
const COMMON_FONT = `font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;`;

// 1. Logo New (1024x256, transparent)
function getLogoNewHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1024px;
    height: 256px;
    background: transparent;
    overflow: hidden;
    display: flex;
    align-items: center;
    padding-left: 48px;
    ${COMMON_FONT}
  }
  .mark-container {
    width: 160px;
    height: 160px;
    background: linear-gradient(135deg, #1F2C38 0%, #141C24 100%);
    border: 4px solid #273741;
    border-radius: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 16px 36px rgba(0,0,0,0.4), 0 0 24px rgba(61, 220, 151, 0.15);
    flex-shrink: 0;
  }
  .mark-circle {
    width: 100px;
    height: 100px;
    background: linear-gradient(135deg, #4EEDB0 0%, #2EC484 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 16px rgba(61, 220, 151, 0.35);
  }
  .mark-n {
    width: 44px;
    height: 48px;
  }
  .text-content {
    margin-left: 40px;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  .wordmark-row {
    display: flex;
    align-items: baseline;
  }
  .brand-title {
    font-size: 68px;
    font-weight: 900;
    letter-spacing: 4px;
    color: #F1F5F7;
    line-height: 1;
  }
  .brand-dot {
    width: 14px;
    height: 14px;
    background: #3DDC97;
    border-radius: 50%;
    margin-left: 10px;
    box-shadow: 0 0 12px #3DDC97;
  }
  .brand-subtitle {
    font-size: 22px;
    font-weight: 700;
    letter-spacing: 8px;
    color: #3DDC97;
    margin-top: 10px;
    text-transform: uppercase;
  }
  .brand-tagline {
    font-size: 13px;
    font-weight: 600;
    letter-spacing: 2.5px;
    color: #94A3B0;
    margin-top: 8px;
    text-transform: uppercase;
  }
</style>
</head>
<body>
  <div class="mark-container">
    <div class="mark-circle">
      <svg class="mark-n" viewBox="0 0 34 38">
        <polyline points="4,34 4,4 30,34 30,4" fill="none" stroke="#07130E" stroke-width="7" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </div>
  </div>
  <div class="text-content">
    <div class="wordmark-row">
      <div class="brand-title">NYAWIT</div>
      <div class="brand-dot"></div>
    </div>
    <div class="brand-subtitle">LAUNCHER</div>
    <div class="brand-tagline">ANDROID &bull; ANDROID TV &bull; DESKTOP</div>
  </div>
</body>
</html>`;
}

// 2. Generic App Icon Template (512x512)
function getAppIconHtml({ accent, darkBg, innerSvg, subtitle }) {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 512px;
    height: 512px;
    background: transparent;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    ${COMMON_FONT}
  }
  .card {
    width: 440px;
    height: 440px;
    background: linear-gradient(145deg, ${darkBg[0]} 0%, ${darkBg[1]} 100%);
    border: 6px solid ${accent};
    border-radius: 96px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    box-shadow: 0 24px 48px rgba(0,0,0,0.5), 0 0 36px ${accent}40;
    position: relative;
    overflow: hidden;
  }
  .glow-overlay {
    position: absolute;
    top: -50px;
    left: -50px;
    width: 240px;
    height: 240px;
    background: radial-gradient(circle, ${accent}33 0%, transparent 70%);
    pointer-events: none;
  }
  .svg-wrap {
    width: 240px;
    height: 240px;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 2;
  }
  .badge-tag {
    position: absolute;
    bottom: 32px;
    font-size: 14px;
    font-weight: 800;
    letter-spacing: 2.5px;
    color: ${accent};
    text-transform: uppercase;
    background: rgba(0,0,0,0.35);
    padding: 6px 16px;
    border-radius: 20px;
    border: 1px solid ${accent}55;
  }
</style>
</head>
<body>
  <div class="card">
    <div class="glow-overlay"></div>
    <div class="svg-wrap">${innerSvg}</div>
    ${subtitle ? `<div class="badge-tag">${subtitle}</div>` : ""}
  </div>
</body>
</html>`;
}

// 3. UI Illustrations
function getEmptyStateHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 400px;
    height: 300px;
    background: transparent;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    ${COMMON_FONT}
  }
  .art {
    width: 140px;
    height: 140px;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .title {
    margin-top: 16px;
    font-size: 20px;
    font-weight: 700;
    color: #F1F5F7;
  }
  .sub {
    margin-top: 6px;
    font-size: 13px;
    color: #94A3B0;
  }
</style>
</head>
<body>
  <div class="art">
    <svg viewBox="0 0 140 140" width="140" height="140">
      <circle cx="70" cy="70" r="54" fill="none" stroke="#273741" stroke-width="2" stroke-dasharray="6 6"/>
      <circle cx="70" cy="70" r="38" fill="#141C24" stroke="#3DDC97" stroke-width="3"/>
      <line x1="96" y1="96" x2="124" y2="124" stroke="#3DDC97" stroke-width="6" stroke-linecap="round"/>
      <line x1="50" y1="70" x2="90" y2="70" stroke="#94A3B0" stroke-width="3" stroke-linecap="round"/>
      <line x1="70" y1="50" x2="70" y2="90" stroke="#94A3B0" stroke-width="3" stroke-linecap="round"/>
      <circle cx="34" cy="40" r="4" fill="#3DDC97" opacity="0.6"/>
      <circle cx="106" cy="44" r="3" fill="#5AA9E6" opacity="0.6"/>
      <circle cx="44" cy="106" r="3" fill="#FFB454" opacity="0.6"/>
    </svg>
  </div>
  <div class="title">Tidak ada aplikasi</div>
  <div class="sub">Coba kata kunci atau kategori lain.</div>
</body>
</html>`;
}

function getUpdateIllustrationHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 256px;
    height: 256px;
    background: transparent;
    display: flex;
    align-items: center;
    justify-content: center;
    ${COMMON_FONT}
  }
  .wrap {
    width: 220px;
    height: 220px;
    background: radial-gradient(circle, #1F2E28 0%, #101915 80%);
    border: 3px solid #3DDC97;
    border-radius: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 12px 30px rgba(0,0,0,0.5), 0 0 24px rgba(61,220,151,0.25);
  }
</style>
</head>
<body>
  <div class="wrap">
    <svg viewBox="0 0 100 100" width="140" height="140">
      <!-- Glow rings -->
      <circle cx="50" cy="50" r="40" fill="none" stroke="#3DDC97" stroke-width="1.5" stroke-dasharray="4 4" opacity="0.4"/>
      <!-- Download / Upgrade Arrow -->
      <path d="M50 20 L50 62" stroke="#3DDC97" stroke-width="7" stroke-linecap="round"/>
      <polyline points="32,48 50,64 68,48" fill="none" stroke="#3DDC97" stroke-width="7" stroke-linecap="round" stroke-linejoin="round"/>
      <!-- Base tray -->
      <path d="M24 74 L76 74" stroke="#F1F5F7" stroke-width="6" stroke-linecap="round"/>
      <!-- Sparkle -->
      <polygon points="76,28 78,34 84,36 78,38 76,44 74,38 68,36 74,34" fill="#3DDC97"/>
    </svg>
  </div>
</body>
</html>`;
}

function getHeroDesktopHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1600px;
    height: 600px;
    background: #0B0F14;
    overflow: hidden;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 100px;
    ${COMMON_FONT}
  }
  .bg-grid {
    position: absolute;
    top: 0; left: 0; width: 100%; height: 100%;
    background-image: 
      linear-gradient(to right, rgba(39, 55, 65, 0.25) 1px, transparent 1px),
      linear-gradient(to bottom, rgba(39, 55, 65, 0.25) 1px, transparent 1px);
    background-size: 60px 60px;
    pointer-events: none;
  }
  .glow-left {
    position: absolute;
    top: -100px; left: -100px; width: 600px; height: 600px;
    background: radial-gradient(circle, rgba(61, 220, 151, 0.15) 0%, transparent 70%);
  }
  .glow-right {
    position: absolute;
    bottom: -150px; right: 200px; width: 700px; height: 700px;
    background: radial-gradient(circle, rgba(90, 169, 230, 0.12) 0%, transparent 70%);
  }
  .left-side {
    z-index: 2;
    max-width: 650px;
  }
  .badge {
    display: inline-flex;
    align-items: center;
    background: #141C24;
    border: 1px solid #3DDC97;
    padding: 8px 18px;
    border-radius: 30px;
    font-size: 13px;
    font-weight: 700;
    letter-spacing: 2px;
    color: #3DDC97;
    margin-bottom: 24px;
  }
  .main-title {
    font-size: 64px;
    font-weight: 900;
    color: #F1F5F7;
    line-height: 1.1;
    margin-bottom: 20px;
  }
  .main-title span { color: #3DDC97; }
  .desc {
    font-size: 18px;
    line-height: 1.6;
    color: #94A3B0;
    margin-bottom: 32px;
  }
  .platforms-row {
    display: flex;
    gap: 12px;
  }
  .chip {
    background: #1B2630;
    border: 1px solid #273741;
    padding: 8px 16px;
    border-radius: 10px;
    font-size: 13px;
    font-weight: 600;
    color: #F1F5F7;
  }
  .right-side {
    z-index: 2;
    display: flex;
    gap: 24px;
    perspective: 1000px;
  }
  .card-preview {
    width: 220px;
    height: 320px;
    background: #141C24;
    border-radius: 24px;
    padding: 24px 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
    box-shadow: 0 20px 40px rgba(0,0,0,0.6);
  }
  .card-1 { border: 2px solid #5AA9E6; transform: rotate(-4deg) translateY(10px); }
  .card-2 { border: 2px solid #3DDC97; transform: translateY(-15px); }
  .card-3 { border: 2px solid #FFB454; transform: rotate(4deg) translateY(15px); }
  .card-icon {
    width: 72px;
    height: 72px;
    border-radius: 18px;
    margin-bottom: 18px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .card-title {
    font-size: 17px;
    font-weight: 700;
    color: #F1F5F7;
    margin-bottom: 6px;
    text-align: center;
  }
  .card-sub {
    font-size: 12px;
    color: #94A3B0;
    text-align: center;
    margin-bottom: 24px;
  }
  .card-btn {
    margin-top: auto;
    width: 100%;
    padding: 10px;
    border-radius: 10px;
    font-size: 12px;
    font-weight: 700;
    text-align: center;
  }
</style>
</head>
<body>
  <div class="bg-grid"></div>
  <div class="glow-left"></div>
  <div class="glow-right"></div>
  <div class="left-side">
    <div class="badge">ECOSYSTEM HUB</div>
    <div class="main-title">Aplikasi Cepat.<br><span>Satu Ekosistem.</span></div>
    <div class="desc">Jelajahi, instal, dan perbarui aplikasi serta game multiplatform langsung dari GitHub API tanpa perantara.</div>
    <div class="platforms-row">
      <div class="chip">Android</div>
      <div class="chip">Android TV</div>
      <div class="chip">Windows</div>
      <div class="chip">macOS</div>
      <div class="chip">Linux</div>
    </div>
  </div>
  <div class="right-side">
    <div class="card-preview card-1">
      <div class="card-icon" style="background:#5AA9E620; border:1px solid #5AA9E6;">
        <svg viewBox="0 0 24 24" width="36" height="36" fill="none" stroke="#5AA9E6" stroke-width="2">
          <circle cx="12" cy="12" r="9"/>
          <path d="M9 18V6l10 6-10 6z"/>
        </svg>
      </div>
      <div class="card-title">Nyawit Music</div>
      <div class="card-sub">Audio Player TV</div>
      <div class="card-btn" style="background:#5AA9E6; color:#061520;">Buka</div>
    </div>
    <div class="card-preview card-2">
      <div class="card-icon" style="background:#3DDC9720; border:1px solid #3DDC97;">
        <svg viewBox="0 0 24 24" width="36" height="36" fill="none" stroke="#3DDC97" stroke-width="2">
          <polygon points="12 2 15 8 22 9 17 14 18 21 12 18 6 21 7 14 2 9 9 8 12 2"/>
        </svg>
      </div>
      <div class="card-title">Nyawit Launcher</div>
      <div class="card-sub">Multiplatform Hub</div>
      <div class="card-btn" style="background:#3DDC97; color:#07130E;">Terbaru v1.3.1</div>
    </div>
    <div class="card-preview card-3">
      <div class="card-icon" style="background:#FFB45420; border:1px solid #FFB454;">
        <svg viewBox="0 0 24 24" width="36" height="36" fill="none" stroke="#FFB454" stroke-width="2">
          <rect x="2" y="6" width="20" height="12" rx="2"/>
          <line x1="6" y1="12" x2="10" y2="12"/>
          <line x1="8" y1="10" x2="8" y2="14"/>
          <circle cx="15" cy="11" r="1" fill="#FFB454"/>
          <circle cx="17" cy="13" r="1" fill="#FFB454"/>
        </svg>
      </div>
      <div class="card-title">Garis Arcade</div>
      <div class="card-sub">Koleksi Retro Game</div>
      <div class="card-btn" style="background:#FFB454; color:#211403;">Pasang</div>
    </div>
  </div>
</body>
</html>`;
}

function getTvFocusFrameHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 320px;
    height: 180px;
    background: transparent;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .frame {
    width: 304px;
    height: 164px;
    border: 5px solid #3DDC97;
    border-radius: 24px;
    box-shadow: 0 0 24px rgba(61, 220, 151, 0.8), inset 0 0 14px rgba(61, 220, 151, 0.4);
    background: rgba(61, 220, 151, 0.05);
  }
</style>
</head>
<body>
  <div class="frame"></div>
</body>
</html>`;
}

// Batch Runner
async function run() {
  console.log("=== Generating Brand, App Icons, and UI Illustrations ===");

  const tasks = [
    // Brand
    {
      name: "logo-new.png",
      html: getLogoNewHtml(),
      out: path.join(ROOT_DIR, "assets", "logo-new.png"),
      w: 1024,
      h: 256,
      transparent: true
    },
    // App Icons
    {
      name: "app-icons/nyawit-launcher.png",
      html: getAppIconHtml({
        accent: "#3DDC97",
        darkBg: ["#1B2630", "#111820"],
        subtitle: "LAUNCHER",
        innerSvg: `<svg viewBox="0 0 100 100" width="160" height="160">
          <circle cx="50" cy="50" r="42" fill="#3DDC97"/>
          <polyline points="32,68 32,32 68,68 68,32" fill="none" stroke="#07130E" stroke-width="8" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>`
      }),
      out: path.join(ROOT_DIR, "assets", "app-icons", "nyawit-launcher.png"),
      w: 512,
      h: 512,
      transparent: true
    },
    {
      name: "app-icons/nyawit-music.png",
      html: getAppIconHtml({
        accent: "#5AA9E6",
        darkBg: ["#142436", "#0D1824"],
        subtitle: "MUSIC",
        innerSvg: `<svg viewBox="0 0 100 100" width="160" height="160">
          <circle cx="50" cy="50" r="38" fill="none" stroke="#5AA9E6" stroke-width="3" opacity="0.3"/>
          <path d="M26 50 C26 34 36 24 50 24 C64 24 74 34 74 50" fill="none" stroke="#5AA9E6" stroke-width="6" stroke-linecap="round"/>
          <rect x="22" y="48" width="10" height="24" rx="5" fill="#5AA9E6"/>
          <rect x="68" y="48" width="10" height="24" rx="5" fill="#5AA9E6"/>
          <!-- Waveform inside -->
          <line x1="42" y1="44" x2="42" y2="60" stroke="#F1F5F7" stroke-width="4" stroke-linecap="round"/>
          <line x1="50" y1="36" x2="50" y2="68" stroke="#5AA9E6" stroke-width="5" stroke-linecap="round"/>
          <line x1="58" y1="42" x2="58" y2="62" stroke="#F1F5F7" stroke-width="4" stroke-linecap="round"/>
        </svg>`
      }),
      out: path.join(ROOT_DIR, "assets", "app-icons", "nyawit-music.png"),
      w: 512,
      h: 512,
      transparent: true
    },
    {
      name: "app-icons/garis-arcade.png",
      html: getAppIconHtml({
        accent: "#FFB454",
        darkBg: ["#2B1F14", "#18110B"],
        subtitle: "ARCADE",
        innerSvg: `<svg viewBox="0 0 100 100" width="160" height="160">
          <!-- Joystick Base & Stick -->
          <ellipse cx="50" cy="74" rx="34" ry="12" fill="#3D2918" stroke="#FFB454" stroke-width="3"/>
          <line x1="50" y1="70" x2="44" y2="38" stroke="#F1F5F7" stroke-width="8" stroke-linecap="round"/>
          <!-- Ball Top -->
          <circle cx="42" cy="34" r="16" fill="#FF5E5E" stroke="#FFB454" stroke-width="3"/>
          <!-- Classic Arcade Buttons -->
          <circle cx="68" cy="62" r="5" fill="#FFB454"/>
          <circle cx="78" cy="56" r="5" fill="#5AA9E6"/>
        </svg>`
      }),
      out: path.join(ROOT_DIR, "assets", "app-icons", "garis-arcade.png"),
      w: 512,
      h: 512,
      transparent: true
    },
    {
      name: "app-icons/garis_game.png",
      html: getAppIconHtml({
        accent: "#F2789F",
        darkBg: ["#2B1424", "#180A14"],
        subtitle: "TETRIS",
        innerSvg: `<svg viewBox="0 0 100 100" width="160" height="160">
          <!-- Compose / Tetris Shapes with neon glow -->
          <rect x="22" y="24" width="22" height="22" rx="4" fill="#F2789F" stroke="#FFF" stroke-width="2"/>
          <rect x="46" y="24" width="22" height="22" rx="4" fill="#F2789F" stroke="#FFF" stroke-width="2"/>
          <rect x="46" y="48" width="22" height="22" rx="4" fill="#5AA9E6" stroke="#FFF" stroke-width="2"/>
          <rect x="70" y="48" width="22" height="22" rx="4" fill="#5AA9E6" stroke="#FFF" stroke-width="2"/>
          <rect x="22" y="48" width="22" height="22" rx="4" fill="#FFB454" stroke="#FFF" stroke-width="2"/>
        </svg>`
      }),
      out: path.join(ROOT_DIR, "assets", "app-icons", "garis_game.png"),
      w: 512,
      h: 512,
      transparent: true
    },
    {
      name: "app-icons/monopoly-kmp.png",
      html: getAppIconHtml({
        accent: "#9B8CFF",
        darkBg: ["#201736", "#120B22"],
        subtitle: "MONOPOLY",
        innerSvg: `<svg viewBox="0 0 100 100" width="160" height="160">
          <!-- 3D Dice with pips & house -->
          <rect x="24" y="26" width="38" height="38" rx="8" fill="#9B8CFF" stroke="#FFFFFF" stroke-width="3"/>
          <circle cx="34" cy="36" r="3" fill="#120B22"/>
          <circle cx="52" cy="54" r="3" fill="#120B22"/>
          <circle cx="43" cy="45" r="3" fill="#120B22"/>
          <!-- Hotel Roof -->
          <polygon points="50,44 74,44 82,60 58,60" fill="#FF5E5E" stroke="#FFF" stroke-width="2"/>
          <rect x="58" y="60" width="24" height="16" fill="#D32F2F" stroke="#FFF" stroke-width="2"/>
        </svg>`
      }),
      out: path.join(ROOT_DIR, "assets", "app-icons", "monopoly-kmp.png"),
      w: 512,
      h: 512,
      transparent: true
    },
    // UI Illustrations
    {
      name: "ui/empty-state.png",
      html: getEmptyStateHtml(),
      out: path.join(ROOT_DIR, "assets", "ui", "empty-state.png"),
      w: 400,
      h: 300,
      transparent: true
    },
    {
      name: "ui/update-illustration.png",
      html: getUpdateIllustrationHtml(),
      out: path.join(ROOT_DIR, "assets", "ui", "update-illustration.png"),
      w: 256,
      h: 256,
      transparent: true
    },
    {
      name: "ui/hero-desktop.png",
      html: getHeroDesktopHtml(),
      out: path.join(ROOT_DIR, "assets", "ui", "hero-desktop.png"),
      w: 1600,
      h: 600,
      transparent: false
    },
    {
      name: "ui/tv-focus-frame.png",
      html: getTvFocusFrameHtml(),
      out: path.join(ROOT_DIR, "assets", "ui", "tv-focus-frame.png"),
      w: 320,
      h: 180,
      transparent: true
    }
  ];

  for (const t of tasks) {
    try {
      const res = await renderHtmlToPng(t.html, t.out, t.w, t.h, t.transparent);
      console.log(`[OK] Generated ${t.name} (${res.width}x${res.height}, ${Math.round(res.size / 1024)} KB)`);
    } catch (e) {
      console.error(`[FAIL] ${t.name}:`, e);
    }
  }
}

run();
