import { renderHtmlToPng, ROOT_DIR } from "./render_helper.js";
import path from "path";

const COMMON_FONT = `font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;`;

// Shared App Data
const APPS = [
  {
    name: "Nyawit Music",
    sub: "Pemutar musik dengan dukungan Android TV.",
    cat: "Musik",
    accent: "#5AA9E6",
    tag: "v1.2.0",
    size: "14 MB",
    status: "Terpasang",
    statusColor: "#3DDC97",
    iconLetter: "M"
  },
  {
    name: "Garis Arcade",
    sub: "Kumpulan game klasik: Snake, 2048, dan Tetris.",
    cat: "Game",
    accent: "#FFB454",
    tag: "v1.1.4",
    size: "18 MB",
    status: "Update",
    statusColor: "#FFB454",
    iconLetter: "A"
  },
  {
    name: "Garis Game",
    sub: "Game Tetris modern dibangun dengan Kotlin Compose.",
    cat: "Game",
    accent: "#F2789F",
    tag: "v1.0.8",
    size: "12 MB",
    status: "Pasang",
    statusColor: "#5AA9E6",
    iconLetter: "T"
  },
  {
    name: "Monopoly KMP",
    sub: "Game monopoli Kotlin Multiplatform lintas perangkat.",
    cat: "Game",
    accent: "#9B8CFF",
    tag: "v0.9.5",
    size: "26 MB",
    status: "Pasang",
    statusColor: "#5AA9E6",
    iconLetter: "P"
  }
];

function getBrandMarkSvg(size = 46) {
  return `<svg viewBox="0 0 100 100" width="${size}" height="${size}">
    <rect x="4" y="4" width="92" height="92" rx="22" fill="#1B2630" stroke="#273741" stroke-width="4"/>
    <circle cx="50" cy="50" r="30" fill="#3DDC97"/>
    <polyline points="35,66 35,34 65,66 65,34" fill="none" stroke="#07130E" stroke-width="7" stroke-linecap="round" stroke-linejoin="round"/>
  </svg>`;
}

// 1. Phone Screenshots (1080x1920)
function getPhoneScreenshot1() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1080px;
    height: 1920px;
    background: #0B0F14;
    color: #F1F5F7;
    ${COMMON_FONT}
    padding: 60px 48px;
    display: flex;
    flex-direction: column;
  }
  .status-bar {
    display: flex;
    justify-content: space-between;
    font-size: 28px;
    font-weight: 700;
    color: #94A3B0;
    margin-bottom: 40px;
  }
  .brand-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 40px;
  }
  .brand-left {
    display: flex;
    align-items: center;
    gap: 24px;
  }
  .brand-title {
    font-size: 44px;
    font-weight: 900;
    letter-spacing: 3px;
    color: #F1F5F7;
  }
  .brand-sub {
    font-size: 18px;
    font-weight: 800;
    letter-spacing: 4px;
    color: #3DDC97;
  }
  .search-box {
    background: #141C24;
    border: 2px solid #273741;
    border-radius: 20px;
    padding: 24px 32px;
    display: flex;
    align-items: center;
    gap: 20px;
    font-size: 28px;
    color: #94A3B0;
    margin-bottom: 32px;
  }
  .chips-row {
    display: flex;
    gap: 16px;
    margin-bottom: 48px;
  }
  .chip {
    padding: 16px 36px;
    border-radius: 16px;
    font-size: 26px;
    font-weight: 700;
    background: #141C24;
    border: 2px solid #273741;
    color: #94A3B0;
  }
  .chip.active {
    background: #1D4033;
    border-color: #3DDC97;
    color: #3DDC97;
  }
  .featured-card {
    background: #141C24;
    border: 3px solid #5AA9E6;
    border-radius: 32px;
    padding: 40px;
    margin-bottom: 48px;
    position: relative;
    box-shadow: 0 16px 40px rgba(0,0,0,0.5);
  }
  .feat-tag {
    font-size: 20px;
    font-weight: 800;
    letter-spacing: 2px;
    color: #5AA9E6;
    margin-bottom: 16px;
  }
  .feat-title {
    font-size: 42px;
    font-weight: 900;
    margin-bottom: 12px;
  }
  .feat-desc {
    font-size: 26px;
    color: #94A3B0;
    line-height: 1.5;
    margin-bottom: 32px;
  }
  .feat-btn {
    display: inline-block;
    background: #5AA9E6;
    color: #061520;
    font-size: 26px;
    font-weight: 800;
    padding: 18px 48px;
    border-radius: 16px;
  }
  .section-title {
    font-size: 24px;
    font-weight: 800;
    letter-spacing: 2.5px;
    color: #3DDC97;
    margin-bottom: 24px;
  }
  .app-list {
    display: flex;
    flex-direction: column;
    gap: 24px;
  }
  .app-row-card {
    background: #141C24;
    border: 2px solid #273741;
    border-radius: 28px;
    padding: 28px 32px;
    display: flex;
    align-items: center;
    gap: 28px;
  }
  .app-icon {
    width: 96px;
    height: 96px;
    border-radius: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 40px;
    font-weight: 900;
    flex-shrink: 0;
  }
  .app-details {
    flex: 1;
  }
  .app-name {
    font-size: 32px;
    font-weight: 800;
    margin-bottom: 6px;
  }
  .app-sub {
    font-size: 22px;
    color: #94A3B0;
  }
  .app-action-btn {
    padding: 16px 36px;
    border-radius: 16px;
    font-size: 24px;
    font-weight: 800;
  }
