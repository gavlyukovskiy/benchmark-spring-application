## Running load tests

1. Data bootstrap:
    ```bash
    docker-compose up -d postgres
    ./gradlew :tools:bootstrap --args="postgres"
    ```
2. Run application (see different options below)
3. Run **db-read-test** (reading a random item from postgres)
    ```bash
    docker run [-e TARGET_HOST=host.docker.internal] [--network benchmark-spring-aplication_default] --rm -i grafana/k6 run [--quiet] - <tools/k6/db-read-test.js
    ```
4. Run **copy-test** (writing a 50MB file to a disk)
    ```bash
    docker run [-e TARGET_HOST=host.docker.internal] [--network benchmark-spring-aplication_default] --rm -i grafana/k6 run [--quiet] - <tools/k6/copy-test.js
    ```
5. Run **download-test** test (downloading two files consecutively from an external webservice)
    ```bash
    docker run [-e TARGET_HOST=host.docker.internal] [--network benchmark-spring-aplication_default] --rm -i grafana/k6 run [--quiet] - <tools/k6/download-test.js
    ```

### Web
```bash
./gradlew :spring-web:bootRun
```
or with Jetty
```bash
./gradlew -Pjetty :spring-web:bootRun
```
#### Results **db-read-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run --rm -i grafana/k6 run --quiet - <tools/k6/db-read-test.js
```
```text
data_received..................: 34 MB  1.7 MB/s
data_sent......................: 7.8 MB 386 kB/s
dropped_iterations.............: 16126  797.639553/s
http_req_blocked...............: avg=235.11µs p(99)=1.1ms    p(99.9)=59.67ms  p(99.99)=154.62ms max=159.07ms
http_req_connecting............: avg=229.98µs p(99)=1.01ms   p(99.9)=59.58ms  p(99.99)=154.52ms max=158.97ms
http_req_duration..............: avg=228.41ms p(99)=512.33ms p(99.9)=979.22ms p(99.99)=1.06s    max=1.17s
  { expected_response:true }...: avg=228.41ms p(99)=512.33ms p(99.9)=979.22ms p(99.99)=1.06s    max=1.17s
http_req_failed................: 0.00%  ✓ 0           ✗ 83875
http_req_receiving.............: avg=17.11ms  p(99)=215.76ms p(99.9)=242.23ms p(99.99)=268.42ms max=270.26ms
http_req_sending...............: avg=22.82µs  p(99)=187.34µs p(99.9)=762.96µs p(99.99)=1.86ms   max=4.19ms
http_req_tls_handshaking.......: avg=0s       p(99)=0s       p(99.9)=0s       p(99.99)=0s       max=0s
http_req_waiting...............: avg=211.26ms p(99)=498.41ms p(99.9)=972.1ms  p(99.99)=1.05s    max=1.17s
http_reqs......................: 83875  4148.705041/s
iteration_duration.............: avg=228.71ms p(99)=514.25ms p(99.9)=1s       p(99.99)=1.1s     max=1.21s
iterations.....................: 83874  4148.655578/s
vus............................: 1000   min=855       max=1000
vus_max........................: 1000   min=855       max=1000
```
#### Results **copy-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run --rm -i grafana/k6 run --quiet - <tools/k6/copy-test.js
```
```text
data_received..................: 404 kB 15 kB/s
data_sent......................: 292 kB 11 kB/s
dropped_iterations.............: 866    31.186041/s
http_req_blocked...............: avg=614.27µs p(99)=2.35ms   p(99.9)=34.33ms  p(99.99)=39.45ms  max=39.91ms
http_req_connecting............: avg=577.08µs p(99)=2.21ms   p(99.9)=34.25ms  p(99.99)=39.34ms  max=39.79ms
http_req_duration..............: avg=4.39s    p(99)=8.12s    p(99.9)=8.34s    p(99.99)=8.36s    max=8.36s
  { expected_response:true }...: avg=4.39s    p(99)=8.12s    p(99.9)=8.34s    p(99.99)=8.36s    max=8.36s
http_req_failed................: 0.00%  ✓ 0          ✗ 3135
http_req_receiving.............: avg=173.14µs p(99)=2.3ms    p(99.9)=5.43ms   p(99.99)=12.36ms  max=12.69ms
http_req_sending...............: avg=53.44µs  p(99)=140.99µs p(99.9)=413.23µs p(99.99)=488.55µs max=512µs
http_req_tls_handshaking.......: avg=0s       p(99)=0s       p(99.9)=0s       p(99.99)=0s       max=0s
http_req_waiting...............: avg=4.39s    p(99)=8.12s    p(99.9)=8.34s    p(99.99)=8.36s    max=8.36s
http_reqs......................: 3135   112.89635/s
iteration_duration.............: avg=4.39s    p(99)=8.12s    p(99.9)=8.34s    p(99.99)=8.36s    max=8.36s
iterations.....................: 3134   112.860338/s
vus............................: 964    min=100      max=964
vus_max........................: 964    min=100      max=964
```

