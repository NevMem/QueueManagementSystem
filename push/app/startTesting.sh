# should be executed from root of project

export FIREBASE_CREDENTIALS=$(pwd)/push/secrets/firebase-push-secret.json
export AWS_SECRET_ACCESS_KEY=s2UA8dKySI6coF4rc1Hr3c0JN_2yS-u4_FJw-hYW
export AWS_ACCESS_KEY_ID=42BeZXLcrEtr17QxhduJ

cd push/app
./gradlew bootRun
cd ../../