</style>
</head>
<body>
  <div class="status-bar">
    <span>09:41</span>
    <span>5G &bull; 100%</span>
  </div>
  <div class="brand-header">
    <div class="brand-left">
      ${getBrandMarkSvg(88)}
      <div>
        <div class="brand-title">NYAWIT</div>
        <div class="brand-sub">LAUNCHER</div>
      </div>
    </div>
    <div style="background:#1B2630; border:2px solid #273741; border-radius:16px; padding:16px 24px; font-size:22px; font-weight:700; color:#3DDC97;">
      v1.3.0
    </div>
  </div>

  <div class="search-box">
    <svg viewBox="0 0 24 24" width="36" height="36" fill="none" stroke="#94A3B0" stroke-width="2.5"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
    <span>Cari aplikasi atau game...</span>
  </div>

  <div class="chips-row">
    <div class="chip active">Semua</div>
    <div class="chip">Musik</div>
    <div class="chip">Game</div>
    <div class="chip">Terpasang</div>
  </div>

  <div class="featured-card">
    <div class="feat-tag">★ APLIKASI UNGGULAN</div>
    <div class="feat-title">Nyawit Music</div>
    <div class="feat-desc">Pemutar audio modern dengan dukungan playlist, equalizer, dan antarmuka Android TV.</div>
    <div class="feat-btn">Buka Aplikasi</div>
  </div>

  <div class="section-title">SEMUA APLIKASI (4)</div>

  <div class="app-list">
    ${APPS.map(a => `
      <div class="app-row-card" style="border-left: 6px solid ${a.accent};">
        <div class="app-icon" style="background:${a.accent}; color:#07130E;">${a.iconLetter}</div>
        <div class="app-details">
          <div class="app-name">${a.name}</div>
          <div class="app-sub">${a.cat} &bull; ${a.tag} &bull; ${a.size}</div>
        </div>
        <div class="app-action-btn" style="background:${a.statusColor}; color:#07130E;">
          ${a.status}
        </div>
      </div>
    `).join("")}
  </div>
</body>
</html>`;
}

function getPhoneScreenshot2() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1080px;
    height: 1920px;
    background: #0B0F14;
    color: #F1F5F7;
    ${COMMON_FONT}
    position: relative;
    overflow: hidden;
  }
  .backdrop-blur {
    width: 100%; height: 100%;
    background: rgba(11, 15, 20, 0.85);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 60px;
  }
  .dialog-card {
    width: 100%;
    background: #141C24;
    border: 3px solid #273741;
    border-radius: 40px;
    padding: 56px;
    box-shadow: 0 32px 80px rgba(0,0,0,0.8);
  }
  .top-row {
    display: flex;
    gap: 32px;
    align-items: center;
    margin-bottom: 40px;
  }
  .app-avatar {
    width: 130px;
    height: 130px;
    background: #FFB454;
    border-radius: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 56px;
    font-weight: 900;
    color: #211403;
    flex-shrink: 0;
  }
  .name {
    font-size: 46px;
    font-weight: 900;
  }
  .pkg {
    font-size: 22px;
    color: #94A3B0;
    margin-top: 6px;
    font-family: monospace;
  }
  .badge-row {
    display: flex;
    gap: 14px;
    margin-top: 14px;
  }
  .pill {
    background: #1B2630;
    border: 1px solid #273741;
    border-radius: 12px;
    padding: 8px 18px;
    font-size: 20px;
    font-weight: 700;
    color: #FFB454;
  }
  .desc-box {
    background: #1B2630;
    border-radius: 24px;
    padding: 32px;
    font-size: 26px;
    line-height: 1.6;
    color: #F1F5F7;
    margin-bottom: 40px;
  }
  .meta-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24px;
    margin-bottom: 48px;
  }
  .meta-item {
    background: #1B2630;
    border-radius: 20px;
    padding: 24px;
  }
  .meta-label {
    font-size: 18px;
    font-weight: 800;
    color: #94A3B0;
    letter-spacing: 1px;
    margin-bottom: 6px;
  }
  .meta-val {
    font-size: 26px;
    font-weight: 800;
    color: #F1F5F7;
  }
  .action-row {
    display: flex;
    gap: 24px;
  }
  .btn-install {
    flex: 2;
    background: #3DDC97;
    color: #07130E;
    font-size: 30px;
    font-weight: 900;
    padding: 24px;
    border-radius: 24px;
    text-align: center;
  }
  .btn-close {
    flex: 1;
    background: #1B2630;
    border: 2px solid #273741;
    color: #F1F5F7;
    font-size: 30px;
    font-weight: 800;
    padding: 24px;
    border-radius: 24px;
    text-align: center;
  }
</style>
</head>
<body>
  <div class="backdrop-blur">
    <div class="dialog-card">
      <div class="top-row">
        <div class="app-avatar">A</div>
        <div>
          <div class="name">Garis Arcade</div>
          <div class="pkg">dev.nyawit.arcade</div>
          <div class="badge-row">
            <div class="pill">Kategori Game</div>
            <div class="pill" style="color:#3DDC97; border-color:#3DDC97;">v1.1.4</div>
          </div>
        </div>
      </div>

      <div class="desc-box">
        Kumpulan game arkade klasik seru tanpa iklan: Snake, 2048, dan Tetris dengan grafis retro neon dan kontrol layar sentuh responsif.
      </div>

      <div class="meta-grid">
        <div class="meta-item">
          <div class="meta-label">UKURAN BERKAS</div>
          <div class="meta-val">18.4 MB</div>
        </div>
        <div class="meta-item">
          <div class="meta-label">INTEGRITAS</div>
          <div class="meta-val" style="color:#3DDC97;">SHA-256 Valid</div>
        </div>
        <div class="meta-item">
          <div class="meta-label">SUMBER</div>
          <div class="meta-val">GitHub Release</div>
        </div>
        <div class="meta-item">
          <div class="meta-label">LISENSI</div>
          <div class="meta-val">GPL-3.0</div>
        </div>
      </div>

      <div class="action-row">
        <div class="btn-install">Pasang Aplikasi</div>
        <div class="btn-close">Tutup</div>
      </div>
    </div>
  </div>
</body>
</html>`;
}