#### Results **download-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run --rm -i grafana/k6 run --quiet - <tools/k6/download-test.js
```
```text
data_received..................: 128 kB 6.1 kB/s
data_sent......................: 98 kB  4.7 kB/s
dropped_iterations.............: 12     0.573683/s
http_req_blocked...............: avg=236.37µs p(99)=1.79ms   p(99.9)=17.5ms   p(99.99)=35.81ms  max=37.84ms
http_req_connecting............: avg=219.35µs p(99)=1.69ms   p(99.9)=17.39ms  p(99.99)=35.71ms  max=37.74ms
http_req_duration..............: avg=851.64ms p(99)=5.92s    p(99.9)=6.39s    p(99.99)=6.85s    max=6.91s
  { expected_response:true }...: avg=851.64ms p(99)=5.92s    p(99.9)=6.39s    p(99.99)=6.85s    max=6.91s
http_req_failed................: 0.00%  ✓ 0         ✗ 990
http_req_receiving.............: avg=86.96µs  p(99)=271.66µs p(99.9)=412.21µs p(99.99)=477.9µs  max=485.2µs
http_req_sending...............: avg=39.52µs  p(99)=107.64µs p(99.9)=142.91µs p(99.99)=144.52µs max=144.69µs
http_req_tls_handshaking.......: avg=0s       p(99)=0s       p(99.9)=0s       p(99.99)=0s       max=0s
http_req_waiting...............: avg=851.51ms p(99)=5.92s    p(99.9)=6.39s    p(99.99)=6.85s    max=6.91s
http_reqs......................: 990    47.328852/s
iteration_duration.............: avg=851.98ms p(99)=5.92s    p(99.9)=6.39s    p(99.99)=6.86s    max=6.91s
iterations.....................: 989    47.281045/s
vus............................: 112    min=100     max=112
vus_max........................: 112    min=100     max=112
```

### Web + Loom
```bash
./gradlew :spring-web:bootRun --args="--spring.profiles.active=loom"
```
or with Jetty
```bash
./gradlew -Pjetty :spring-web:bootRun --args="--spring.profiles.active=loom"
```
#### Results **db-read-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run --rm -i grafana/k6 run --quiet - <tools/k6/db-read-test.js
```
```text
data_received..................: 34 MB  1.7 MB/s
data_sent......................: 7.7 MB 381 kB/s
dropped_iterations.............: 17114  845.76101/s
http_req_blocked...............: avg=1.1ms    p(99)=1.23ms   p(99.9)=254.84ms p(99.99)=272.87ms max=275.76ms
http_req_connecting............: avg=1.09ms   p(99)=1.13ms   p(99.9)=254.72ms p(99.99)=272.78ms max=275.69ms
http_req_duration..............: avg=228.79ms p(99)=457.48ms p(99.9)=513.6ms  p(99.99)=532.94ms max=537.38ms
  { expected_response:true }...: avg=228.79ms p(99)=457.48ms p(99.9)=513.6ms  p(99.99)=532.94ms max=537.38ms
http_req_failed................: 0.00%  ✓ 0           ✗ 82888
http_req_receiving.............: avg=19.34ms  p(99)=219.11ms p(99.9)=268.66ms p(99.99)=276.65ms max=278.41ms
http_req_sending...............: avg=25.76µs  p(99)=221.03µs p(99.9)=1.12ms   p(99.99)=3.37ms   max=4.9ms
http_req_tls_handshaking.......: avg=0s       p(99)=0s       p(99.9)=0s       p(99.99)=0s       max=0s
http_req_waiting...............: avg=209.43ms p(99)=306.27ms p(99.9)=471.72ms p(99.99)=480.07ms max=501.4ms
http_reqs......................: 82888  4096.262626/s
iteration_duration.............: avg=229.97ms p(99)=465.65ms p(99.9)=527.06ms p(99.99)=664.68ms max=695.34ms
iterations.....................: 82887  4096.213207/s
vus............................: 1000   min=702       max=1000
vus_max........................: 1000   min=702       max=1000
```
#### Results **copy-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run --rm -i grafana/k6 run --quiet - <tools/k6/copy-test.js
```
```text
data_received..................: 400 kB 15 kB/s
data_sent......................: 288 kB 11 kB/s
dropped_iterations.............: 901    32.738065/s
http_req_blocked...............: avg=676.14µs p(99)=2.84ms  p(99.9)=42.75ms  p(99.99)=66.66ms  max=69.91ms
http_req_connecting............: avg=636.78µs p(99)=2.66ms  p(99.9)=42.64ms  p(99.99)=66.56ms  max=69.8ms
http_req_duration..............: avg=4.44s    p(99)=8.11s   p(99.9)=8.26s    p(99.99)=8.42s    max=8.45s
  { expected_response:true }...: avg=4.44s    p(99)=8.11s   p(99.9)=8.26s    p(99.99)=8.42s    max=8.45s
