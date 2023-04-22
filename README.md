## Running load tests

1. Data bootstrap:
   ```bash
   docker compose up -d postgres
   ./gradlew :tools:bootstrap --args="postgres"
   ```
2. Build docker images
   ```bash
   ./gradlew :spring-web:bootBuildImage
   ./gradlew :spring-webflux:bootBuildImage
   ./gradlew -Pjetty :spring-web:bootBuildImage
   ```
3. Run application
   1. Docker
       1. Tomcat
           ```bash
          docker compose rm --force && docker compose up spring-web-tomcat
          ```
       2. Tomcat + Loom
           ```bash
          docker compose rm --force && docker compose up spring-web-tomcat-loom
          ```
       3. Webflux
           ```bash
          docker compose rm --force && docker compose up spring-webflux
          ```
   2. Gradle
      1. Tomcat
          ```bash
         ./gradlew :spring-web:bootRun
         ```
      2. Tomcat + Loom
          ```bash
         ./gradlew :spring-web:bootRun --args="--spring.profiles.active=loom"
         ```
      3. Webflux
          ```bash
         ./gradlew -Pjetty :spring-webflux:bootRun
         ```
4. Run **db-read-test** (reading a random item from postgres)
   1. With application in Docker
      ```bash
      docker run -e TARGET_HOST=application --network benchmark-network --rm -i grafana/k6 run --quiet - <tools/k6/db-read-test.js
      ```
   2. With application in Gradle
      ```bash
      docker run -e TARGET_HOST=host.docker.internal --rm -i grafana/k6 run --quiet - <tools/k6/db-read-test.js
      ```
5. Run **copy-test** (writing a 1 MiB file to a disk in 32 chunks of 32 KiB)
    1. With application in Docker
       ```bash
       docker run -e TARGET_HOST=application --network benchmark-network --rm -i grafana/k6 run --quiet - <tools/k6/copy-test.js
       ```
    2. With application in Gradle
       ```bash
       docker run -e TARGET_HOST=host.docker.internal --rm -i grafana/k6 run --quiet - <tools/k6/copy-test.js
       ```
6. Run **download-test** test (downloading two files consecutively from an external webservice)
    1. With application in Docker
       ```bash
       docker run -e TARGET_HOST=application --network benchmark-network --rm -i grafana/k6 run --quiet - <tools/k6/download-test.js
       ```
    2. With application in Gradle
       ```bash
       docker run -e TARGET_HOST=host.docker.internal --rm -i grafana/k6 run --quiet - <tools/k6/download-test.js
       ```

### Web (Docker)
```bash
docker compose rm --force && docker compose up spring-web-tomcat
```
#### Results **db-read-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run -e TARGET_HOST=application --network benchmark-network --rm -i grafana/k6 run --quiet - <tools/k6/db-read-test.js
```
     checks...............: 100.00% ✓ 497605       ✗ 0
     data_received........: 202 MB  6.7 MB/s
     data_sent............: 42 MB   1.4 MB/s
     iteration_duration...: avg=29.97ms p(99)=146.61ms p(99.9)=239.18ms p(99.99)=332.36ms max=473.39ms
     iterations...........: 497605  16565.492727/s

#### Results **copy-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run -e TARGET_HOST=application --network benchmark-network --rm -i grafana/k6 run --quiet - <tools/k6/copy-test.js
```
     checks...............: 100.00% ✓ 1350      ✗ 0
     data_received........: 177 kB  3.8 kB/s
     data_sent............: 114 kB  2.4 kB/s
     iteration_duration...: avg=14.3s p(99)=20.7s p(99.9)=20.78s p(99.99)=20.78s max=20.78s
     iterations...........: 1350    28.871743/s


#### Results **download-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run -e TARGET_HOST=application --network benchmark-network --rm -i grafana/k6 run --quiet - <tools/k6/download-test.js
```
     checks...............: 100.00% ✓ 2099      ✗ 0
     data_received........: 271 kB  7.0 kB/s
     data_sent............: 189 kB  4.8 kB/s
     iteration_duration...: avg=8.09s p(99)=11.68s p(99.9)=12.2s p(99.99)=12.26s max=12.26s
     iterations...........: 2099    53.840925/s

### Web + Loom (Docker)
```bash
docker compose rm --force && docker compose up spring-web-tomcat-loom
```
#### Results **db-read-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
> Pinned carrier threads result in degraded performance (https://github.com/pgjdbc/pgjdbc/issues/1951)
```bash
docker run -e TARGET_HOST=application --network benchmark-network --rm -i grafana/k6 run --quiet - <tools/k6/db-read-test.js
```
     checks...............: 100.00% ✓ 189290     ✗ 0
     data_received........: 77 MB   2.6 MB/s
     data_sent............: 16 MB   529 kB/s
     iteration_duration...: avg=78.99ms p(99)=167.24ms p(99.9)=237.61ms p(99.99)=240.9ms max=350.75ms
     iterations...........: 189290  6292.35721/s

#### Results **copy-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run -e TARGET_HOST=application --network benchmark-network --rm -i grafana/k6 run --quiet - <tools/k6/copy-test.js
```
     checks...............: 100.00% ✓ 1350      ✗ 0
     data_received........: 177 kB  3.8 kB/s
     data_sent............: 114 kB  2.4 kB/s
     iteration_duration...: avg=14.46s p(99)=29.4s p(99.9)=29.43s p(99.99)=29.43s max=29.43s
     iterations...........: 1350    28.654728/s


#### Results **download-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run -e TARGET_HOST=application --network benchmark-network --rm -i grafana/k6 run --quiet - <tools/k6/download-test.js
```
     checks...............: 100.00% ✓ 2139      ✗ 0
     data_received........: 276 kB  7.2 kB/s
     data_sent............: 193 kB  5.0 kB/s
     iteration_duration...: avg=7.85s p(99)=13.37s p(99.9)=13.83s p(99.99)=13.84s max=13.84s
     iterations...........: 2139    55.602732/s

### Webflux (Docker)
```bash
docker compose rm --force && docker compose up spring-webflux
```
#### Results **db-read-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run -e TARGET_HOST=application --network benchmark-network --rm -i grafana/k6 run --quiet - <tools/k6/db-read-test.js
```
     checks...............: 100.00% ✓ 118127      ✗ 0
     data_received........: 34 MB   1.1 MB/s
     data_sent............: 9.9 MB  324 kB/s
     iteration_duration...: avg=126.67ms p(99)=407.02ms p(99.9)=762.35ms p(99.99)=877.68ms max=911.35ms
     iterations...........: 118127  3850.840137/s

#### Results **copy-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run -e TARGET_HOST=application --network benchmark-network --rm -i grafana/k6 run --quiet - <tools/k6/copy-test.js
```
     checks...............: 100.00% ✓ 1524      ✗ 0
     data_received........: 118 kB  2.7 kB/s
     data_sent............: 128 kB  3.0 kB/s
     iteration_duration...: avg=12.05s p(99)=14.51s p(99.9)=14.53s p(99.99)=14.56s max=14.57s
     iterations...........: 1524    35.194136/s

#### Results **download-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run -e TARGET_HOST=application --network benchmark-network --rm -i grafana/k6 run --quiet - <tools/k6/download-test.js
```
     checks...............: 100.00% ✓ 2178      ✗ 0
     data_received........: 164 kB  4.3 kB/s
     data_sent............: 196 kB  5.1 kB/s
     iteration_duration...: avg=7.75s p(99)=11.56s p(99.9)=12.01s p(99.99)=12.87s max=13.04s
     iterations...........: 2178    57.103158/s