function getPhoneScreenshot3() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1080px;
    height: 1920px;
    background: #0B0F14;
    color: #F1F5F7;
    ${COMMON_FONT}
    padding: 60px 48px;
    display: flex;
    flex-direction: column;
  }
  .status-bar {
    display: flex;
    justify-content: space-between;
    font-size: 28px;
    font-weight: 700;
    color: #94A3B0;
    margin-bottom: 40px;
  }
  .brand-header {
    display: flex;
    align-items: center;
    gap: 24px;
    margin-bottom: 40px;
  }
  .search-box {
    background: #141C24;
    border: 2px solid #273741;
    border-radius: 20px;
    padding: 24px 32px;
    display: flex;
    align-items: center;
    gap: 20px;
    font-size: 28px;
    color: #F1F5F7;
    margin-bottom: 32px;
  }
  .chips-row {
    display: flex;
    gap: 16px;
    margin-bottom: 48px;
  }
  .chip {
    padding: 16px 36px;
    border-radius: 16px;
    font-size: 26px;
    font-weight: 700;
    background: #141C24;
    border: 2px solid #273741;
    color: #94A3B0;
  }
  .chip.active {
    background: #1D4033;
    border-color: #3DDC97;
    color: #3DDC97;
  }
  .grid-container {
    display: grid;
    grid-template-columns: 1fr;
    gap: 28px;
  }
  .game-card {
    background: #141C24;
    border-radius: 32px;
    padding: 36px;
    display: flex;
    align-items: center;
    gap: 32px;
    border: 2px solid #273741;
  }
  .game-icon {
    width: 110px;
    height: 110px;
    border-radius: 28px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 48px;
    font-weight: 900;
    flex-shrink: 0;
  }
  .game-info {
    flex: 1;
  }
  .game-name {
    font-size: 36px;
    font-weight: 900;
    margin-bottom: 8px;
  }
  .game-desc {
    font-size: 24px;
    color: #94A3B0;
    margin-bottom: 12px;
  }
  .btn-action {
    padding: 18px 40px;
    border-radius: 18px;
    font-size: 26px;
    font-weight: 800;
  }
</style>
</head>
<body>
  <div class="status-bar">
    <span>09:42</span>
    <span>5G &bull; 100%</span>
  </div>
  <div class="brand-header">
    ${getBrandMarkSvg(88)}
    <div>
      <div style="font-size:44px;font-weight:900;letter-spacing:3px;">NYAWIT</div>
      <div style="font-size:18px;font-weight:800;letter-spacing:4px;color:#3DDC97;">LAUNCHER</div>
    </div>
  </div>

  <div class="search-box">
    <svg viewBox="0 0 24 24" width="36" height="36" fill="none" stroke="#3DDC97" stroke-width="2.5"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
    <span>Game Klasik</span>
  </div>

  <div class="chips-row">
    <div class="chip">Semua</div>
    <div class="chip">Musik</div>
    <div class="chip active">Game (3)</div>
  </div>

  <div class="grid-container">
    <div class="game-card" style="border-left: 8px solid #FFB454;">
      <div class="game-icon" style="background:#FFB454; color:#211403;">A</div>
      <div class="game-info">
        <div class="game-name">Garis Arcade</div>
        <div class="game-desc">Kumpulan game klasik: Snake, 2048, dan Tetris.</div>
        <div style="font-size:20px;color:#3DDC97;font-weight:700;">● Versi Baru v1.1.4</div>
      </div>
      <div class="btn-action" style="background:#FFB454; color:#211403;">Update</div>
    </div>

    <div class="game-card" style="border-left: 8px solid #F2789F;">
      <div class="game-icon" style="background:#F2789F; color:#FFF;">T</div>
      <div class="game-info">
        <div class="game-name">Garis Game</div>
        <div class="game-desc">Game Tetris modern berbasis Kotlin Compose.</div>
        <div style="font-size:20px;color:#94A3B0;font-weight:700;">v1.0.8 &bull; 12 MB</div>
      </div>
      <div class="btn-action" style="background:#3DDC97; color:#07130E;">Pasang</div>
    </div>

    <div class="game-card" style="border-left: 8px solid #9B8CFF;">
      <div class="game-icon" style="background:#9B8CFF; color:#FFF;">P</div>
      <div class="game-info">
        <div class="game-name">Monopoly KMP</div>
        <div class="game-desc">Game monopoli Kotlin Multiplatform.</div>
        <div style="font-size:20px;color:#94A3B0;font-weight:700;">v0.9.5 &bull; 26 MB</div>
      </div>
      <div class="btn-action" style="background:#3DDC97; color:#07130E;">Pasang</div>
    </div>
  </div>
