# should be executed from root of project

export FIREBASE_CREDENTIALS=$(pwd)/push/secrets/firebase-push-secret.json
cd push/app
./gradlew bootRun
cd ../../
