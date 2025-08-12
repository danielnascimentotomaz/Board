plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Biblioteca principal do Liquibase para controle e versionamento de banco de dados
    implementation("org.liquibase:liquibase-core:4.29.1")

    // Driver JDBC do MySQL para que a aplicação possa se conectar e executar comandos no banco MySQL
    implementation("mysql:mysql-connector-java:8.0.33")

    // Biblioteca Lombok que ajuda a reduzir código boilerplate em Java (como getters, setters, construtores, etc.)
    implementation("org.projectlombok:lombok:1.18.34")

    // Processador de anotações do Lombok, usado em tempo de compilação para gerar o código automaticamente
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    implementation("com.zaxxer:HikariCP:5.0.1")

    implementation("org.slf4j:slf4j-simple:2.0.9")


}

tasks.test {
    useJUnitPlatform()
}