</body>
</html>`;
}

function getPhoneScreenshot4() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1080px;
    height: 1920px;
    background: #0B0F14;
    color: #F1F5F7;
    ${COMMON_FONT}
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 60px;
  }
  .dialog {
    width: 100%;
    background: #141C24;
    border: 3px solid #273741;
    border-radius: 40px;
    padding: 56px;
    box-shadow: 0 32px 80px rgba(0,0,0,0.8);
  }
  .tag {
    font-size: 22px;
    font-weight: 800;
    letter-spacing: 2px;
    color: #3DDC97;
    margin-bottom: 20px;
  }
  .title {
    font-size: 48px;
    font-weight: 900;
    margin-bottom: 12px;
  }
  .cur-ver {
    font-size: 24px;
    color: #94A3B0;
    margin-bottom: 36px;
  }
  .installer-box {
    background: #1B2630;
    border-radius: 24px;
    padding: 32px;
    display: flex;
    align-items: center;
    gap: 28px;
    margin-bottom: 40px;
  }
  .box-icon {
    width: 80px;
    height: 80px;
    background: #1D4033;
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .progress-bar {
    width: 100%;
    height: 14px;
    background: #273741;
    border-radius: 7px;
    overflow: hidden;
    margin-top: 14px;
  }
  .progress-fill {
    width: 85%;
    height: 100%;
    background: #3DDC97;
    border-radius: 7px;
  }
  .checksum-badge {
    background: rgba(61, 220, 151, 0.1);
    border: 1px solid #3DDC97;
    border-radius: 14px;
    padding: 16px 24px;
    font-size: 20px;
    color: #3DDC97;
    display: flex;
    align-items: center;
    gap: 14px;
    margin-bottom: 48px;
  }
  .action-row {
    display: flex;
    gap: 24px;
  }
  .btn-confirm {
    flex: 2;
    background: #3DDC97;
    color: #07130E;
    font-size: 30px;
    font-weight: 900;
    padding: 24px;
    border-radius: 24px;
    text-align: center;
  }
  .btn-cancel {
    flex: 1;
    background: #1B2630;
    border: 2px solid #273741;
    color: #F1F5F7;
    font-size: 30px;
    font-weight: 800;
    padding: 24px;
    border-radius: 24px;
    text-align: center;
  }
</style>
</head>
<body>
  <div class="dialog">
    <div class="tag">PEMBARUAN TERSEDIA</div>
    <div class="title">Perbarui ke v1.3.0?</div>
    <div class="cur-ver">Versi terpasang saat ini: v1.2.0</div>

    <div class="installer-box">
      <div class="box-icon">
        <svg viewBox="0 0 24 24" width="44" height="44" fill="none" stroke="#3DDC97" stroke-width="2.5"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
      </div>
      <div style="flex:1;">
        <div style="font-size:28px;font-weight:800;">nyawit-launcher-release.apk</div>
        <div style="font-size:20px;color:#94A3B0;margin-top:4px;">Mengunduh installer native (16.2 MB)</div>
        <div class="progress-bar">
          <div class="progress-fill"></div>
        </div>
      </div>
    </div>

    <div class="checksum-badge">
      <svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="#3DDC97" stroke-width="2.5"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><polyline points="9 12 11 14 15 10"/></svg>
      <span>Checksum SHA-256 diverifikasi otomatis</span>
    </div>

    <div class="action-row">
      <div class="btn-confirm">Instal Pembaruan</div>
      <div class="btn-cancel">Batal</div>
    </div>
  </div>
</body>
</html>`;
}

// 2. Tablet Screenshots (1920x1200)
function getTabletScreenshot1() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1920px;
    height: 1200px;
    background: #0B0F14;
    color: #F1F5F7;
    ${COMMON_FONT}
    display: flex;
  }
  .sidebar {
    width: 380px;
    background: #141C24;
    border-right: 2px solid #273741;
    padding: 48px 36px;
    display: flex;
    flex-direction: column;
  }
  .brand {
    display: flex;
    align-items: center;
    gap: 20px;
    margin-bottom: 56px;
  }
  .nav-group {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
  .nav-item {
    padding: 18px 24px;
    border-radius: 16px;
    font-size: 22px;
    font-weight: 700;
    color: #94A3B0;
    display: flex;
    align-items: center;
    gap: 16px;
  }
  .nav-item.active {
    background: #1B2630;
    border-left: 4px solid #3DDC97;
    color: #F1F5F7;
  }
  .side-footer {
    margin-top: auto;
    font-size: 16px;
    color: #5F7180;
    line-height: 1.5;
  }
  .main-content {
    flex: 1;
    padding: 48px 64px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }
  .top-search-row {
    display: flex;
    gap: 24px;
    margin-bottom: 40px;
  }
  .search-input {
    flex: 1;
    background: #141C24;
    border: 2px solid #273741;
    border-radius: 18px;
    padding: 20px 28px;
    font-size: 22px;
    color: #94A3B0;
    display: flex;
    align-items: center;
    gap: 16px;
  }
  .app-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 28px;
  }
  .grid-card {
    background: #141C24;
    border: 2px solid #273741;
    border-radius: 24px;
    padding: 32px;
    display: flex;
    flex-direction: column;
  }
  .gc-top {
    display: flex;
    align-items: center;
    gap: 20px;
    margin-bottom: 20px;
  }
  .gc-icon {
    width: 72px;
    height: 72px;
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 32px;
    font-weight: 900;
  }
  .gc-title {
    font-size: 26px;
    font-weight: 800;
  }
  .gc-sub {
    font-size: 16px;
    color: #94A3B0;
  }
  .gc-desc {
    font-size: 18px;
    color: #94A3B0;
    line-height: 1.5;
    margin-bottom: 32px;
    flex: 1;
  }
  .gc-btn {
    width: 100%;
    padding: 16px;
    border-radius: 14px;
    font-size: 18px;
    font-weight: 800;
    text-align: center;
  }