http_req_failed................: 0.00%  ✓ 0          ✗ 3101
http_req_receiving.............: avg=172.83µs p(99)=2.37ms  p(99.9)=7.12ms   p(99.99)=7.22ms   max=7.25ms
http_req_sending...............: avg=54.97µs  p(99)=145.9µs p(99.9)=495.77µs p(99.99)=575.79µs max=593.5µs
http_req_tls_handshaking.......: avg=0s       p(99)=0s      p(99.9)=0s       p(99.99)=0s       max=0s
http_req_waiting...............: avg=4.44s    p(99)=8.11s   p(99.9)=8.26s    p(99.99)=8.42s    max=8.45s
http_reqs......................: 3101   112.675626/s
iteration_duration.............: avg=4.44s    p(99)=8.11s   p(99.9)=8.26s    p(99.99)=8.42s    max=8.45s
iterations.....................: 3100   112.639291/s
vus............................: 998    min=109      max=998
vus_max........................: 998    min=109      max=998
```

#### Results **download-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run --rm -i grafana/k6 run --quiet - <tools/k6/download-test.js
```
```text
data_received..................: 115 kB 4.6 kB/s
data_sent......................: 88 kB  3.5 kB/s
dropped_iterations.............: 113    4.536711/s
http_req_blocked...............: avg=459.4µs p(99)=2.01ms   p(99.9)=34.81ms  p(99.99)=35.67ms  max=35.77ms
http_req_connecting............: avg=431.7µs p(99)=1.86ms   p(99.9)=34.73ms  p(99.99)=35.6ms   max=35.7ms
http_req_duration..............: avg=3.54s   p(99)=5.54s    p(99.9)=5.58s    p(99.99)=5.61s    max=5.61s
  { expected_response:true }...: avg=3.54s   p(99)=5.54s    p(99.9)=5.58s    p(99.99)=5.61s    max=5.61s
http_req_failed................: 0.00%  ✓ 0         ✗ 889
http_req_receiving.............: avg=88.46µs p(99)=223.76µs p(99.9)=339.56µs p(99.99)=518.42µs max=538.29µs
http_req_sending...............: avg=43.15µs p(99)=117.96µs p(99.9)=130.65µs p(99.99)=133.21µs max=133.5µs
http_req_tls_handshaking.......: avg=0s      p(99)=0s       p(99.9)=0s       p(99.99)=0s       max=0s
http_req_waiting...............: avg=3.54s   p(99)=5.54s    p(99.9)=5.58s    p(99.99)=5.61s    max=5.61s
http_reqs......................: 889    35.691468/s
iteration_duration.............: avg=3.54s   p(99)=5.54s    p(99.9)=5.58s    p(99.99)=5.61s    max=5.61s
iterations.....................: 888    35.65132/s
vus............................: 213    min=100     max=213
vus_max........................: 213    min=100     max=213
```

### Webflux
```bash
./gradlew :spring-webflux:bootRun
```
#### Results **db-read-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run --rm -i grafana/k6 run --quiet - <tools/k6/db-read-test.js
```
```text
data_received..................: 15 MB  744 kB/s
data_sent......................: 4.1 MB 197 kB/s
dropped_iterations.............: 56418  2737.18422/s
http_req_blocked...............: avg=1.62ms   p(99)=3.22ms   p(99.9)=513.6ms  p(99.99)=520ms    max=549.79ms
http_req_connecting............: avg=1.61ms   p(99)=3.07ms   p(99.9)=513.5ms  p(99.99)=519.9ms  max=549.49ms
http_req_duration..............: avg=449.39ms p(99)=897.55ms p(99.9)=1.1s     p(99.99)=1.22s    max=1.23s
  { expected_response:true }...: avg=449.39ms p(99)=897.55ms p(99.9)=1.1s     p(99.99)=1.22s    max=1.23s
