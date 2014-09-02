"C:\Program Files\Java\jre7\bin\keytool.exe" -exportcert -alias androiddebugkey -keystore "%HOMEPATH%\.android\debug.keystore" | "C:\Users\sahar\Dropbox\android\KidsPortal\openSSL\bin\openssl.exe" sha1 -binary | "C:\Users\sahar\Dropbox\android\KidsPortal\openSSL\bin\openssl.exe"  base64

pause