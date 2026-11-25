#ifndef MyAppVersion
  #define MyAppVersion "0.0.0"
#endif

#define MyAppName "Nyawit Launcher"
#define MyAppPublisher "Nyawit"

[Setup]
AppId={C723E301-F7DC-4ABA-98B8-FC41B467DFE2}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
DefaultDirName={userpf}\{#MyAppName}
DefaultGroupName={#MyAppPublisher}
DisableProgramGroupPage=yes
PrivilegesRequired=lowest
AllowNoIcons=yes
OutputDir=Output
OutputBaseFilename=NyawitLauncher-{#MyAppVersion}-windows-x64-setup
SetupIconFile=..\..\desktopApp\icons\windows\nyawit.ico
UninstallDisplayIcon={app}\nyawit-launcher.exe
Compression=lzma2/max
SolidCompression=yes
WizardStyle=modern
WizardImageFile=wizard-image.bmp
WizardSmallImageFile=wizard-small-image.png
WizardImageStretch=no
WizardImageBackColor=clWhite
VersionInfoVersion={#MyAppVersion}
VersionInfoCompany={#MyAppPublisher}
VersionInfoDescription={#MyAppName} installer
VersionInfoProductName={#MyAppName}
MinVersion=10.0
ArchitecturesAllowed=x64compatible

[Languages]
Name: "default"; MessagesFile: "compiler:Default.isl"

[Messages]
WelcomeLabel1=Selamat datang di Nyawit Launcher {#MyAppVersion}
WelcomeLabel2=Edisi pemasangan ini akan menyiapkan launcher di komputer Anda.%n%nKlik Lanjut untuk membuka pilihan pemasangan.
ReadyLabel2a=Klik Pasang untuk memulai edisi ini.
InstallingLabel=Memasang Nyawit Launcher...
FinishedHeadingLabel=Edisi terpasang
FinishedLabel=Nyawit Launcher siap dijalankan.%n%nKlik Selesai untuk menutup pemasang.
ButtonNext=&Lanjut >
ButtonInstall=&Pasang
ButtonFinish=&Selesai
ButtonCancel=Batal

[Files]
Source: "..\..\desktopApp\build\compose\binaries\main\app\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{autoprograms}\{#MyAppName}"; Filename: "{app}\nyawit-launcher.exe"; IconFilename: "{app}\nyawit-launcher.exe"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\nyawit-launcher.exe"; IconFilename: "{app}\nyawit-launcher.exe"; Tasks: desktopicon

[Tasks]
Name: "desktopicon"; Description: "Buat ikon di Desktop"

[Run]
Filename: "{app}\nyawit-launcher.exe"; Description: "Jalankan Nyawit Launcher"; Flags: postinstall nowait skipifsilent