</style>
</head>
<body>
  <div class="sidebar">
    <div class="brand">
      ${getBrandMarkSvg(64)}
      <div>
        <div style="font-size:32px;font-weight:900;">NYAWIT</div>
        <div style="font-size:14px;font-weight:800;letter-spacing:3px;color:#3DDC97;">LAUNCHER</div>
      </div>
    </div>
    <div class="nav-group">
      <div class="nav-item active">Semua Aplikasi</div>
      <div class="nav-item">Musik & Audio</div>
      <div class="nav-item">Game Arkade</div>
      <div class="nav-item">Pengaturan</div>
    </div>
    <div class="side-footer">
      <div>Nyawit Hub v1.3.0</div>
      <div>Sumber: GitHub REST API</div>
      <div style="color:#3DDC97;margin-top:4px;">● Siap Digunakan</div>
    </div>
  </div>
  <div class="main-content">
    <div class="top-search-row">
      <div class="search-input">
        <svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="#94A3B0" stroke-width="2.5"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        <span>Cari aplikasi atau repositori GitHub...</span>
      </div>
      <div style="background:#1B2630;border:2px solid #273741;border-radius:18px;padding:20px 32px;font-size:20px;font-weight:700;color:#3DDC97;">
        Perbarui Katalog
      </div>
    </div>

    <div class="app-grid">
      ${APPS.map(a => `
        <div class="grid-card" style="border-top: 5px solid ${a.accent};">
          <div class="gc-top">
            <div class="gc-icon" style="background:${a.accent}; color:#07130E;">${a.iconLetter}</div>
            <div>
              <div class="gc-title">${a.name}</div>
              <div class="gc-sub">${a.cat} &bull; ${a.tag}</div>
            </div>
          </div>
          <div class="gc-desc">${a.sub}</div>
          <div class="gc-btn" style="background:${a.statusColor}; color:#07130E;">
            ${a.status}
          </div>
        </div>
      `).join("")}
    </div>
  </div>
</body>
</html>`;
}

function getTabletScreenshot2() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1920px;
    height: 1200px;
    background: #0B0F14;
    color: #F1F5F7;
    ${COMMON_FONT}
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 80px;
  }
  .modal {
    width: 1100px;
    background: #141C24;
    border: 3px solid #273741;
    border-radius: 36px;
    padding: 60px;
    box-shadow: 0 32px 80px rgba(0,0,0,0.8);
  }
  .top-info {
    display: flex;
    gap: 32px;
    align-items: center;
    margin-bottom: 36px;
  }
  .icon-box {
    width: 120px;
    height: 120px;
    background: #5AA9E6;
    border-radius: 28px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 56px;
    font-weight: 900;
    color: #061520;
  }
  .desc-panel {
    background: #1B2630;
    border-radius: 20px;
    padding: 28px;
    font-size: 22px;
    line-height: 1.6;
    color: #F1F5F7;
    margin-bottom: 36px;
  }
  .spec-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 48px;
  }
  .spec-card {
    background: #1B2630;
    border-radius: 16px;
    padding: 20px;
  }
  .spec-lbl {
    font-size: 14px;
    font-weight: 800;
    color: #94A3B0;
    letter-spacing: 1px;
    margin-bottom: 4px;
  }
  .spec-val {
    font-size: 20px;
    font-weight: 800;
    color: #F1F5F7;
  }
  .btn-row {
    display: flex;
    gap: 20px;
  }
  .btn-p {
    flex: 2;
    background: #3DDC97;
    color: #07130E;
    font-size: 24px;
    font-weight: 900;
    padding: 20px;
    border-radius: 18px;
    text-align: center;
  }
  .btn-s {
    flex: 1;
    background: #1B2630;
    border: 2px solid #273741;
    color: #F1F5F7;
    font-size: 24px;
    font-weight: 800;
    padding: 20px;
    border-radius: 18px;
    text-align: center;
  }
</style>
</head>
<body>
  <div class="modal">
    <div class="top-info">
      <div class="icon-box">M</div>
      <div>
        <div style="font-size:40px;font-weight:900;">Nyawit Music</div>
        <div style="font-size:18px;color:#94A3B0;margin-top:6px;font-family:monospace;">dev.nyawit.music</div>
        <div style="display:flex;gap:10px;margin-top:12px;">
          <div style="background:#1B2630;padding:6px 14px;border-radius:8px;font-size:16px;font-weight:700;color:#5AA9E6;">Audio & TV</div>
          <div style="background:#1B2630;padding:6px 14px;border-radius:8px;font-size:16px;font-weight:700;color:#3DDC97;">Terpasang (v1.2.0)</div>
        </div>
      </div>
    </div>

    <div class="desc-panel">
      Pemutar audio canggih dengan antarmuka Compose Multiplatform yang dirancang khusus untuk kenyamanan navigasi layar sentuh tablet maupun navigasi D-Pad TV.
    </div>

    <div class="spec-grid">
      <div class="spec-card">
        <div class="spec-lbl">PLATFORM</div>
        <div class="spec-val">Android, TV, JVM</div>
      </div>
      <div class="spec-card">
        <div class="spec-lbl">UKURAN</div>
        <div class="spec-val">14.2 MB</div>
      </div>
      <div class="spec-card">
        <div class="spec-lbl">CHECKSUM</div>
        <div class="spec-val" style="color:#3DDC97;">Terverifikasi</div>
      </div>
      <div class="spec-card">
        <div class="spec-lbl">SUMBER</div>
        <div class="spec-val">GitHub Release</div>
      </div>
    </div>

    <div class="btn-row">
      <div class="btn-p">Buka Aplikasi</div>
      <div class="btn-s">Tutup</div>
    </div>
  </div>
</body>
</html>`;
}

