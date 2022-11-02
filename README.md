## Running load tests

1. Data bootstrap:
    ```bash
    docker-compose up -d postgres
    ./gradlew :tools:bootstrap --args="postgres"
    ```
2. Run application (see different options below)
3. Run **db-read-test** (reading a random item from postgres)
    ```bash
    docker run --rm -i grafana/k6 run - [-e TARGET_HOST=host.docker.internal] [--quiet] <tools/k6/db-read-test.js
    ```
4. Run **copy-test** (writing a 50MB file to a disk)
    ```bash
    docker run --rm -i grafana/k6 run - [-e TARGET_HOST=host.docker.internal] [--quiet] <tools/k6/copy-test.js
    ```
5. Run **download-test** test (downloading two files consecutively from an external webservice)
    ```bash
    docker run --rm -i grafana/k6 run - [-e TARGET_HOST=host.docker.internal] [--quiet] <tools/k6/download-test.js
    ```

### Web + jOOQ
```bash
./gradlew :spring-web:bootRun --args="--spring.profiles.active=jooq"
```
#### Results **db-read-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run --rm -i grafana/k6 run - --quiet <tools/k6/db-read-test.js
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
docker run --rm -i grafana/k6 run - --quiet <tools/k6/copy-test.js
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
docker run --rm -i grafana/k6 run - --quiet <tools/k6/download-test.js
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

### Web + jOOQ + Loom
```bash
./gradlew :spring-web:bootRun --args="--spring.profiles.active=jooq,loom"
```
#### Results **db-read-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run --rm -i grafana/k6 run - --quiet <tools/k6/db-read-test.js
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
docker run --rm -i grafana/k6 run - --quiet <tools/k6/copy-test.js
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
docker run --rm -i grafana/k6 run - --quiet <tools/k6/download-test.js
```
```text
data_received..................: 109 kB 3.7 kB/s
data_sent......................: 83 kB  2.9 kB/s
dropped_iterations.............: 161    5.516152/s
http_req_blocked...............: avg=650.04µs p(99)=2.86ms   p(99.9)=32.93ms  p(99.99)=33.2ms   max=33.23ms
http_req_connecting............: avg=614.45µs p(99)=2.41ms   p(99.9)=32.84ms  p(99.99)=33.1ms   max=33.13ms
http_req_duration..............: avg=5.05s    p(99)=9.34s    p(99.9)=9.76s    p(99.99)=9.78s    max=9.78s
  { expected_response:true }...: avg=5.05s    p(99)=9.34s    p(99.9)=9.76s    p(99.99)=9.78s    max=9.78s
http_req_failed................: 0.00%  ✓ 0         ✗ 841
http_req_receiving.............: avg=89.53µs  p(99)=304.8µs  p(99.9)=426.47µs p(99.99)=472.66µs max=477.8µs
http_req_sending...............: avg=47µs     p(99)=127.88µs p(99.9)=262.23µs p(99.99)=458.49µs max=480.3µs
http_req_tls_handshaking.......: avg=0s       p(99)=0s       p(99.9)=0s       p(99.99)=0s       max=0s
http_req_waiting...............: avg=5.05s    p(99)=9.34s    p(99.9)=9.76s    p(99.99)=9.78s    max=9.78s
http_reqs......................: 841    28.814185/s
iteration_duration.............: avg=5.05s    p(99)=9.34s    p(99.9)=9.76s    p(99.99)=9.78s    max=9.79s
iterations.....................: 840    28.779923/s
vus............................: 261    min=100     max=261
vus_max........................: 261    min=100     max=261
```

### Webflux + jOOQ
```bash
./gradlew :spring-webflux:bootRun --args="--spring.profiles.active=jooq"
```
#### Results **db-read-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run --rm -i grafana/k6 run - --quiet <tools/k6/db-read-test.js
```
```text
data_received..................: 15 MB  484 kB/s
data_sent......................: 3.9 MB 128 kB/s
dropped_iterations.............: 57778  1891.854482/s
http_req_blocked...............: avg=1.55ms   p(99)=3.53ms   p(99.9)=515.31ms p(99.99)=533.18ms max=535.48ms
http_req_connecting............: avg=1.55ms   p(99)=3.44ms   p(99.9)=515.18ms p(99.99)=533.1ms  max=535.43ms
http_req_duration..............: avg=407.15ms p(99)=918.85ms p(99.9)=1.15s    p(99.99)=1.24s    max=1.25s
  { expected_response:true }...: avg=408.3ms  p(99)=918.89ms p(99.9)=1.15s    p(99.99)=1.24s    max=1.25s
http_req_failed................: 0.28%  ✓ 119         ✗ 42105
http_req_receiving.............: avg=57.7µs   p(99)=130.97µs p(99.9)=211.67µs p(99.99)=551.26µs max=1.02ms
http_req_sending...............: avg=24.28µs  p(99)=90µs     p(99.9)=587.16µs p(99.99)=2.94ms   max=19.25ms
http_req_tls_handshaking.......: avg=0s       p(99)=0s       p(99.9)=0s       p(99.99)=0s       max=0s
http_req_waiting...............: avg=407.07ms p(99)=918.67ms p(99.9)=1.15s    p(99.99)=1.24s    max=1.25s
http_reqs......................: 42224  1382.561938/s
iteration_duration.............: avg=493.32ms p(99)=958.46ms p(99.9)=30s      p(99.99)=30s      max=30s
iterations.....................: 42223  1382.529194/s
vus............................: 1000   min=708       max=1000
vus_max........................: 1000   min=708       max=1000
```
#### Results **copy-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run --rm -i grafana/k6 run - --quiet <tools/k6/copy-test.js
```
```text
data_received..................: 219 kB 5.4 kB/s
data_sent......................: 272 kB 6.7 kB/s
dropped_iterations.............: 822    20.384722/s
http_req_blocked...............: avg=498.16µs p(99)=2.09ms   p(99.9)=42.63ms  p(99.99)=67.31ms  max=73.56ms
http_req_connecting............: avg=472.37µs p(99)=1.94ms   p(99.9)=42.56ms  p(99.99)=67.24ms  max=73.49ms
http_req_duration..............: avg=2.57s    p(99)=5.53s    p(99.9)=5.57s    p(99.99)=5.58s    max=5.59s
  { expected_response:true }...: avg=2.8s     p(99)=5.53s    p(99.9)=5.57s    p(99.99)=5.59s    max=5.59s
http_req_failed................: 8.02%  ✓ 255       ✗ 2924
http_req_receiving.............: avg=80.85µs  p(99)=161.2µs  p(99.9)=442.17µs p(99.99)=599.64µs max=634µs
http_req_sending...............: avg=43.6µs   p(99)=133.54µs p(99.9)=195.16µs p(99.99)=367.06µs max=421.6µs
http_req_tls_handshaking.......: avg=0s       p(99)=0s       p(99.9)=0s       p(99.99)=0s       max=0s
http_req_waiting...............: avg=2.57s    p(99)=5.53s    p(99.9)=5.57s    p(99.99)=5.58s    max=5.59s
http_reqs......................: 3179   78.835806/s
iteration_duration.............: avg=4.98s    p(99)=30s      p(99.9)=30s      p(99.99)=30s      max=30s
iterations.....................: 3178   78.811007/s
vus............................: 921    min=100     max=921
vus_max........................: 921    min=100     max=921
```

#### Results **download-test** (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
```bash
docker run --rm -i grafana/k6 run - --quiet <tools/k6/download-test.js
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
