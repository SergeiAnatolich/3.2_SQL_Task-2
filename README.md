docker-compose up

java -jar app-deadline.jar -P:jdbc.url=jdbc:mysql://localhost:3306/alfabank_test -P:jdbc.user=sergei -P:jdbc.password=mypassword

docker-compose exec mysql mysql -u sergei -p alfabank_test