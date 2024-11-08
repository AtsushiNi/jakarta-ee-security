# ベースイメージとしてWilfFlyを使用
FROM quay.io/wildfly/wildfly:latest-jdk17

# アプリをビルドするための一時的なイメージを作成
FROM maven:3.8.5-openjdk-17-slim AS build

# 作業ディレクトリを作成
WORKDIR /app

# プロジェクトのpom.xmlをコピー
COPY pom.xml .

# MAVENを実行
RUN mvn dependency:go-offline

# アプリのソースコードをコピー
COPY src ./src

# アプリをビルド
RUN mvn package -DskipTests

# WildFlyイメージにもど理、ビルドしたアプリをコピー
FROM quay.io/wildfly/wildfly:latest-jdk17

# 環境変数の設定
ENV DB_USER=user
ENV DB_PASSWORD=password
ENV DB_URL=jdbc:postgresql://postgres:5435/postgres

# standalone.xmlをコピー
COPY standalone.xml /opt/jboss/wildfly/standalone/configuration/standalone.xml

# PostgreSQL JDBCドライバをコピー
COPY --from=build /root/.m2/repository/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar /opt/jboss/wildfly/modules/system/layers/base/org/postgresql/main/
# PostgreSQL JDBCドライバのモジュール定義
COPY postgresql-module.xml /opt/jboss/wildfly/modules/system/layers/base/org/postgresql/main/module.xml

# 管理者を作成
RUN /opt/jboss/wildfly/bin/add-user.sh admin admin --silent

# アプリをデプロイ
COPY --from=build /app/target/demo-app.war /opt/jboss/wildfly/standalone/deployments/
