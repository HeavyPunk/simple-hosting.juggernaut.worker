set -eu

BUILD_ARTIFACT_PATH=./target/scala-3.3.1/*.jar

cd ..

rm -rf target

sbt package

rm -rf ./.ci/build

mkdir ./.ci/build
cp $BUILD_ARTIFACT_PATH ./.ci/build/

cp prod-settings.yml ./.ci/build/settings.yml