http_req_failed................: 0.00%  ✓ 0           ✗ 43584
http_req_receiving.............: avg=57.25µs  p(99)=126.4µs  p(99.9)=204.78µs p(99.99)=774.57µs max=1.02ms
http_req_sending...............: avg=22.76µs  p(99)=86.21µs  p(99.9)=353.85µs p(99.99)=1.13ms   max=1.66ms
http_req_tls_handshaking.......: avg=0s       p(99)=0s       p(99.9)=0s       p(99.99)=0s       max=0s
http_req_waiting...............: avg=449.31ms p(99)=897.43ms p(99.9)=1.1s     p(99.99)=1.22s    max=1.23s
http_reqs......................: 43584  2114.527935/s
iteration_duration.............: avg=451.08ms p(99)=963.7ms  p(99.9)=1.4s     p(99.99)=1.41s    max=1.42s
iterations.....................: 43583  2114.479419/s
vus............................: 1000   min=712       max=1000
vus_max........................: 1000   min=712       max=1000
```
#### Results **copy-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run --rm -i grafana/k6 run --quiet - <tools/k6/copy-test.js
```
```text
data_received..................: 238 kB 8.6 kB/s
data_sent......................: 295 kB 11 kB/s
dropped_iterations.............: 830    29.839818/s
http_req_blocked...............: avg=708.48µs p(99)=3.45ms   p(99.9)=50.72ms  p(99.99)=64.75ms  max=70.34ms
http_req_connecting............: avg=672.42µs p(99)=2.97ms   p(99.9)=50.64ms  p(99.99)=64.68ms  max=70.27ms
http_req_duration..............: avg=4.21s    p(99)=7.81s    p(99.9)=7.87s    p(99.99)=7.88s    max=7.88s
  { expected_response:true }...: avg=4.21s    p(99)=7.81s    p(99.9)=7.87s    p(99.99)=7.88s    max=7.88s
http_req_failed................: 0.00%  ✓ 0          ✗ 3171
http_req_receiving.............: avg=91.31µs  p(99)=168.43µs p(99.9)=257.86µs p(99.99)=655.16µs max=732.2µs
http_req_sending...............: avg=53.02µs  p(99)=147.33µs p(99.9)=443.42µs p(99.99)=498.14µs max=500.4µs
http_req_tls_handshaking.......: avg=0s       p(99)=0s       p(99.9)=0s       p(99.99)=0s       max=0s
http_req_waiting...............: avg=4.21s    p(99)=7.81s    p(99.9)=7.87s    p(99.99)=7.88s    max=7.88s
http_reqs......................: 3171   114.002484/s
iteration_duration.............: avg=4.21s    p(99)=7.81s    p(99.9)=7.87s    p(99.99)=7.88s    max=7.88s
iterations.....................: 3170   113.966532/s
vus............................: 927    min=100      max=927
vus_max........................: 927    min=100      max=927
```

#### Results **download-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run --rm -i grafana/k6 run --quiet - <tools/k6/download-test.js
```
```text
data_received..................: 73 kB 3.6 kB/s
data_sent......................: 96 kB 4.7 kB/s
dropped_iterations.............: 31    1.515182/s
http_req_blocked...............: avg=338.51µs p(99)=1.94ms   p(99.9)=39.4ms   p(99.99)=48.09ms  max=49.06ms
http_req_connecting............: avg=319.56µs p(99)=1.81ms   p(99.9)=39.31ms  p(99.99)=48ms     max=48.97ms
http_req_duration..............: avg=814.1ms  p(99)=4.58s    p(99.9)=4.79s    p(99.99)=4.79s    max=4.79s
  { expected_response:true }...: avg=814.1ms  p(99)=4.58s    p(99.9)=4.79s    p(99.99)=4.79s    max=4.79s
http_req_failed................: 0.00% ✓ 0         ✗ 971
http_req_receiving.............: avg=75.35µs  p(99)=150.53µs p(99.9)=204.77µs p(99.99)=369.85µs max=388.2µs
http_req_sending...............: avg=41.08µs  p(99)=119.23µs p(99.9)=483.6µs  p(99.99)=518.61µs max=522.5µs
http_req_tls_handshaking.......: avg=0s       p(99)=0s       p(99.9)=0s       p(99.99)=0s       max=0s
http_req_waiting...............: avg=813.99ms p(99)=4.58s    p(99.9)=4.79s    p(99.99)=4.79s    max=4.79s
http_reqs......................: 971   47.459398/s
iteration_duration.............: avg=814.54ms p(99)=4.58s    p(99.9)=4.79s    p(99.99)=4.79s    max=4.79s
iterations.....................: 970   47.410521/s
vus............................: 131   min=100     max=131
vus_max........................: 131   min=100     max=131
```
