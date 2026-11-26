import { renderHtmlToPng, ROOT_DIR, BRAND_MARK_URI } from "./render_helper.js";
import path from "path";

const COMMON_FONT = `font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;`;

// 1. Play Store Icon (512x512)
function getPlayIconHtml() {
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
    display: flex;
    align-items: center;
    justify-content: center;
    ${COMMON_FONT}
  }
  .app-container {
    width: 480px;
    height: 480px;
    background: linear-gradient(135deg, #1C2732 0%, #0F161E 100%);
    border: 5px solid #273741;
    border-radius: 108px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    box-shadow: 0 24px 60px rgba(0,0,0,0.6), inset 0 2px 4px rgba(255,255,255,0.1);
    position: relative;
    overflow: hidden;
  }
  .circle-core {
    width: 260px;
    height: 260px;
    background: linear-gradient(135deg, #4EEDB0 0%, #2EC484 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 16px 40px rgba(61, 220, 151, 0.4);
  }
  .svg-n {
    width: 240px;
    height: 240px;
  }
  .corner-light {
    position: absolute;
    top: -80px;
    right: -80px;
    width: 240px;
    height: 240px;
    background: radial-gradient(circle, rgba(61, 220, 151, 0.25) 0%, transparent 70%);
  }
</style>
</head>
<body>
  <div class="app-container">
    <div class="corner-light"></div>
    <div class="circle-core">
      <svg class="svg-n" viewBox="0 0 34 38">
        <image href="${BRAND_MARK_URI}" x="0" y="0" width="100" height="100" preserveAspectRatio="xMidYMid meet"/>
      </svg>
    </div>
  </div>
</body>
</html>`;
}

// 2. Play Store Feature Graphic (1024x500)
function getFeatureGraphicHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1024px;
    height: 500px;
    background: #0B0F14;
    overflow: hidden;
    position: relative;
    display: flex;
    align-items: center;
    padding: 0 60px;
    ${COMMON_FONT}
  }
  .bg-decor {
    position: absolute;
    top: 0; left: 0; width: 100%; height: 100%;
    background-image: 
      radial-gradient(circle at 15% 50%, rgba(61, 220, 151, 0.18) 0%, transparent 55%),
      radial-gradient(circle at 85% 50%, rgba(90, 169, 230, 0.15) 0%, transparent 55%),
      linear-gradient(to right, rgba(39, 55, 65, 0.2) 1px, transparent 1px),
      linear-gradient(to bottom, rgba(39, 55, 65, 0.2) 1px, transparent 1px);
    background-size: 100% 100%, 100% 100%, 48px 48px, 48px 48px;
  }
  .content {
    position: relative;
    z-index: 2;
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
  }
  .left-col {
    max-width: 520px;
  }
  .tag {
    display: inline-flex;
    align-items: center;
    background: rgba(61, 220, 151, 0.12);
    border: 1px solid #3DDC97;
    border-radius: 20px;
    padding: 6px 14px;
    font-size: 11px;
    font-weight: 800;
    letter-spacing: 2px;
    color: #3DDC97;
    text-transform: uppercase;
    margin-bottom: 16px;
  }
  .brand-head {
    font-size: 46px;
    font-weight: 900;
    color: #F1F5F7;
    line-height: 1.15;
    letter-spacing: 1px;
  }
  .brand-head span { color: #3DDC97; }
  .tagline {
    margin-top: 14px;
    font-size: 15px;
    line-height: 1.5;
    color: #94A3B0;
  }
  .badges-row {
    margin-top: 24px;
    display: flex;
    gap: 8px;
  }
  .platform-badge {
    background: #141C24;
    border: 1px solid #273741;
    border-radius: 8px;
    padding: 6px 12px;
    font-size: 11px;
    font-weight: 700;
    color: #F1F5F7;
  }
  .right-col {
    display: flex;
    align-items: center;
    gap: 16px;
  }
  .mock-device {
    background: #141C24;
    border: 2px solid #273741;
    border-radius: 20px;
    padding: 16px;
    box-shadow: 0 16px 40px rgba(0,0,0,0.6);
  }
  .phone-mock {
    width: 170px;
    height: 330px;
    border: 2px solid #3DDC97;
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
  .app-preview-card {
    background: #1B2630;
    border-radius: 10px;
    padding: 10px;
    display: flex;
    align-items: center;
    gap: 10px;
  }
  .app-mini-icon {
    width: 32px;
    height: 32px;
    border-radius: 8px;
    flex-shrink: 0;
  }
  .app-mini-info {
    flex: 1;
  }
  .mini-title {
    font-size: 11px;
    font-weight: 700;
    color: #F1F5F7;
  }
  .mini-sub {
    font-size: 8px;
    color: #94A3B0;
  }
  .tv-mock {
    width: 220px;
    height: 240px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    gap: 12px;
  }
  .tv-header {
    font-size: 12px;
    font-weight: 800;
    color: #3DDC97;
    letter-spacing: 1px;
  }
</style>
</head>
<body>
  <div class="bg-decor"></div>
  <div class="content">
    <div class="left-col">
      <div class="tag">KOTLIN MULTIPLATFORM</div>
      <div class="brand-head">NYAWIT<br><span>LAUNCHER</span></div>
      <div class="tagline">Katalog aplikasi dan game multiplatform yang cepat, ringan, dan transparan langsung dari repositori GitHub.</div>
      <div class="badges-row">
        <div class="platform-badge">Android</div>
        <div class="platform-badge">Android TV</div>
        <div class="platform-badge">Windows</div>
        <div class="platform-badge">macOS</div>
        <div class="platform-badge">Linux</div>
      </div>
    </div>
    <div class="right-col">
      <!-- Phone Mockup -->
      <div class="mock-device phone-mock">
        <div style="font-size:10px;font-weight:800;color:#F1F5F7;display:flex;justify-content:space-between;">
          <span>KATALOG</span>
          <span style="color:#3DDC97;">● ONLINE</span>
        </div>
        <div class="app-preview-card" style="border: 1px solid #5AA9E655;">
          <div class="app-mini-icon" style="background:#5AA9E6;display:flex;align-items:center;justify-content:center;color:#000;font-weight:900;font-size:14px;">M</div>
          <div class="app-mini-info">
            <div class="mini-title">Nyawit Music</div>
            <div class="mini-sub">Pemutar Audio</div>
          </div>
        </div>
        <div class="app-preview-card" style="border: 1px solid #FFB45455;">
          <div class="app-mini-icon" style="background:#FFB454;display:flex;align-items:center;justify-content:center;color:#000;font-weight:900;font-size:14px;">A</div>
          <div class="app-mini-info">
            <div class="mini-title">Garis Arcade</div>
            <div class="mini-sub">Koleksi Game</div>
          </div>
        </div>
        <div class="app-preview-card" style="border: 1px solid #F2789F55;">
          <div class="app-mini-icon" style="background:#F2789F;display:flex;align-items:center;justify-content:center;color:#000;font-weight:900;font-size:14px;">T</div>
          <div class="app-mini-info">
            <div class="mini-title">Garis Game</div>
            <div class="mini-sub">Tetris KMP</div>
          </div>
        </div>
        <div style="margin-top:auto;background:#3DDC97;border-radius:6px;padding:6px;text-align:center;font-size:10px;font-weight:800;color:#07130E;">PASANG SEKARANG</div>
      </div>

      <!-- TV & Remote Visual -->
      <div class="mock-device tv-mock">
        <div class="tv-header">DUKUNGAN ANDROID TV</div>
        <div style="font-size:11px;color:#94A3B0;line-height:1.4;">Optimasi navigasi D-pad Leanback dan fokus visual yang jelas di layar televisi.</div>
        <div style="background:#1B2630;border:2px solid #3DDC97;border-radius:10px;padding:12px;display:flex;align-items:center;gap:10px;">
          <div style="width:12px;height:12px;border-radius:50%;background:#3DDC97;"></div>
          <span style="font-size:11px;font-weight:700;color:#F1F5F7;">Fokus D-Pad Responsif</span>
        </div>
      </div>
    </div>
  </div>
</body>
</html>`;
}

// 3. Play Store Promo Graphic (1280x720)
function getPromoGraphicHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1280px;
    height: 720px;
    background: #0B0F14;
    overflow: hidden;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    ${COMMON_FONT}
  }
  .bg-glow {
    position: absolute;
    width: 800px;
    height: 800px;
    background: radial-gradient(circle, rgba(61, 220, 151, 0.15) 0%, transparent 65%);
    top: 50%; left: 50%;
    transform: translate(-50%, -50%);
  }
  .inner-frame {
    z-index: 2;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  .logo-box {
    width: 130px;
    height: 130px;
    background: #141C24;
    border: 3px solid #3DDC97;
    border-radius: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 16px 40px rgba(0,0,0,0.6), 0 0 30px rgba(61,220,151,0.3);
    margin-bottom: 24px;
  }
  .core {
    width: 84px;
    height: 84px;
    background: #3DDC97;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .main-h {
    font-size: 56px;
    font-weight: 900;
    color: #F1F5F7;
    letter-spacing: 2px;
  }
  .main-h span { color: #3DDC97; }
  .sub-h {
    margin-top: 12px;
    font-size: 20px;
    color: #94A3B0;
    max-width: 700px;
    line-height: 1.5;
  }
  .pills {
    margin-top: 36px;
    display: flex;
    gap: 16px;
  }
  .pill {
    background: #1B2630;
    border: 1px solid #273741;
    border-radius: 12px;
    padding: 12px 24px;
    font-size: 14px;
    font-weight: 700;
    color: #F1F5F7;
    display: flex;
    align-items: center;
    gap: 10px;
  }
  .pill span { color: #3DDC97; }
</style>
</head>
<body>
  <div class="bg-glow"></div>
  <div class="inner-frame">
    <div class="logo-box">
      <div class="core">
        <svg viewBox="0 0 34 38" width="40" height="44">
          <image href="${BRAND_MARK_URI}" x="0" y="0" width="100" height="100" preserveAspectRatio="xMidYMid meet"/>
        </svg>
      </div>
    </div>
    <div class="main-h">NYAWIT <span>LAUNCHER</span></div>
    <div class="sub-h">Platform distribusi dan peluncur aplikasi multiplatform berbasis GitHub tanpa server dengan verifikasi integritas paket.</div>
    <div class="pills">
      <div class="pill"><span>●</span> Android & TV</div>
      <div class="pill"><span>●</span> Windows MSI</div>
      <div class="pill"><span>●</span> Linux DEB/RPM</div>
      <div class="pill"><span>●</span> macOS DMG</div>
    </div>
  </div>
</body>
</html>`;
}

// 4. Social Preview (1280x640)
function getSocialPreviewHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1280px;
    height: 640px;
    background: #0B0F14;
    overflow: hidden;
    position: relative;
    display: flex;
    align-items: center;
    padding: 0 80px;
    ${COMMON_FONT}
  }
  .grid-pattern {
    position: absolute;
    top: 0; left: 0; width: 100%; height: 100%;
    background-image: 
      radial-gradient(circle at 80% 30%, rgba(61, 220, 151, 0.14) 0%, transparent 60%),
      linear-gradient(to right, rgba(39, 55, 65, 0.22) 1px, transparent 1px),
      linear-gradient(to bottom, rgba(39, 55, 65, 0.22) 1px, transparent 1px);
    background-size: 100% 100%, 64px 64px, 64px 64px;
  }
  .content {
    z-index: 2;
    display: flex;
    width: 100%;
    justify-content: space-between;
    align-items: center;
  }
  .left {
    max-width: 620px;
  }
  .header-tag {
    display: inline-flex;
    align-items: center;
    background: #1B2630;
    border: 1px solid #3DDC97;
    border-radius: 8px;
    padding: 6px 14px;
    font-size: 12px;
    font-weight: 700;
    letter-spacing: 2px;
    color: #3DDC97;
    margin-bottom: 20px;
  }
  .title {
    font-size: 58px;
    font-weight: 900;
    color: #F1F5F7;
    letter-spacing: 1px;
    line-height: 1.1;
  }
  .title span { color: #3DDC97; }
  .desc {
    margin-top: 18px;
    font-size: 18px;
    line-height: 1.5;
    color: #94A3B0;
  }
  .features {
    margin-top: 28px;
    display: flex;
    gap: 14px;
  }
  .f-item {
    background: #141C24;
    border: 1px solid #273741;
    border-radius: 10px;
    padding: 10px 16px;
    font-size: 13px;
    font-weight: 600;
    color: #F1F5F7;
  }
  .right {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  .glass-card {
    width: 380px;
    background: rgba(20, 28, 36, 0.85);
    border: 1px solid #273741;
    border-radius: 18px;
    padding: 18px 24px;
    display: flex;
    align-items: center;
    gap: 18px;
    box-shadow: 0 16px 36px rgba(0,0,0,0.5);
  }
  .gc-icon {
    width: 52px;
    height: 52px;
    border-radius: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 900;
    font-size: 20px;
    flex-shrink: 0;
  }
</style>
</head>
<body>
  <div class="grid-pattern"></div>
  <div class="content">
    <div class="left">
      <div class="header-tag">OPEN SOURCE &bull; GPL-3.0</div>
      <div class="title">Nyawit<br><span>Launcher</span></div>
      <div class="desc">A modern Kotlin Multiplatform app launcher with Compose Multiplatform for Android, Android TV, and JVM Desktop.</div>
      <div class="features">
        <div class="f-item">Direct GitHub REST API</div>
        <div class="f-item">Zero Backend Server</div>
        <div class="f-item">SHA-256 Verified</div>
      </div>
    </div>
    <div class="right">
      <div class="glass-card" style="border-left: 5px solid #3DDC97;">
        <div class="gc-icon" style="background:#3DDC97; color:#07130E;">N</div>
        <div>
          <div style="font-size:16px;font-weight:800;color:#F1F5F7;">Nyawit Launcher</div>
          <div style="font-size:12px;color:#94A3B0;margin-top:2px;">v1.3.1 &bull; Multiplatform Release</div>
        </div>
      </div>
      <div class="glass-card" style="border-left: 5px solid #5AA9E6;">
        <div class="gc-icon" style="background:#5AA9E6; color:#061520;">M</div>
        <div>
          <div style="font-size:16px;font-weight:800;color:#F1F5F7;">Nyawit Music</div>
          <div style="font-size:12px;color:#94A3B0;margin-top:2px;">Android &bull; TV &bull; Desktop</div>
        </div>
      </div>
      <div class="glass-card" style="border-left: 5px solid #FFB454;">
        <div class="gc-icon" style="background:#FFB454; color:#211403;">A</div>
        <div>
          <div style="font-size:16px;font-weight:800;color:#F1F5F7;">Garis Arcade</div>
          <div style="font-size:12px;color:#94A3B0;margin-top:2px;">Snake &bull; 2048 &bull; Tetris</div>
        </div>
      </div>
    </div>
  </div>
</body>
</html>`;
}

// 5. Readme Hero Banner (1280x400)
function getReadmeHeroHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1280px;
    height: 400px;
    background: #0B0F14;
    overflow: hidden;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 70px;
    ${COMMON_FONT}
  }
  .bg {
    position: absolute;
    top: 0; left: 0; width: 100%; height: 100%;
    background: 
      radial-gradient(circle at 10% 40%, rgba(61, 220, 151, 0.16) 0%, transparent 50%),
      radial-gradient(circle at 90% 70%, rgba(90, 169, 230, 0.12) 0%, transparent 50%),
      linear-gradient(to right, rgba(39, 55, 65, 0.2) 1px, transparent 1px),
      linear-gradient(to bottom, rgba(39, 55, 65, 0.2) 1px, transparent 1px);
    background-size: 100% 100%, 100% 100%, 50px 50px, 50px 50px;
  }
  .left {
    z-index: 2;
    display: flex;
    align-items: center;
    gap: 36px;
  }
  .mark-box {
    width: 120px;
    height: 120px;
    background: #141C24;
    border: 3px solid #273741;
    border-radius: 28px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 12px 30px rgba(0,0,0,0.6);
  }
  .mark-circle {
    width: 76px;
    height: 76px;
    background: #3DDC97;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .h-title {
    font-size: 52px;
    font-weight: 900;
    color: #F1F5F7;
    line-height: 1.1;
    letter-spacing: 2px;
  }
  .h-title span { color: #3DDC97; }
  .h-sub {
    font-size: 16px;
    color: #94A3B0;
    margin-top: 8px;
    letter-spacing: 1px;
  }
  .right {
    z-index: 2;
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
    max-width: 440px;
    justify-content: flex-end;
  }
  .badge-chip {
    background: #1B2630;
    border: 1px solid #273741;
    border-radius: 10px;
    padding: 10px 18px;
    font-size: 13px;
    font-weight: 700;
    color: #F1F5F7;
  }
</style>
</head>
<body>
  <div class="bg"></div>
  <div class="left">
    <div class="mark-box">
      <div class="mark-circle">
        <svg viewBox="0 0 34 38" width="36" height="40">
          <image href="${BRAND_MARK_URI}" x="0" y="0" width="100" height="100" preserveAspectRatio="xMidYMid meet"/>
        </svg>
      </div>
    </div>
    <div>
      <div class="h-title">NYAWIT <span>LAUNCHER</span></div>
      <div class="h-sub">Compose Multiplatform &bull; Android, Android TV & Desktop</div>
    </div>
  </div>
  <div class="right">
    <div class="badge-chip" style="border-color:#3DDC97; color:#3DDC97;">Kotlin 2.0+</div>
    <div class="badge-chip">Compose Multiplatform</div>
    <div class="badge-chip">Direct GitHub API</div>
    <div class="badge-chip">Leanback TV Focus</div>
    <div class="badge-chip">SHA-256 Checksum</div>
  </div>
</body>
</html>`;
}

async function run() {
  console.log("=== Generating Store and Documentation Graphics ===");

  const tasks = [
    {
      name: "play/ic_launcher-512.png",
      html: getPlayIconHtml(),
      out: path.join(ROOT_DIR, "play", "ic_launcher-512.png"),
      w: 512,
      h: 512,
      transparent: false
    },
    {
      name: "play/feature-graphic.png",
      html: getFeatureGraphicHtml(),
      out: path.join(ROOT_DIR, "play", "feature-graphic.png"),
      w: 1024,
      h: 500,
      transparent: false
    },
    {
      name: "play/promo-graphic.png",
      html: getPromoGraphicHtml(),
      out: path.join(ROOT_DIR, "play", "promo-graphic.png"),
      w: 1280,
      h: 720,
      transparent: false
    },
    {
      name: "docs/images/social-preview.png",
      html: getSocialPreviewHtml(),
      out: path.join(ROOT_DIR, "docs", "images", "social-preview.png"),
      w: 1280,
      h: 640,
      transparent: false
    },
    {
      name: "docs/images/readme-hero.png",
      html: getReadmeHeroHtml(),
      out: path.join(ROOT_DIR, "docs", "images", "readme-hero.png"),
      w: 1280,
      h: 400,
      transparent: false
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
