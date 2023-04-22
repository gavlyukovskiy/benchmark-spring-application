import { check, sleep } from 'k6';
import http from 'k6/http';
import { textSummary } from "https://jslib.k6.io/k6-summary/0.0.2/index.js";

export const options = {
  scenarios: {
    bootstrap: {
      executor: 'constant-arrival-rate',
      rate: 2000,
      timeUnit: '1s',
      duration: '5s',
      preAllocatedVUs: 100,
      maxVUs: 1000,
      startTime: '0s',
    },
    main: {
      executor: 'constant-arrival-rate',
      rate: 10000,
      timeUnit: '1s',
      duration: '60s',
      preAllocatedVUs: 100,
      maxVUs: 1000,
      startTime: '5s',
    },
    overload: {
      executor: 'constant-arrival-rate',
      rate: 30000, // maximum application can handle inside docker is around 24-25k rps
      timeUnit: '1s',
      duration: '10s',
      preAllocatedVUs: 100,
      maxVUs: 1000,
      startTime: '65s',
    },
    teardown: {
      executor: 'constant-arrival-rate',
      rate: 2000,
      timeUnit: '1s',
      duration: '5s',
      preAllocatedVUs: 100,
      maxVUs: 1000,
      startTime: '75s',
    },
  },
  summaryTrendStats: ['avg', 'p(99)', 'p(99.9)', 'p(99.99)', 'max'],
};

const host = __ENV.TARGET_HOST || 'host.docker.internal'

export function setup() {
  while (true) {
    const response = http.get(`http://${host}:8080/hello`, { timeout: 5000 });
    if (response.status === 200) {
      console.log("Service is ready")
      return
    }
    console.log("Service hasn't started yet, sleeping for 1s")
    sleep(1)
  }
}

export default function () {
  const response = http.get(`http://${host}:8080/db`);
  check(response, { 'is status 200': (r) => r.status === 200 });
}

export function handleSummary(data) {
  let keys = ['dropped_iterations', 'iteration_duration', 'iterations', 'data_received', 'data_sent', 'checks'];
  for (const key in data.metrics) {
    if (!keys.includes(key)) delete data.metrics[key]
  }
  return {
    stdout: textSummary(data, { indent: ' ', enableColors: false }),
  };
}
