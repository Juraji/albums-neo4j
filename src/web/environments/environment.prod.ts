export const environment = {
  production: true,
  apiBaseUri: `${window.origin}/api`,
  pagination: {
    defaultPageSize: 50,
    pageSizeOptions: [50, 100, 200, 500],
  },
  uploads: {
    maxConcurrent: 6,
    retryWhenStatus: [429, 502, 503, 504],
    retryDelay: 800,
    maxRetries: 50
  }
};
