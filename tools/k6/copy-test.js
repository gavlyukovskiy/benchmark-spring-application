import { check } from 'k6';
import http from 'k6/http';
import { textSummary } from "https://jslib.k6.io/k6-summary/0.0.2/index.js";

export const options = {
  scenarios: {
    main: {
      executor: 'constant-arrival-rate',
      rate: 200,
      timeUnit: '1s',
      duration: '20s',
      preAllocatedVUs: 100,
      maxVUs: 1000,
    },
  },
  summaryTrendStats: ['avg', 'p(99)', 'p(99.9)', 'p(99.99)', 'max'],
};

const host = __ENV.TARGET_HOST || 'host.docker.internal'

export function setup() {
  http.get(`http://${host}:8080/hello`);
}

export default function () {
  const response = http.get(`http://${host}:8080/cp`);
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
