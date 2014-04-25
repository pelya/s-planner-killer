#!/bin/sh
# Set path to your Android keystore and your keystore alias here, or put them in your environment
[ -z "$ANDROID_KEYSTORE_FILE" ] && ANDROID_KEYSTORE_FILE=~/.android/debug.keystore
[ -z "$ANDROID_KEYSTORE_ALIAS" ] && ANDROID_KEYSTORE_ALIAS=androiddebugkey

# Remove old certificate
zip -d killer/build/apk/killer-release-unsigned.apk "META-INF/*"
# Sign with the new certificate
echo Using keystore $ANDROID_KEYSTORE_FILE and alias $ANDROID_KEYSTORE_ALIAS
stty -echo
jarsigner -verbose -keystore $ANDROID_KEYSTORE_FILE -sigalg MD5withRSA -digestalg SHA1 killer/build/apk/killer-release-unsigned.apk $ANDROID_KEYSTORE_ALIAS || exit 1
stty echo
echo
rm -f s-planner-killer.apk
zipalign 4 killer/build/apk/killer-release-unsigned.apk s-planner-killer.apk