// 3. Android TV Screenshots (1920x1080)
function getTvScreenshot1() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1920px;
    height: 1080px;
    background: #0B0F14;
    color: #F1F5F7;
    ${COMMON_FONT}
    padding: 60px 80px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
  .tv-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  .tv-brand {
    display: flex;
    align-items: center;
    gap: 24px;
  }
  .tv-cards-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 40px;
    align-items: center;
  }
  .tv-card {
    background: #141C24;
    border-radius: 28px;
    padding: 40px;
    display: flex;
    flex-direction: column;
    border: 3px solid transparent;
    transition: all 0.2s;
  }
  .tv-card.focused {
    border: 4px solid #3DDC97;
    background: #1B2630;
    box-shadow: 0 0 36px rgba(61, 220, 151, 0.7);
    transform: scale(1.04);
  }
  .tvc-icon {
    width: 90px;
    height: 90px;
    border-radius: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 44px;
    font-weight: 900;
    margin-bottom: 24px;
  }
  .tv-bottom-bar {
    background: #141C24;
    border: 1px solid #273741;
    border-radius: 16px;
    padding: 18px 36px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 18px;
    color: #94A3B0;
  }
  .key-hint {
    color: #3DDC97;
    font-weight: 800;
  }
</style>
</head>
<body>
  <div class="tv-header">
    <div class="tv-brand">
      ${getBrandMarkSvg(68)}
      <div>
        <div style="font-size:36px;font-weight:900;letter-spacing:2px;">NYAWIT LAUNCHER</div>
        <div style="font-size:16px;font-weight:700;color:#3DDC97;letter-spacing:3px;">EDISI ANDROID TV</div>
      </div>
    </div>
    <div style="font-size:22px;color:#94A3B0;">14:30 WIB</div>
  </div>

  <div class="tv-cards-grid">
    <!-- Focused card with glowing frame -->
    <div class="tv-card focused">
      <div class="tvc-icon" style="background:#5AA9E6; color:#061520;">M</div>
      <div style="font-size:32px;font-weight:900;margin-bottom:8px;">Nyawit Music</div>
      <div style="font-size:18px;color:#94A3B0;margin-bottom:28px;">Pemutar audio TV dengan antarmuka D-Pad ramah sofa.</div>
      <div style="background:#3DDC97;color:#07130E;font-size:20px;font-weight:800;padding:16px;border-radius:14px;text-align:center;">
        Tekan [OK] untuk Membuka
      </div>
    </div>

    <div class="tv-card">
      <div class="tvc-icon" style="background:#FFB454; color:#211403;">A</div>
      <div style="font-size:32px;font-weight:900;margin-bottom:8px;">Garis Arcade</div>
      <div style="font-size:18px;color:#94A3B0;margin-bottom:28px;">Game klasik Snake, 2048, dan Tetris untuk TV.</div>
      <div style="background:#1B2630;border:2px solid #273741;color:#F1F5F7;font-size:20px;font-weight:800;padding:16px;border-radius:14px;text-align:center;">
        Update v1.1.4
      </div>
    </div>

    <div class="tv-card">
      <div class="tvc-icon" style="background:#9B8CFF; color:#FFF;">P</div>
      <div style="font-size:32px;font-weight:900;margin-bottom:8px;">Monopoly KMP</div>
      <div style="font-size:18px;color:#94A3B0;margin-bottom:28px;">Game monopoli seru bersama keluarga di layar TV.</div>
      <div style="background:#1B2630;border:2px solid #273741;color:#F1F5F7;font-size:20px;font-weight:800;padding:16px;border-radius:14px;text-align:center;">
        Pasang Aplikasi
      </div>
    </div>
  </div>

  <div class="tv-bottom-bar">
    <div><span class="key-hint">[D-PAD]</span> Navigasi</div>
    <div><span class="key-hint">[OK]</span> Pilih / Buka</div>
    <div><span class="key-hint">[TAHAN OK]</span> Informasi Rilis</div>
    <div><span class="key-hint">[KEMBALI]</span> Keluar</div>
  </div>
