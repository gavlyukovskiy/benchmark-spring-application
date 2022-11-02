import http from 'k6/http';

export const options = {
  scenarios: {
    main: {
      executor: 'constant-arrival-rate',
      rate: 50,
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
  http.get(`http://${host}:8080/download`);
}
