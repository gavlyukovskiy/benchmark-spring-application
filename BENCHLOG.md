### 2023-12-01

Spring Boot 3.2.0
Java 21
Kotlin 1.9.21

#### Web, **db-read-test** (MacBook Pro M1, 2021, 32GB):

     checks...............: 100.00% ✓ 1201985     ✗ 0
     data_received........: 487 MB  16 MB/s
     data_sent............: 101 MB  3.3 MB/s
     iteration_duration...: avg=12.39ms p(99)=69.61ms p(99.9)=130.36ms p(99.99)=1.03s max=1.43s
     iterations...........: 1201985 39605.71224/s

#### Web + Loom, **db-read-test** (MacBook Pro M1, 2021, 32GB):

     checks...............: 99.70% ✓ 300220       ✗ 895
     data_received........: 122 MB 4.1 MB/s
     data_sent............: 25 MB  821 kB/s
     iteration_duration...: avg=49.84ms p(99)=114.17ms p(99.9)=166.84ms p(99.99)=234.91ms max=304.61ms
     iterations...........: 301115 10020.746004/s

#### Webflux, **db-read-test** (MacBook Pro M1, 2021, 32GB):

     checks...............: 99.83% ✓ 276616      ✗ 470
     data_received........: 80 MB  2.7 MB/s
     data_sent............: 23 MB  756 kB/s
     iteration_duration...: avg=54.16ms p(99)=67.4ms p(99.9)=75.75ms p(99.99)=85.87ms max=152.71ms
     iterations...........: 277086 9220.847596/s

#### Web, **copy-test** (MacBook Pro M1, 2021, 32GB):

     checks...............: 100.00% ✓ 2215      ✗ 0
     data_received........: 291 kB  7.5 kB/s
     data_sent............: 186 kB  4.8 kB/s
     iteration_duration...: avg=7.7s p(99)=9.79s p(99.9)=10.45s p(99.99)=10.45s max=10.45s
     iterations...........: 2215    56.905237/s

#### Web + Loom, **copy-test** (MacBook Pro M1, 2021, 32GB):

     checks...............: 85.03% ✓ 2802      ✗ 493
     data_received........: 368 kB 11 kB/s
     data_sent............: 263 kB 7.5 kB/s
     iteration_duration...: avg=4.93s p(99)=7.36s p(99.9)=7.59s p(99.99)=7.66s max=7.68s
     iterations...........: 3295   93.702682/s

#### Webflux, **copy-test** (MacBook Pro M1, 2021, 32GB):

     checks...............: 82.83% ✓ 3065       ✗ 635
     data_received........: 236 kB 6.6 kB/s
     data_sent............: 294 kB 8.2 kB/s
     iteration_duration...: avg=4.44s p(99)=6.71s p(99.9)=6.77s p(99.99)=6.78s max=6.79s
     iterations...........: 3700   103.395009/s

### 2022-12-22

Spring Boot 3.1.0
Java 20 (Preview)
Kotlin 1.8.21

#### Web, **db-read-test** (MacBook Pro M1, 2021, 32GB):

     checks...............: 99.86% ✓ 316363       ✗ 423
     data_received........: 128 MB 4.2 MB/s
     data_sent............: 26 MB  858 kB/s
     iteration_duration...: avg=47.37ms p(99)=132.59ms p(99.9)=193.91ms p(99.99)=246.26ms max=356.28ms
     iterations...........: 316786 10468.147453/s

#### Web + Loom, **db-read-test** (MacBook Pro M1, 2021, 32GB):

     checks...............: 99.70% ✓ 300220       ✗ 895
     data_received........: 122 MB 4.1 MB/s
     data_sent............: 25 MB  821 kB/s
     iteration_duration...: avg=49.84ms p(99)=114.17ms p(99.9)=166.84ms p(99.99)=234.91ms max=304.61ms
     iterations...........: 301115 10020.746004/s

#### Webflux, **db-read-test** (MacBook Pro M1, 2021, 32GB):

     checks...............: 99.83% ✓ 276616      ✗ 470
     data_received........: 80 MB  2.7 MB/s
     data_sent............: 23 MB  756 kB/s
     iteration_duration...: avg=54.16ms p(99)=67.4ms p(99.9)=75.75ms p(99.99)=85.87ms max=152.71ms
     iterations...........: 277086 9220.847596/s

#### Web, **copy-test** (MacBook Pro M1, 2021, 32GB):

     checks...............: 84.68% ✓ 3412       ✗ 617
     data_received........: 447 kB 13 kB/s
     data_sent............: 321 kB 9.0 kB/s
     iteration_duration...: avg=4.07s p(99)=5.75s p(99.9)=5.8s p(99.99)=5.81s max=5.81s
     iterations...........: 4029   112.508125/s

#### Web + Loom, **copy-test** (MacBook Pro M1, 2021, 32GB):

     checks...............: 85.03% ✓ 2802      ✗ 493
     data_received........: 368 kB 11 kB/s
     data_sent............: 263 kB 7.5 kB/s
     iteration_duration...: avg=4.93s p(99)=7.36s p(99.9)=7.59s p(99.99)=7.66s max=7.68s
     iterations...........: 3295   93.702682/s

#### Webflux, **copy-test** (MacBook Pro M1, 2021, 32GB):

     checks...............: 82.83% ✓ 3065       ✗ 635
     data_received........: 236 kB 6.6 kB/s
     data_sent............: 294 kB 8.2 kB/s
     iteration_duration...: avg=4.44s p(99)=6.71s p(99.9)=6.77s p(99.99)=6.78s max=6.79s
     iterations...........: 3700   103.395009/s