</body>
</html>`;
}

function getTvScreenshot2() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1920px;
    height: 1080px;
    background: #0B0F14;
    color: #F1F5F7;
    ${COMMON_FONT}
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 60px;
  }
  .dialog-box {
    width: 960px;
    background: #141C24;
    border: 3px solid #3DDC97;
    border-radius: 36px;
    padding: 56px;
    box-shadow: 0 0 50px rgba(61, 220, 151, 0.4);
  }
  .dialog-title {
    font-size: 38px;
    font-weight: 900;
    margin-bottom: 12px;
  }
  .dialog-desc {
    font-size: 22px;
    color: #94A3B0;
    line-height: 1.6;
    margin-bottom: 36px;
  }
  .specs {
    background: #1B2630;
    border-radius: 20px;
    padding: 24px 32px;
    display: flex;
    justify-content: space-between;
    margin-bottom: 40px;
    font-size: 20px;
  }
  .btn-actions {
    display: flex;
    gap: 24px;
  }
  .tv-btn {
    flex: 1;
    padding: 20px;
    border-radius: 18px;
    font-size: 22px;
    font-weight: 800;
    text-align: center;
  }
</style>
</head>
<body>
  <div class="dialog-box">
    <div style="font-size:18px;font-weight:800;color:#3DDC97;letter-spacing:2px;margin-bottom:12px;">NAVIGASI TV D-PAD</div>
    <div class="dialog-title">Buka Garis Arcade?</div>
    <div class="dialog-desc">Kumpulan game klasik dioptimalkan untuk remote kontrol Android TV. Pengalaman bermain lancar di layar lebar Anda.</div>
    <div class="specs">
      <div>Versi: <b style="color:#FFF;">v1.1.4</b></div>
      <div>Ukuran: <b style="color:#FFF;">18.4 MB</b></div>
      <div>Integritas: <b style="color:#3DDC97;">Terverifikasi</b></div>
    </div>
    <div class="btn-actions">
      <div class="tv-btn" style="background:#3DDC97;color:#07130E;box-shadow:0 0 20px rgba(61,220,151,0.6);">[OK] Jalankan Game</div>
      <div class="tv-btn" style="background:#1B2630;border:2px solid #273741;color:#F1F5F7;">[KEMBALI] Batal</div>
    </div>
  </div>
</body>
</html>`;
}

