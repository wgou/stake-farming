cd /home/code/fast-bnbh
git pull
mvn install
rm -rf  /app/fast_bnbh/fast-bnbh-3.0.0.jar
cp  -rf target/fast-bnbh-3.0.0.jar  /app/fast_bnbh/