// 4. Desktop OS Releases (1920x1080)
function getDesktopReleaseHtml(osType) {
  const isWin = osType === "windows";
  const isMac = osType === "macos";
  const isLinux = osType === "linux";

  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1920px;
    height: 1080px;
    background: #0B0F14;
    color: #F1F5F7;
    ${COMMON_FONT}
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }
  .titlebar {
    height: 48px;
    background: #141C24;
    border-bottom: 1px solid #273741;
    display: flex;
    align-items: center;
    padding: 0 18px;
    user-select: none;
  }
  .mac-traffic-lights {
    display: flex;
    gap: 8px;
    margin-right: 20px;
  }
  .tl-btn {
    width: 13px;
    height: 13px;
    border-radius: 50%;
  }
  .tl-close { background: #FF5F56; }
  .tl-min { background: #FFBD2E; }
  .tl-max { background: #27C93F; }

  .window-title {
    font-size: 13px;
    font-weight: 600;
    color: #94A3B0;
    flex: 1;
    text-align: ${isMac ? "center" : "left"};
  }
  .win-controls {
    display: flex;
    margin-left: auto;
  }
  .win-btn {
    width: 46px;
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #94A3B0;
    font-size: 14px;
  }
  .win-btn.close:hover { background: #E81123; color: #FFF; }

  .app-body {
    flex: 1;
    display: flex;
  }
  .side {
    width: 320px;
    background: #141C24;
    border-right: 1px solid #273741;
    padding: 32px 24px;
    display: flex;
    flex-direction: column;
  }
  .content {
    flex: 1;
    padding: 40px 56px;
    display: flex;
    flex-direction: column;
  }
  .os-badge {
    display: inline-block;
    background: ${isWin ? "#0078D4" : isMac ? "#FFFFFF" : "#E95420"};
    color: ${isMac ? "#000000" : "#FFFFFF"};
    font-size: 12px;
    font-weight: 800;
    padding: 4px 12px;
    border-radius: 6px;
    margin-bottom: 24px;
    width: fit-content;
  }
  .grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
  }
  .card {
    background: #141C24;
    border: 1px solid #273741;
    border-radius: 18px;
    padding: 24px;
  }
</style>
</head>
<body>
  <div class="titlebar">
    ${isMac ? `
      <div class="mac-traffic-lights">
        <div class="tl-btn tl-close"></div>
        <div class="tl-btn tl-min"></div>
        <div class="tl-btn tl-max"></div>
      </div>
    ` : ""}
    <div class="window-title">Nyawit Launcher — v1.3.0 (${osType.toUpperCase()})</div>
    ${isWin ? `
      <div class="win-controls">
        <div class="win-btn">&minus;</div>
        <div class="win-btn">&#9633;</div>
        <div class="win-btn close">&times;</div>
      </div>
    ` : isLinux ? `
      <div class="win-controls">
        <div class="win-btn">&minus;</div>
        <div class="win-btn">&times;</div>
      </div>
    ` : ""}
  </div>

  <div class="app-body">
    <div class="side">
      <div class="os-badge">${isWin ? "WINDOWS NATIVE (MSI)" : isMac ? "MACOS NATIVE (DMG)" : "LINUX NATIVE (DEB/RPM)"}</div>
      <div style="display:flex;align-items:center;gap:14px;margin-bottom:32px;">
        ${getBrandMarkSvg(48)}
        <span style="font-size:22px;font-weight:800;">NYAWIT</span>
      </div>
      <div style="background:#1B2630;padding:12px 18px;border-radius:12px;color:#3DDC97;font-weight:700;margin-bottom:10px;">Semua Aplikasi</div>
      <div style="padding:12px 18px;color:#94A3B0;font-weight:600;">Kategori Musik</div>
      <div style="padding:12px 18px;color:#94A3B0;font-weight:600;">Kategori Game</div>
    </div>
    <div class="content">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:32px;">
        <div style="font-size:28px;font-weight:900;">Katalog Aplikasi JVM Desktop</div>
        <div style="background:#3DDC97;color:#07130E;padding:10px 20px;border-radius:10px;font-weight:800;font-size:14px;">Periksa Pembaruan</div>
      </div>
      <div class="grid">
        ${APPS.map(a => `
          <div class="card" style="border-top: 4px solid ${a.accent};">
            <div style="font-size:20px;font-weight:800;margin-bottom:6px;">${a.name}</div>
            <div style="font-size:13px;color:#94A3B0;margin-bottom:20px;">${a.sub}</div>
            <div style="background:${a.statusColor};color:#07130E;padding:10px;border-radius:8px;text-align:center;font-weight:800;font-size:13px;">
              ${a.status}
            </div>
          </div>
        `).join("")}
      </div>
    </div>
  </div>
</body>
</html>`;
}

async function run() {
  console.log("=== Generating All Screenshots & OS Release Mockups ===");

  const tasks = [
    // Phone 1..4 (1080x1920)
    {
      name: "play/screenshot-phone-1.png",
      html: getPhoneScreenshot1(),
      out: path.join(ROOT_DIR, "play", "screenshot-phone-1.png"),
      w: 1080,
      h: 1920
    },
    {
      name: "play/screenshot-phone-2.png",
      html: getPhoneScreenshot2(),
      out: path.join(ROOT_DIR, "play", "screenshot-phone-2.png"),
      w: 1080,
      h: 1920
    },
    {
      name: "play/screenshot-phone-3.png",
      html: getPhoneScreenshot3(),
      out: path.join(ROOT_DIR, "play", "screenshot-phone-3.png"),
      w: 1080,
      h: 1920
    },
    {
      name: "play/screenshot-phone-4.png",
      html: getPhoneScreenshot4(),
      out: path.join(ROOT_DIR, "play", "screenshot-phone-4.png"),
      w: 1080,
      h: 1920
    },
    // Tablet 1..2 (1920x1200)
    {
      name: "play/screenshot-tablet-1.png",
      html: getTabletScreenshot1(),
      out: path.join(ROOT_DIR, "play", "screenshot-tablet-1.png"),
      w: 1920,
      h: 1200
    },
    {
      name: "play/screenshot-tablet-2.png",
      html: getTabletScreenshot2(),
      out: path.join(ROOT_DIR, "play", "screenshot-tablet-2.png"),
      w: 1920,
      h: 1200
    },
    // TV 1..2 (1920x1080)
    {
      name: "play/screenshot-tv-1.png",
      html: getTvScreenshot1(),
      out: path.join(ROOT_DIR, "play", "screenshot-tv-1.png"),
      w: 1920,
      h: 1080
    },
    {
      name: "play/screenshot-tv-2.png",
      html: getTvScreenshot2(),
      out: path.join(ROOT_DIR, "play", "screenshot-tv-2.png"),
      w: 1920,
      h: 1080
    },
    // Release OS Mockups (1920x1080)
    {
      name: "docs/images/release-windows.png",
      html: getDesktopReleaseHtml("windows"),
      out: path.join(ROOT_DIR, "docs", "images", "release-windows.png"),
      w: 1920,
      h: 1080
    },
    {
      name: "docs/images/release-linux.png",
      html: getDesktopReleaseHtml("linux"),
      out: path.join(ROOT_DIR, "docs", "images", "release-linux.png"),
      w: 1920,
      h: 1080
    },
    {
      name: "docs/images/release-macos.png",
      html: getDesktopReleaseHtml("macos"),
      out: path.join(ROOT_DIR, "docs", "images", "release-macos.png"),
      w: 1920,
      h: 1080
    }
  ];

  for (const t of tasks) {
    try {
      const res = await renderHtmlToPng(t.html, t.out, t.w, t.h, false);
      console.log(`[OK] Generated ${t.name} (${res.width}x${res.height}, ${Math.round(res.size / 1024)} KB)`);
    } catch (e) {
      console.error(`[FAIL] ${t.name}:`, e);
    }
  }
}

run();
